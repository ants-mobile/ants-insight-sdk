package ants.mobile.insight;

import android.app.Application;

import ants.mobile.ants_insight.InsightSDK;

/**
 * Created by luonglc on 15/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        InsightSDK.setInstance(this);
    }
}
