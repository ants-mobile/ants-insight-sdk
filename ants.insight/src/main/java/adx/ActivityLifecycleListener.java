package adx;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {
    @Nullable
    private static ActivityLifecycleListener instance;
    @Nullable
    private static ComponentCallbacks configuration;

    public static void registerActivityLifecycleCallbacks(@NonNull final Application application) {
        // Activity lifecycle listener setup
        if (instance == null) {
            instance = new ActivityLifecycleListener();
            application.registerActivityLifecycleCallbacks(instance);
        }

        // Configuration change listener setup
        if (configuration == null) {
            configuration = new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    ActivityLifecycleHandler.onConfigurationChanged(newConfig);
                }

                @Override
                public void onLowMemory() {

                }
            };
            application.registerComponentCallbacks(configuration);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityLifecycleHandler.onActivityCreated(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ActivityLifecycleHandler.onActivityStarted(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ActivityLifecycleHandler.onActivityResumed(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ActivityLifecycleHandler.onActivityPaused(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ActivityLifecycleHandler.onActivityStopped(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityLifecycleHandler.onActivityDestroyed(activity);
    }
}
