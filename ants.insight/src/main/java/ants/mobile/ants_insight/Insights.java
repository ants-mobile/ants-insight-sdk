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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adx.ActivityLifecycleListener;
import adx.Campaign;
import adx.Utils;
import adx.WebViewManager;
import ants.mobile.ants_insight.Constants.ActionEvent;
import ants.mobile.ants_insight.Model.Anonymous;
import ants.mobile.ants_insight.Model.ContextModel;
import ants.mobile.ants_insight.Model.CurrentLocation;
import ants.mobile.ants_insight.Model.DeliveryResponse;
import ants.mobile.ants_insight.Model.Dimension;
import ants.mobile.ants_insight.Model.ExtraItem;
import ants.mobile.ants_insight.Model.InsightConfig;
import ants.mobile.ants_insight.Model.InsightDataRequest;
import ants.mobile.ants_insight.Model.ProductItem;
import ants.mobile.ants_insight.Model.UserItem;
import ants.mobile.ants_insight.Service.ApiClient;
import ants.mobile.ants_insight.Service.DeliveryApiDetail;
import ants.mobile.ants_insight.Service.InsightApiDetail;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Insights {

    private Context mContext;
    private InsightApiDetail isApiDetail;
    private DeliveryApiDetail dlvApiDetail;
    private InsightDatabase mInsightDatabase;
    private Cursor events;
    private SQLiteDatabase db;
    private List<Long> timeRequest = new ArrayList<>();
    private static final int MAXIMUM_NUMBER_OF_REQUESTS = 30;
    private static final String TAG = "INSIGHT_ERROR";
    private JsonObject paramObject;
    private JsonParser insightsJson = new JsonParser();
    private static final int INSIGHT_TYPE = 1;
    private static final int DELIVERY_TYPE = 2;
    private boolean isShowInAppView = true;

    private String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Insights(Context mContext) {
        this.mContext = mContext;
        if (mInsightDatabase == null)
            mInsightDatabase = new InsightDatabase(mContext);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void init() {

        ActivityLifecycleListener.registerActivityLifecycleCallbacks((Application) mContext.getApplicationContext());

        if (!hasPermissions(mContext, PERMISSIONS)) {
            int PERMISSION_ALL = 200;
            ActivityCompat.requestPermissions(Utils.getActivity(mContext), PERMISSIONS, PERMISSION_ALL);
        }

        CurrentLocation currentLocation = new CurrentLocation(Utils.getActivity(mContext));
        currentLocation.getAndSaveLastLocation();

        isApiDetail = ApiClient.getInsightInstance(mContext);
        dlvApiDetail = ApiClient.getDeliveryInstance(mContext);

        registerNetworkReceiver();

        if (InsightSharedPref.getIsFirstInstallApp(mContext)) {
            logEvent(ActionEvent.USER_IDENTIFY_ACTION);
        }
    }

    private void registerNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(networkStateReceiver, intentFilter);
    }

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

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventAction(action);
        data.setContextModel(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            paramObject = (JsonObject) insightsJson.parse(data.getDataRequest().toString());
        }
        callApi();
    }

    /**
     * Call when using the events: logIn, logOut,
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void logEvent(@NonNull String action, UserItem userItem) {

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventAction(action);
        data.setContextModel(context);
        data.setUserItem(userItem);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            paramObject = (JsonObject) insightsJson.parse(data.getDataRequest().toString());
        }

        callApi();
    }

    /**
     * Call when using the events: product_Click, product_Search, product_View....
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void logEvent(@NonNull String action, @NonNull List<ProductItem> productItems,
                         @Nullable ExtraItem extraItem, @Nullable List<Dimension> dimensions) {

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventAction(action);
        data.setProductItemList(productItems);
        data.setExtraItem(extraItem);
        data.setContextModel(context);
        data.setDimensionList(dimensions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            paramObject = (JsonObject) insightsJson.parse(data.getDataRequest().toString());
        }

        callApi();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void callApi() {
        if (insightsValid(INSIGHT_TYPE)) {
            isApiDetail.logEvent(getQueryParam(INSIGHT_TYPE), paramObject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CustomApiCallBack<JsonObject>() {
                    });
        }

        if (InsightSharedPref.getIsDelivery(mContext) && insightsValid(DELIVERY_TYPE)) {
            dlvApiDetail.logDelivery(getQueryParam(DELIVERY_TYPE), paramObject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CustomApiCallBack<DeliveryResponse>() {
                        @Override
                        public void onNext(DeliveryResponse response) {
                            super.onNext(response);
                            //Todo: handle show ads
                            if (isShowInAppView && response.campaignStatus() && response.getCampaign() != null) {
//                                response.getCampaign().get(0).setPositionId("top");
                                handleShowAd(response.getCampaign().get(0));
                            }
//                            Campaign campaign = new Campaign();
//                            campaign.setPositionId("full_screen");
//                            campaign.setTimeDelay("0");
//                            handleShowAd(campaign);
                        }
                    });
        }
    }

    /**
     * Check if the local db has data
     */

    private boolean haveDataInDbLocal() {
        if (mInsightDatabase != null) {
            db = mInsightDatabase.getWritableDatabase();
            events = db.query("events", null, null, null, null, null, null);
            return events.getCount() > 0;
        } else return false;
    }

    private static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    private void postDataWhenNetworkAvailable(Object object, int eventType) {
        switch (eventType) {
            case INSIGHT_TYPE:
                isApiDetail.logEvent(getQueryParam(INSIGHT_TYPE), object)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new CustomApiCallBack<JsonObject>() {
                        });
                break;
            case DELIVERY_TYPE:
                dlvApiDetail.logDelivery(getQueryParam(DELIVERY_TYPE), object)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new CustomApiCallBack<DeliveryResponse>() {
                        });
                break;
            default:
                break;
        }
    }

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
            deleteData(timeRequest);
        }
    }

    private void deleteData(List<Long> timeRequest) {
        if (timeRequest != null) {
            for (int i = 0; i < timeRequest.size(); i++) {
                mInsightDatabase.deleteRow(timeRequest.get(i));
            }
            this.timeRequest = new ArrayList<>();
        }
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
    }

    private Map<String, String> getQueryParam(int type) {
        Map<String, String> param = new HashMap<>();
        param.put("portal_id", InsightSharedPref.getPortalId(mContext));
        param.put("prop_id", InsightSharedPref.getPropertyId(mContext));
        param.put(type == DELIVERY_TYPE ? "format" : "resp_type", "json");

        return param;
    }

    private void handleShowAd(Campaign campaign) {
        if (!WebViewManager.isShowingAds) {
            WebViewManager.showHTMLString(campaign);
            WebViewManager.isShowingAds = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resetAnonymousId() {

        InsightDataRequest param = new InsightDataRequest(mContext);
        param.setEventCustom("reset_anonymous_id", "user");
        if (!Anonymous.getInstance().isFileExists()) {
            Anonymous.getInstance().saveIndexToStorageLocal(mContext, "0");
            InsightSharedPref.setUID(mContext, "0");
        }

        JsonObject paramObject = (JsonObject) insightsJson.parse(param.getDataRequest().toString());
        isApiDetail.logEvent(getQueryParam(INSIGHT_TYPE), paramObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomApiCallBack<JsonObject>() {
                    @Override
                    public void onNext(JsonObject object) {
                        super.onNext(object);
                        param.updateAnonymousIndex();
                    }
                });

        dlvApiDetail.logDelivery(getQueryParam(DELIVERY_TYPE), paramObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomApiCallBack<Object>() {
                });


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void initialization(@NonNull Context mContext) {
        String data = Utils.getAssetJsonData(mContext);
        Type type = new TypeToken<InsightConfig>() {
        }.getType();
        InsightConfig config = new Gson().fromJson(data, type);
        if (config == null) {
            Log.e(TAG, "PLEASE READ THE CONFIGURATION FILE CREATION GUIDE AGAIN");
        } else {
            InsightSharedPref.setInsightURL(mContext, config.getInsightUrl());
            InsightSharedPref.setDeliveryURL(mContext, config.getDeliveryUrl());
            InsightSharedPref.setIsDelivery(mContext, config.isDelivery());
            if (!TextUtils.isEmpty(config.getPortalId()) && !TextUtils.isEmpty(config.getPropertyId())) {
                InsightSharedPref.setPortalId(mContext, config.getPortalId());
                InsightSharedPref.setPropertyId(mContext, config.getPropertyId());
            } else {
                Log.e(TAG, "PLEASE CREATE PORTALID, PROPERTYID VALUE");
            }
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

}
