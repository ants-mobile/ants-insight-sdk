package ants.mobile.ants_insight;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ants.mobile.ants_insight.Constants.Constants;
import ants.mobile.ants_insight.Service.GoogleTracking;
import ants.mobile.ants_insight.adx.ActivityLifecycleListener;
import ants.mobile.ants_insight.adx.Campaign;
import ants.mobile.ants_insight.adx.Utils;
import ants.mobile.ants_insight.adx.WebViewManager;
import ants.mobile.ants_insight.Constants.Event;
import ants.mobile.ants_insight.Model.CurrentLocation;
import ants.mobile.ants_insight.Response.DeliveryResponse;
import ants.mobile.ants_insight.Model.Dimension;
import ants.mobile.ants_insight.Model.ExtraItem;
import ants.mobile.ants_insight.Model.InsightConfig;
import ants.mobile.ants_insight.Model.InsightDataRequest;
import ants.mobile.ants_insight.Model.ProductItem;
import ants.mobile.ants_insight.Model.UserItem;
import ants.mobile.ants_insight.Service.ApiClient;
import ants.mobile.ants_insight.Service.DeliveryApiDetail;
import ants.mobile.ants_insight.Service.InsightApiDetail;
import ants.mobile.ants_insight.db.InsightDatabase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static ants.mobile.ants_insight.Constants.Constants.PREF_DELIVERY_URL;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_ADD_TO_CART_TOKEN;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_CHECKOUT_APP_ID;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_CHECK_OUT_TOKEN;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_PURCHASE_APP_ID;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_PURCHASE_TOKEN;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_VIEW_PRODUCT_APP_ID;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_VIEW_PRODUCT_TOKEN;
import static ants.mobile.ants_insight.Constants.Constants.PREF_INSIGHT_URL;
import static ants.mobile.ants_insight.Constants.Constants.IS_DELIVERY;
import static ants.mobile.ants_insight.Constants.Constants.PREF_IS_FIRST_INSTALL_APP;
import static ants.mobile.ants_insight.Constants.Constants.PREF_PORTAL_ID;
import static ants.mobile.ants_insight.Constants.Constants.PREF_PROPERTY_ID;
import static ants.mobile.ants_insight.Constants.Constants.MAXIMUM_NUMBER_OF_REQUESTS;
import static ants.mobile.ants_insight.Constants.Constants.PERMISSION_ALL;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_ADD_TO_CART_APP_ID;
import static ants.mobile.ants_insight.Constants.Constants.PREF_UID;
import static ants.mobile.ants_insight.Constants.Constants.DELIVERY;
import static ants.mobile.ants_insight.Constants.Constants.INSIGHT;

public class Insights {

    private Context mContext;
    private InsightApiDetail isApiDetail;
    private DeliveryApiDetail deliveryApiDetail;
    private InsightDatabase mInsightDatabase;
    private Cursor events;
    private SQLiteDatabase db;
    private List<Long> timeRequest = new ArrayList<>();
    private static final String TAG = Insights.class.getCanonicalName();
    private JsonObject paramObject;
    private boolean isShowInAppView = true;
    private boolean isDelivery = true;

    private String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA
    };


    public static class Builder {
        private Context mContext;

        public Insights.Builder getContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Insights build() {
            return new Insights(this);
        }
    }

    private Insights(Builder builder) {
        mContext = builder.mContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            initialization();

        String data = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            data = Utils.getAssetJsonData(mContext);
        }
        Type type = new TypeToken<InsightConfig>() {
        }.getType();
        InsightConfig config = new Gson().fromJson(data, type);

        if (validConfigFile(config))
            saveConfigToSharedPref(config);
        else
            Log.e(TAG, "PLEASE READ THE CONFIGURATION FILE CREATION GUIDE AGAIN");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initialization() {
        ActivityLifecycleListener.registerActivityLifecycleCallbacks((Application) mContext.getApplicationContext());

        if (mInsightDatabase == null)
            mInsightDatabase = new InsightDatabase(mContext);

        if (!hasPermissions(mContext, PERMISSIONS))
            ActivityCompat.requestPermissions(Utils.getActivity(mContext), PERMISSIONS, PERMISSION_ALL);

        isDelivery = InsightSharedPref.getBooleanValue(IS_DELIVERY);

        new CurrentLocation.Builder().activity(Utils.getActivity(mContext)).build().getAndSaveLastLocation();

        isApiDetail = ApiClient.getInsightInstance();
        deliveryApiDetail = ApiClient.getDeliveryInstance();

        if (InsightSharedPref.getBooleanValue(PREF_IS_FIRST_INSTALL_APP)) {
            logEvent(Event.IDENTIFY);
        }
        registerNetworkReceiver();
    }

    /**
     * create network listeners
     */
    private void registerNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(networkStateReceiver, intentFilter);
    }

    /**
     * listen until the internet is available
     * if network available, check data in localDb, if available, get and callApi
     */
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnectionAvailable(context) && haveDataInDbLocal()) {
                getDataOffline();
            }
        }
    };

    public void setIsShowInAppView(boolean isShowInAppView) {
        this.isShowInAppView = isShowInAppView;
    }

    /**
     * Call when using the event: screenView
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void logEvent(@NonNull String action) {
        InsightDataRequest data = new InsightDataRequest.Builder().withEventName(action).build();
        paramObject = (JsonObject) JsonParser.parseString(data.getJSONObjectData().toString());
        callInsightApi(data);
    }

    /**
     * Call when using the events: logIn, logOut,
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void logEvent(@NonNull String action, UserItem userItem) {
        InsightDataRequest data = new InsightDataRequest
                .Builder()
                .withEventName(action)
                .user(userItem).build();
        callInsightApi(data);
    }

    /**
     * Call when using the events: product_Click, product_Search, product_View....
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void logEvent(@NonNull String action, @NonNull List<ProductItem> productItems,
                         @Nullable ExtraItem extraItem, @Nullable List<Dimension> dimensions) {

        InsightDataRequest data = new InsightDataRequest.Builder().withEventName(action)
                .productList(productItems)
                .extraData(extraItem)
                .dimensionList(dimensions).build();

        callInsightApi(data);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void callInsightApi(InsightDataRequest dataRequest) {
        paramObject = (JsonObject) JsonParser.parseString(dataRequest.getJSONObjectData().toString());
        if (insightsValid(INSIGHT)) {
            isApiDetail.logEvent(getQueryParam(INSIGHT), paramObject);
        }

        if (isDelivery && insightsValid(DELIVERY)) {
            deliveryApiDetail.logDelivery(getQueryParam(DELIVERY), paramObject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CustomApiCallBack<DeliveryResponse>() {
                        @Override
                        public void onNext(DeliveryResponse response) {
                            super.onNext(response);
                            //Todo: handle show ads
                            if (isShowInAppView && response.campaignStatus() && response.getCampaign() != null) {
                                response.getCampaign().get(0).setPositionId("full_screen");
                                response.getCampaign().get(0).setNative(true);
                                handleShowAd(response.getCampaign().get(0));
                            }
                        }
                    });
        }
        // eventName equals: purchase, add_to_cart, view_product, search, checkout then fb API
        // eventName in SDK not matching fb API

//        if (!TextUtils.isEmpty(getFbEventName(dataRequest.getEventAction()))) {
//            FacebookEvents fb = new FacebookEvents.Builder().insightEventName(dataRequest.getEventAction())
//                    .setProductList(dataRequest.getProductItemList()).build();
//
//            GoogleTracking googleTracking = new GoogleTracking.Builder().insightEventName(dataRequest.getEventAction())
//                    .setProductList(dataRequest.getProductItemList()).build();
//
//            googleTracking.googleTrackingEvent();
//            fb.callApiFacebook();
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean insightsValid(int eventType) {
        if (!isNetworkConnectionAvailable(mContext)) {
            Log.e(TAG, "NETWORK NOT AVAILABLE");
            saveDataToDbLocal(paramObject.toString(), eventType);
            return false;
        }

        return true;
    }

    private static Long getCurrentTime() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.getTime();
    }

    public void unregisterReceiver() {
        if (mContext != null && networkStateReceiver != null)
            mContext.unregisterReceiver(networkStateReceiver);
        if (WebViewManager.lastInstance != null)
            WebViewManager.lastInstance = null;
    }

    /**
     * get query param
     *
     * @param type : delivery or insight
     * @return Map<String, String>
     */

    private Map<String, String> getQueryParam(int type) {
        Map<String, String> param = new HashMap<>();
        param.put("portal_id",  InsightSharedPref.getStringValue(PREF_PORTAL_ID));
        param.put("prop_id", InsightSharedPref.getStringValue(PREF_PROPERTY_ID));
        param.put(type == DELIVERY ? "format" : "resp_type", "json");
        return param;
    }

    private String getFbEventName(String insightEventName) {
        String fbEventName = "";
        switch (insightEventName) {
            case Event.PURCHASE:
                fbEventName = "fb_mobile_purchase";
                break;
            case Event.ADD_TO_CART:
                fbEventName = "fb_mobile_add_to_cart";
                break;
            case Event.PAYMENT:
                fbEventName = "fb_mobile_add_payment_info";
                break;
            case Event.VIEW_PRODUCT_DETAIL:
                fbEventName = "fb_mobile_content_view";
                break;
            case Event.PRODUCT_SEARCH:
                fbEventName = "fb_mobile_search";
            default:
                break;
        }
        return fbEventName;
    }

    private void handleShowAd(Campaign campaign) {
        if (!WebViewManager.isShowingAds) {
            WebViewManager.showHTMLString(campaign);
            WebViewManager.isShowingAds = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resetAnonymousId() {
        InsightDataRequest param = new InsightDataRequest.Builder()
                .eventActionCustom("reset_anonymous_id")
                .eventCategoryCustom("user").build();

        if (!Anonymous.getInstance().isFileExists()) {
            Anonymous.getInstance().saveIndexToStorageLocal(mContext, "0");
            InsightSharedPref.savePreference(PREF_UID, "0");
        }

        callInsightApi(param);
    }

    private static void saveConfigToSharedPref(InsightConfig config) {
        InsightSharedPref.savePreference(PREF_INSIGHT_URL, config.getInsightUrl());
        InsightSharedPref.savePreference(PREF_DELIVERY_URL, config.getDeliveryUrl());
        InsightSharedPref.savePreference(IS_DELIVERY, config.isDelivery());
        InsightSharedPref.savePreference(PREF_PORTAL_ID, config.getPortalId());
        InsightSharedPref.savePreference(PREF_PROPERTY_ID, config.getPropertyId());
        InsightSharedPref.savePreference(PREF_FB_ADD_TO_CART_APP_ID, config.getFbAddToCartAppId());
        InsightSharedPref.savePreference(PREF_FB_PURCHASE_TOKEN, config.getFbPurchaseToken());
        InsightSharedPref.savePreference(PREF_FB_ADD_TO_CART_TOKEN, config.getFbAddToCartToken());
        InsightSharedPref.savePreference(PREF_FB_CHECK_OUT_TOKEN, config.getFbCheckoutToken());
        InsightSharedPref.savePreference(PREF_FB_VIEW_PRODUCT_TOKEN, config.getFbViewProductToken());
        InsightSharedPref.savePreference(PREF_FB_CHECKOUT_APP_ID, config.getFbCheckOutAppId());
        InsightSharedPref.savePreference(PREF_FB_PURCHASE_APP_ID, config.getFbPurchaseAppId());
        InsightSharedPref.savePreference(PREF_FB_VIEW_PRODUCT_APP_ID, config.getFbViewProductAppId());

        InsightSharedPref.savePreference(Constants.PREF_GG_DEV_TOKEN, config.getDevToken());
        InsightSharedPref.savePreference(Constants.PREF_GG_VIEW_PRODUCT_LINK_ID, config.getGgViewProductLinkId());
        InsightSharedPref.savePreference(Constants.PREF_GG_VIEW_LIST_LINK_ID, config.getGgViewListLinkId());
        InsightSharedPref.savePreference(Constants.PREF_GG_PURCHASE_LINK_ID, config.getGgPurchaseLinkId());
        InsightSharedPref.savePreference(Constants.PREF_GG_ADD_TO_CART_LINK_ID, config.getGgAddToCartLinkId());
    }

    private static boolean validConfigFile(InsightConfig config) {
        if (config == null)
            return false;
        else {
            return !TextUtils.isEmpty(config.getPortalId()) &&
                    !TextUtils.isEmpty(config.getInsightUrl()) &&
                    !TextUtils.isEmpty(config.getPropertyId()) &&
                    !TextUtils.isEmpty(config.getDeliveryUrl());
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * query data from dbLocal
     *
     * @return boolean
     */

    private boolean haveDataInDbLocal() {
        if (mInsightDatabase != null) {
            db = mInsightDatabase.getWritableDatabase();
            events = db.query("events", null, null, null, null, null, null);
            return events.getCount() > 0;
        } else return false;
    }

    /**
     * check network available
     *
     * @param context Context
     * @return boolean
     */

    private static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    /**
     * if network available --> push data to server
     *
     * @param object    Object
     * @param eventType int
     */

    private void postDataWhenNetworkAvailable(Object object, int eventType) {
        switch (eventType) {
            case INSIGHT:
                isApiDetail.logEvent(getQueryParam(INSIGHT), object);
                break;
            case DELIVERY:
                deliveryApiDetail.logDelivery(getQueryParam(DELIVERY), object);
                break;
            default:
                break;
        }
    }

    /**
     * Insert data to dbLocal when network not available
     * maximum insert 30 record
     *
     * @param eventData : InsightDataRequest
     * @param eventType type Insight or Delivery
     */

    private void saveDataToDbLocal(String eventData, int eventType) {
        if (mInsightDatabase != null) {
            db = mInsightDatabase.getWritableDatabase();
            events = db.query("events", null, null, null, null, null, null);
            if (events.getCount() < MAXIMUM_NUMBER_OF_REQUESTS)
                mInsightDatabase.insertData(eventData, getCurrentTime(), eventType);
            else
                Log.e(TAG, "db can only save up to " + MAXIMUM_NUMBER_OF_REQUESTS + " request");
        }
    }

    private void getDataOffline() {
        if (mInsightDatabase != null) {
            db = mInsightDatabase.getWritableDatabase();
            events = db.query("events", null, null, null, null, null, null);
            db.beginTransaction();
            while (events.moveToNext()) {
                String dataRequest = events.getString(events.getColumnIndex("event_data"));
                JsonParser parser = new JsonParser();
                JsonObject json = (JsonObject) parser.parse(dataRequest);
                timeRequest.add(events.getLong(events.getColumnIndex("time")));
                int eventType = events.getInt(events.getColumnIndex("event_type"));
                postDataWhenNetworkAvailable(json, eventType);
            }
            db.endTransaction();
            events.close();
            deleteItem(timeRequest);
        }
    }

    private void deleteItem(List<Long> timeRequest) {
        if (timeRequest != null) {
            for (int i = 0; i < timeRequest.size(); i++) {
                mInsightDatabase.deleteRow(timeRequest.get(i));
            }
            this.timeRequest = new ArrayList<>();
        }
    }
}
