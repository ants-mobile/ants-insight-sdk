package ants.mobile.ants_insight;

import android.content.Context;
import android.content.SharedPreferences;

import ants.mobile.ants_insight.Constants.Constants;

public class InsightSharedPref {

    public static void savePreference(String key, String value) {
        SharedPreferences.Editor editor = InsightSDK.getInstance().getSharedPreferences(
                Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void savePreference(String key, Boolean value) {
        SharedPreferences.Editor editor = InsightSDK.getInstance().getSharedPreferences(
                Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void savePreference(String key, long value) {
        SharedPreferences.Editor editor = InsightSDK.getInstance().getSharedPreferences(
                Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static String getStringValue(String key) {
        SharedPreferences settings = InsightSDK.getInstance().getSharedPreferences(
                Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, Constants.BLANK);
    }

    public static boolean getBooleanValue(String key) {
        SharedPreferences settings = InsightSDK.getInstance().getSharedPreferences(
                Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, true);
    }

}
