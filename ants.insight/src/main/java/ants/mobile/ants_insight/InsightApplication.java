package ants.mobile.ants_insight;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by luonglc on 26/5/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class InsightApplication extends Application {
    protected static volatile InsightApplication mInstance = null;
    private Scheduler mScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
    }

    public static InsightApplication getInstance() {
        if (mInstance != null)
            return mInstance;
        return null;
    }

    public static void setInstance(InsightApplication mInstance) {
        InsightApplication.mInstance = mInstance;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
