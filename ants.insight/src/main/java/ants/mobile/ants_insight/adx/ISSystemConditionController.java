package ants.mobile.ants_insight.adx;

import java.lang.ref.WeakReference;

public class ISSystemConditionController {

    interface OSSystemConditionObserver {
        // Alerts the systemConditionObserver that a system condition has being activated
        void messageTriggerConditionChanged();
    }

    private static final String TAG = ISSystemConditionController.class.getCanonicalName();
    private final OSSystemConditionObserver systemConditionObserver;

    ISSystemConditionController(OSSystemConditionObserver systemConditionObserver) {
        this.systemConditionObserver = systemConditionObserver;
    }

    boolean systemConditionsAvailable() {
        if (ActivityLifecycleHandler.curActivity == null) {
            return false;
        }
        boolean keyboardUp = Utils.isKeyboardUp(new WeakReference<>(ActivityLifecycleHandler.curActivity));
        if (keyboardUp) {
            ActivityLifecycleHandler.setSystemConditionObserver(TAG, systemConditionObserver);
        }
        return !keyboardUp;
    }
}
