
package ants.mobile.ants_insight.adx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.multidex.BuildConfig;

import android.util.Log;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ActivityLifecycleHandler {

    private static boolean nextResumeIsFirstActivity;

    abstract static class ActivityAvailableListener {
        void available(@NonNull Activity activity) {
        }

        void stopped(WeakReference<Activity> reference) {
        }
    }

    private static Map<String, ActivityAvailableListener> sActivityAvailableListeners = new ConcurrentHashMap<>();
    private static Map<String, ISSystemConditionController.OSSystemConditionObserver> sSystemConditionObservers = new ConcurrentHashMap<>();
    private static Map<String, KeyboardListener> sKeyboardListeners = new ConcurrentHashMap<>();
    static FocusHandlerThread focusHandlerThread = new FocusHandlerThread();
    @SuppressLint("StaticFieldLeak")
    static Activity curActivity;

    static void setSystemConditionObserver(String key, ISSystemConditionController.OSSystemConditionObserver systemConditionObserver) {
        if (curActivity != null) {
            ViewTreeObserver treeObserver = curActivity.getWindow().getDecorView().getViewTreeObserver();
            KeyboardListener keyboardListener = new KeyboardListener(systemConditionObserver, key);
            treeObserver.addOnGlobalLayoutListener(keyboardListener);
            sKeyboardListeners.put(key, keyboardListener);
        }
        sSystemConditionObservers.put(key, systemConditionObserver);
    }

    static void setActivityAvailableListener(String key, ActivityAvailableListener activityAvailableListener) {
        sActivityAvailableListeners.put(key, activityAvailableListener);
        if (curActivity != null)
            activityAvailableListener.available(curActivity);
    }

    private static void removeSystemConditionObserver(String key) {
        sKeyboardListeners.remove(key);
        sSystemConditionObservers.remove(key);
    }

    static void removeActivityAvailableListener(String key) {
        sActivityAvailableListeners.remove(key);
    }

    private static void setCurActivity(Activity activity) {
        curActivity = activity;
        for (Map.Entry<String, ActivityAvailableListener> entry : sActivityAvailableListeners.entrySet()) {
            entry.getValue().available(curActivity);
        }

        try {
            ViewTreeObserver treeObserver = curActivity.getWindow().getDecorView().getViewTreeObserver();
            for (Map.Entry<String, ISSystemConditionController.OSSystemConditionObserver> entry : sSystemConditionObservers.entrySet()) {
                KeyboardListener keyboardListener = new KeyboardListener(entry.getValue(), entry.getKey());
                treeObserver.addOnGlobalLayoutListener(keyboardListener);
                sKeyboardListeners.put(entry.getKey(), keyboardListener);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    static void onConfigurationChanged(Configuration newConfig) {
        // If Activity contains the configChanges orientation flag, re-create the view this way
        if (curActivity != null && Utils.hasConfigChangeFlag(curActivity, ActivityInfo.CONFIG_ORIENTATION)) {
            logOrientationChange(newConfig.orientation);
            onOrientationChanged();
        }
    }

    static void onActivityCreated(Activity activity) {

    }

    static void onActivityStarted(Activity activity) {
    }

    static void onActivityResumed(Activity activity) {
        setCurActivity(activity);
        logCurActivity();
        handleFocus();
    }

    static void onActivityPaused(Activity activity) {
        if (activity == curActivity) {
            curActivity = null;
            handleLostFocus();
        }

        logCurActivity();
    }

    static void onActivityStopped(Activity activity) {

        if (activity == curActivity) {
            curActivity = null;
            handleLostFocus();
        }

        for (Map.Entry<String, ActivityAvailableListener> entry : sActivityAvailableListeners.entrySet()) {
            entry.getValue().stopped(new WeakReference<>(activity));
        }

        logCurActivity();
    }

    static void onActivityDestroyed(Activity activity) {
        sKeyboardListeners.clear();

        if (activity == curActivity) {
            curActivity = null;
            handleLostFocus();
        }
        WebViewManager.isShowingAds = false;
        logCurActivity();
    }

    static private void logCurActivity() {
        if (BuildConfig.DEBUG)
            Log.d("InsightDebug", "curActivity is NOW: " + (curActivity != null ? "" + curActivity.getClass().getName() + ":" + curActivity : "null"));
    }

    private static void logOrientationChange(int orientation) {
    }

    /**
     * Takes pieces from onActivityResumed and onActivityStopped to recreate the view when the
     * phones orientation is changed from manual detection using the onConfigurationChanged callback
     * This fix was originally implemented for In App Messages not being re-shown when orientation
     * was changed on wrapper SDK apps
     */
    private static void onOrientationChanged() {
        // Remove view
        handleLostFocus();
        for (Map.Entry<String, ActivityAvailableListener> entry : sActivityAvailableListeners.entrySet()) {
            entry.getValue().stopped(new WeakReference<>(curActivity));
        }

        // Show view
        for (Map.Entry<String, ActivityAvailableListener> entry : sActivityAvailableListeners.entrySet()) {
            entry.getValue().available(curActivity);
        }

        ViewTreeObserver treeObserver = curActivity.getWindow().getDecorView().getViewTreeObserver();
        for (Map.Entry<String, ISSystemConditionController.OSSystemConditionObserver> entry : sSystemConditionObservers.entrySet()) {
            KeyboardListener keyboardListener = new KeyboardListener(entry.getValue(), entry.getKey());
            treeObserver.addOnGlobalLayoutListener(keyboardListener);
            sKeyboardListeners.put(entry.getKey(), keyboardListener);
        }
        handleFocus();
    }

    static private void handleLostFocus() {
        focusHandlerThread.runRunnable(new AppFocusRunnable());
    }

    static private void handleFocus() {
        if (focusHandlerThread.hasBackgrounded() || nextResumeIsFirstActivity) {
            nextResumeIsFirstActivity = false;
            focusHandlerThread.resetBackgroundState();
        } else
            focusHandlerThread.stopScheduledRunnable();
    }

    static class FocusHandlerThread extends HandlerThread {
        private Handler mHandler;
        private AppFocusRunnable appFocusRunnable;

        FocusHandlerThread() {
            super("FocusHandlerThread");
            start();
            mHandler = new Handler(getLooper());
        }

        Looper getHandlerLooper() {
            return mHandler.getLooper();
        }

        void resetBackgroundState() {
            if (appFocusRunnable != null)
                appFocusRunnable.backgrounded = false;
        }

        void stopScheduledRunnable() {
            mHandler.removeCallbacksAndMessages(null);
        }

        void runRunnable(AppFocusRunnable runnable) {
            if (appFocusRunnable != null && appFocusRunnable.backgrounded && !appFocusRunnable.completed)
                return;

            appFocusRunnable = runnable;
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(runnable, 2000);
        }

        boolean hasBackgrounded() {
            return appFocusRunnable != null && appFocusRunnable.backgrounded;
        }
    }

    private static class AppFocusRunnable implements Runnable {
        private boolean backgrounded, completed;

        public void run() {
            if (curActivity != null)
                return;

            backgrounded = true;
            completed = true;
        }
    }

    private static class KeyboardListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private final ISSystemConditionController.OSSystemConditionObserver observer;
        private final String key;

        private KeyboardListener(ISSystemConditionController.OSSystemConditionObserver observer, String key) {
            this.observer = observer;
            this.key = key;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onGlobalLayout() {
            boolean keyboardUp = Utils.isKeyboardUp(new WeakReference<>(ActivityLifecycleHandler.curActivity));
            if (!keyboardUp) {
                if (curActivity != null) {
                    ViewTreeObserver treeObserver = curActivity.getWindow().getDecorView().getViewTreeObserver();
                    treeObserver.removeOnGlobalLayoutListener(KeyboardListener.this);
                }
                ActivityLifecycleHandler.removeSystemConditionObserver(key);
                observer.messageTriggerConditionChanged();
            }
        }
    }

}