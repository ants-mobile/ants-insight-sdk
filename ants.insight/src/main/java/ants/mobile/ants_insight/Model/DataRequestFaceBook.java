package ants.mobile.ants_insight.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ants.mobile.ants_insight.InsightSDK;
import ants.mobile.ants_insight.Insights;
import ants.mobile.ants_insight.adx.Utils;

/**
 * Created by luonglc on 15/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class DataRequestFaceBook {
    @SerializedName("event")
    @Expose
    private String event;
    @SerializedName("advertiser_id")
    @Expose
    private String advertiserId;
    @SerializedName("application_tracking_enabled")
    @Expose
    private String appTrackingEnabled;
    @SerializedName("advertiser_tracking_enabled")
    @Expose
    private String advertiserTrackingEnabled;
    @SerializedName("custom_events")
    @Expose
    private List<CustomEventsData> data;
    @SerializedName("bundle_id")
    @Expose
    private String packageName;

    public static class Builder {
        private String event;
        private String advertiserId;
        private String appTrackingEnabled;
        private String advertiserTrackingEnabled;
        private List<CustomEventsData> data;

        public Builder setEvent(String event) {
            this.event = event;
            return this;
        }

        public Builder setAppTrackingEnabled(boolean enabled) {
            this.appTrackingEnabled = enabled ? "1" : "0";
            return this;
        }

        public Builder setAdvertiserTrackingEnabled(boolean enabled) {
            this.advertiserTrackingEnabled = enabled ? "1" : "0";
            return this;
        }

        public Builder setData(List<CustomEventsData> data) {
            this.data = data;
            return this;
        }


        public DataRequestFaceBook build() {
            return new DataRequestFaceBook(this);
        }
    }

    @SuppressLint("HardwareIds")
    private DataRequestFaceBook(Builder builder) {
        this.advertiserId = builder.advertiserId;
        this.advertiserTrackingEnabled = builder.advertiserTrackingEnabled;
        this.appTrackingEnabled = builder.appTrackingEnabled;
        this.data = builder.data;
        this.packageName = String.valueOf(this.getClass().getPackage());
        this.event = builder.event;
        this.advertiserId = Settings.Secure.getString(InsightSDK.getInstance()
                .getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
