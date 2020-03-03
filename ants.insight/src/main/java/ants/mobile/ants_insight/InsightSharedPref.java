package ants.mobile.ants_insight;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import static android.content.Context.MODE_PRIVATE;

public class InsightSharedPref {

    private static final String INSIGHT_SHARED_PREF = "insight_shared_pref";
    private static final String KEY_PORTAL_ID = "portal_id";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String KEY_PROPERTY_ID = "prop_id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_IS_FIRST_INSTALL_APP = "is_first_install_app";
    private static final String KEY_ONE_SIGNAL_ID = "oneSignalId";
    private static final String UID = "uid";
    private static final String INDEX_FILE_PATH = "path";
    private static final String ADS_IS_SHOWING = "is_showing";

    private static SharedPreferences.Editor getSharedPreferenceEditor(Context context) {
        return context.getSharedPreferences(INSIGHT_SHARED_PREF, MODE_PRIVATE).edit();
    }

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(INSIGHT_SHARED_PREF, MODE_PRIVATE);
    }

    static void setPushNotificationId(Context mContext, String oneSignalId) {
        getSharedPreferenceEditor(mContext).putString(KEY_ONE_SIGNAL_ID, oneSignalId).apply();
    }

    public static String getPushNotificationId(Context mContext) {
        return getSharedPreference(mContext).getString(KEY_ONE_SIGNAL_ID, "3c80bd93-ce53-4180-b423-8ae62b014f04");
    }

    static void setPortalId(Context mContext, String portalId) {
        getSharedPreferenceEditor(mContext).putString(KEY_PORTAL_ID, portalId).apply();
    }

    static String getPortalId(Context mContext) {
        return getSharedPreference(mContext).getString(KEY_PORTAL_ID, "");
    }

    public static void setServerUrl(Context mContext, String domain) {
        getSharedPreferenceEditor(mContext).putString(KEY_SERVER_URL, domain).apply();
    }

    public static String getServerUrl(Context mContext) {
        return getSharedPreference(mContext).getString(KEY_SERVER_URL, "");
    }

    static void setPropertyId(Context mContext, String propertyId) {
        getSharedPreferenceEditor(mContext).putString(KEY_PROPERTY_ID, propertyId).apply();
    }

    static String getPropertyId(Context mContext) {
        return getSharedPreference(mContext).getString(KEY_PROPERTY_ID, "");
    }

    public static String getLongitude(Context mContext) {
        return getSharedPreference(mContext).getString(KEY_LONGITUDE, "");
    }

    public static String getLatitude(Context mContext) {
        return getSharedPreference(mContext).getString(KEY_LATITUDE, "");
    }

    public static void saveLocation(Context context, Location location) {
        getSharedPreferenceEditor(context).putString(KEY_LATITUDE, String.valueOf(location.getLatitude())).apply();
        getSharedPreferenceEditor(context).putString(KEY_LONGITUDE, String.valueOf(location.getLongitude())).apply();
    }

    public static void setIsFirstInstallApp(Context context, Boolean isFirst) {
        getSharedPreferenceEditor(context).putBoolean(KEY_IS_FIRST_INSTALL_APP, isFirst).apply();
    }

    public static boolean getIsFirstInstallApp(Context context) {
        return getSharedPreference(context).getBoolean(KEY_IS_FIRST_INSTALL_APP, true);
    }

    static void setUID(Context context, String uid) {
        getSharedPreferenceEditor(context).putString(UID, uid).apply();
    }

    public static String getUID(Context context) {
        return getSharedPreference(context).getString(UID, "");
    }

    public static void setIndexFilePath(Context context, String path) {
        getSharedPreferenceEditor(context).putString(INDEX_FILE_PATH, path).apply();
    }

    public static String getIndexFile(Context context) {
        return getSharedPreference(context).getString(INDEX_FILE_PATH, "");
    }

    public static void adsIsShowing(Context context, Boolean isShowing) {
        getSharedPreferenceEditor(context).putBoolean(ADS_IS_SHOWING, isShowing).apply();
    }

    static boolean adsIsShowing(Context context) {
        return getSharedPreference(context).getBoolean(ADS_IS_SHOWING, false);
    }
}
