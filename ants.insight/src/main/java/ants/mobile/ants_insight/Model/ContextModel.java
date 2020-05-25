package ants.mobile.ants_insight.Model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.multidex.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import adx.Utils;

import ants.mobile.ants_insight.Constants.Constants;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class ContextModel {
    private AppInfo appInfo;
    private SdkInfo sdkInfo;
    private DeviceInfo deviceInfo;
    private String navigation;
    private Context mContext;
    private Screen screen;
    private Network network;
    private Campaign campaign;
    private Os os;

    public ContextModel(Context context) {
        this.mContext = context;
        appInfo = new AppInfo(context);
        deviceInfo = new DeviceInfo(context);
        screen = new Screen(context);
        sdkInfo = new SdkInfo();
        this.navigation = context.getClass().getSimpleName();
        network = new Network();
        os = new Os();
    }

    public ContextModel(Context mContext, Campaign campaign) {
        this.mContext = mContext;
        this.campaign = campaign;
    }

    public JSONObject getContextModel() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("app", appInfo.getDataAppInfo());
            jsonObject.putOpt("sdk", sdkInfo.getSdkInfo());
            jsonObject.putOpt("device", deviceInfo.getDeviceInfo());
            jsonObject.putOpt("screen", screen.getScreenInfo());
            jsonObject.put("navigation", navigation);
            jsonObject.putOpt("os", os.getOs());
            if (getLocation() != null)
                jsonObject.putOpt("geo", getLocation());
            if (campaign != null)
                jsonObject.putOpt("campaign", campaign.getCampaign());
            jsonObject.put("timezone", getTimeZone());
            jsonObject.put("userAgent", getUserAgent());
            jsonObject.put("ip", getIpAddress());
            jsonObject.put("locale", getLocale());
            jsonObject.putOpt("network", network.getNetWorkInfo());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static class AppInfo {
        private Context mContext;

        AppInfo(Context context) {
            this.mContext = context;
        }

        private JSONObject getDataAppInfo() {
            JSONObject param = new JSONObject();
            try {
                param.put("name", getAppName());
                param.put("version", getVersion());
                param.put("build", getAppBuild());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return param;
        }

        private String getAppName() {
            ApplicationInfo applicationInfo = mContext.getApplicationInfo();
            int stringId = applicationInfo.labelRes;
            return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : mContext.getString(stringId);
        }

        private String getVersion() {
            String version = "";
            try {
                version = mContext.getPackageManager()
                        .getPackageInfo(mContext.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return version;
        }

        private int getAppBuild() {
            int buildVersion = 0;
            try {
                buildVersion = mContext.getPackageManager()
                        .getPackageInfo(mContext.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return buildVersion;
        }

    }

    private class SdkInfo {

        private SdkInfo() {
        }

        private JSONObject getSdkInfo() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", "SDK Insights V2 on Android");
                jsonObject.put("version", BuildConfig.VERSION_NAME);
                jsonObject.put("build", BuildConfig.VERSION_CODE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

    }

    public class DeviceInfo {

        private Context mContext;

        DeviceInfo(Context context) {
            this.mContext = context;
        }

        private JSONObject getDeviceInfo() {
            JSONObject param = new JSONObject();
            try {
                param.put("id", getDeviceId());
                param.put("name", getDeviceName());
                param.put("type", "android");
                param.put("adTrackingEnabled", isAdTrackingEnabled());
                param.put("model", getDeviceModel());
                param.put("advertisingId", getAdvertisingId());
                param.put("manufacturer", getManufacturer());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return param;
        }

        private String getDeviceId() {
            return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        private String getAdvertisingId() {
            return Build.DEVICE;
        }

        private boolean isAdTrackingEnabled() {
            return true;
        }

        private String getManufacturer() {
            return Build.MANUFACTURER;
        }

        private String getDeviceModel() {
            return Build.MODEL;
        }

        private String getDeviceName() {
            return android.os.Build.MODEL;
        }
    }

    private class Network {

        JSONObject getNetWorkInfo() {
            JSONObject param = new JSONObject();
            try {
                param.put("carrier", getCellular());
                param.put("bluetooth", getBluetoothStatus());
                param.put("wifi", getWifiStatus());
                param.put("cellular", getCellular());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return param;
        }

        private Network() {
        }

        private boolean getCellular() {
            ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);

            return manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .isConnectedOrConnecting();
        }

        private String getCarrierName() {
            TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getNetworkOperatorName();
        }

        private boolean getWifiStatus() {
            WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wifiManager.isWifiEnabled();
        }

        private boolean getBluetoothStatus() {
            BluetoothAdapter btAdapter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                btAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            }

            if (btAdapter == null) {
                return false;
            }
            return btAdapter.getState() == BluetoothAdapter.STATE_ON;
        }
    }

    private class Screen {
        private Context mContext;

        private Screen(Context context) {
            this.mContext = context;
        }

        private JSONObject getScreenInfo() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("width", getWidth());
                jsonObject.put("weight", getHeight());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        private int getWidth() {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }

        private int getHeight() {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    private class Os {

        private Os() {
        }

        private JSONObject getOs() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", "android");
                jsonObject.put("version", getAndroidVersion());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        private String getAndroidVersion() {
            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            return sdkVersion + " (" + release + ")";
        }
    }

    private String getTimeZone() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("z", Locale.getDefault());
        return date.format(currentLocalTime);
    }

    private String getUserAgent() {
        return new WebView(mContext).getSettings().getUserAgentString();
    }

    private String getLocale() {
        Locale current = mContext.getResources().getConfiguration().locale;
        return current.getCountry();
    }

    private String getIpAddress() {
        WifiManager wm = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    private JSONObject getLocation() {
        JSONObject param = new JSONObject();
        String latitude = Utils.getSharedPreValue(mContext, Constants.CURRENT_LATITUDE);
        String longitude = Utils.getSharedPreValue(mContext, Constants.CURRENT_LONGITUDE);
        if (!TextUtils.isEmpty(latitude) || !TextUtils.isEmpty(longitude)) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                try {
                    param.put("city", addresses.get(0).getAdminArea());
                    param.put("country", addresses.get(0).getCountryName());
                    param.put("latitude", latitude);
                    param.put("longitude", longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return param;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
}
