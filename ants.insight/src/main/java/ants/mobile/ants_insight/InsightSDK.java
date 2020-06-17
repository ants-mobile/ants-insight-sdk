package ants.mobile.ants_insight;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import androidx.annotation.NonNull;

/**
 * Created by luonglc on 15/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class InsightSDK extends Application {
    protected static volatile Application mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getInstance() {
        if (mInstance != null)
            return mInstance;
        return null;
    }

    public static void setInstance(Application mInstance) {
        InsightSDK.mInstance = mInstance;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
