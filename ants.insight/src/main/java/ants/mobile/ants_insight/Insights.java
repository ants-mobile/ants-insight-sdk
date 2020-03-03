package ants.mobile.ants_insight;

import android.Manifest;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adx.AdListener;
import adx.Campaign;
import adx.Utils;
import adx.WebViewManager;
import ants.mobile.ants_insight.Constants.ActionEvent;
import ants.mobile.ants_insight.Model.Anonymous;
import ants.mobile.ants_insight.Model.ContextModel;
import ants.mobile.ants_insight.Model.CurrentLocation;
import ants.mobile.ants_insight.Model.Dimension;
import ants.mobile.ants_insight.Model.ExtraItem;
import ants.mobile.ants_insight.Model.InsightDataRequest;
import ants.mobile.ants_insight.Model.ProductItem;
import ants.mobile.ants_insight.Model.UserItem;
import ants.mobile.ants_insight.Service.ApiClient;
import ants.mobile.ants_insight.Service.DeliveryApiDetail;
import ants.mobile.ants_insight.Service.InsightApiDetail;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Insights implements AdListener {

    private Context mContext;
    private InsightsCallBackListener mListener;
    private InsightApiDetail isApiDetail;
    private DeliveryApiDetail dlvApiDetail;
    private InsightDatabase mInsightDatabase;
    private Cursor events;
    private SQLiteDatabase db;
    private List<Long> timeRequest = new ArrayList<>();
    private static final int MAXIMUM_NUMBER_OF_REQUESTS = 30;
    private static final String TAG = "InsightsMessage";
    private JsonObject paramObject;
    private JsonParser insightsJson = new JsonParser();
    private static final int INSIGHT_TYPE = 1;
    private static final int DELIVERY_TYPE = 2;
    private static boolean isShowInAppView = false;

    private String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA
    };

    public interface InsightsCallBackListener {

        void onSuccess(JsonObject eventResponse);

        void onError(String error);
    }

    public Insights(Context mContext) {
        this.mContext = mContext;
        if (mInsightDatabase == null)
            mInsightDatabase = new InsightDatabase(mContext);
        initialization();
    }

    public Insights(Context mContext, InsightsCallBackListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;

        if (mInsightDatabase == null)
            mInsightDatabase = new InsightDatabase(mContext);
        initialization();
    }

    private void initialization() {

        if (!hasPermissions(mContext, PERMISSIONS)) {
            int PERMISSION_ALL = 200;
            ActivityCompat.requestPermissions(Utils.getActivity(mContext), PERMISSIONS, PERMISSION_ALL);
        }

        CurrentLocation currentLocation = new CurrentLocation(Utils.getActivity(mContext));
        currentLocation.getAndSaveLastLocation();

        isApiDetail = ApiClient.getInsightInstance();
        dlvApiDetail = ApiClient.getDeliveryInstance();

        registerNetworkReceiver();

        if (InsightSharedPref.getIsFirstInstallApp(mContext)) {
            logEvent(ActionEvent.USER_IDENTIFY_ACTION);
            deliveryEvent(ActionEvent.USER_IDENTIFY_ACTION);
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

    public static void setIsShowInAppView(boolean isShowInAppView) {
        Insights.isShowInAppView = isShowInAppView;
    }

    public void logEvent(@NonNull String action) {

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventAction(action);
        data.setContextModel(context);

        if (insightsValid(INSIGHT_TYPE, data)) {
            isApiDetail.logEvent(getQueryParam(), paramObject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CustomApiCallBack<JsonObject>() {
                    });
        }
    }

    public void logEvent(@NonNull String action, UserItem userItem) {

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventAction(action);
        data.setContextModel(context);
        data.setUserItem(userItem);

        if (insightsValid(INSIGHT_TYPE, data)) {
            isApiDetail.logEvent(getQueryParam(), paramObject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CustomApiCallBack<JsonObject>() {
                    });
        }
    }

    public void logEvent(@NonNull String action, @NonNull List<ProductItem> productItems,
                         @Nullable ExtraItem extraItem, @Nullable List<Dimension> dimensions) {

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventAction(action);
        data.setProductItemList(productItems);
        data.setExtraItem(extraItem);
        data.setContextModel(context);
        data.setDimensionList(dimensions);

        if (insightsValid(INSIGHT_TYPE, data)) {
            isApiDetail.logEvent(getQueryParam(), paramObject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CustomApiCallBack<JsonObject>() {
                    });
        }
    }

    public void deliveryEvent(@NonNull String action) {

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventAction(action);
        data.setContextModel(context);

        if (insightsValid(DELIVERY_TYPE, data)) {
            dlvApiDetail.logDelivery(getQueryParam(), paramObject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CustomApiCallBack<JsonObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onNext(JsonObject deliveryResponse) {
                            super.onNext(deliveryResponse);
                            //Todo: Waiting for api delivery to complete
//                            if (isShowInAppView)
//                                handleShowAd();
                            if (mListener != null) {
                                mListener.onSuccess(deliveryResponse);
                            }
                        }
                    });
        }
    }

    public void deliveryEvent(@NonNull String action, @NonNull List<ProductItem> productItems, @Nullable ExtraItem extraItem, @Nullable List<Dimension> dimensions) {

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventAction(action);
        data.setProductItemList(productItems);
        data.setExtraItem(extraItem);
        data.setContextModel(context);
        data.setDimensionList(dimensions);

        if (insightsValid(DELIVERY_TYPE, data)) {
            dlvApiDetail.logDelivery(getQueryParam(), paramObject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new CustomApiCallBack<JsonObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onNext(JsonObject deliveryResponse) {
                            super.onNext(deliveryResponse);
                            //Todo: Waiting for api delivery to complete
//                            if (isShowInAppView)
//                                handleShowAd();
                            if (mListener != null) {
                                mListener.onSuccess(deliveryResponse);
                            }
                        }
                    });
        }
    }

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
                isApiDetail.logEvent(getQueryParam(), object)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new CustomApiCallBack<JsonObject>() {
                        });
                break;
            case DELIVERY_TYPE:
                dlvApiDetail.logDelivery(getQueryParam(), object)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new CustomApiCallBack<JsonObject>() {
                            @Override
                            public void onNext(JsonObject deliveryResponse) {
                                super.onNext(deliveryResponse);
                                if (mListener != null)
                                    mListener.onSuccess(deliveryResponse);
                            }
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

    private boolean insightsValid(int eventType, InsightDataRequest dataRequest) {

        paramObject = (JsonObject) insightsJson.parse(dataRequest.getDataRequest().toString());

        if (!isNetworkConnectionAvailable(mContext)) {
            Log.e(TAG, "Network not available");
            saveDataToDbLocal(paramObject.toString(), eventType);
            return false;
        }

        if (TextUtils.isEmpty(InsightSharedPref.getPortalId(mContext))
                || TextUtils.isEmpty(InsightSharedPref.getPropertyId(mContext))
                || TextUtils.isEmpty(InsightSharedPref.getPushNotificationId(mContext))) {
            Log.e(TAG, "You need to initialize the portal_id, property_id values.");
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

    private Map<String, String> getQueryParam() {
        Map<String, String> param = new HashMap<>();
        param.put("portal_id", InsightSharedPref.getPortalId(mContext));
        param.put("prop_id", InsightSharedPref.getPropertyId(mContext));
        param.put("resp_type", "json");
        return param;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleShowAd() {
        if (!WebViewManager.isIsShowingAds()) {
            Campaign campaign = new Campaign();
            campaign.setPositionId(1);
            WebViewManager.setAdsListener(this);
            WebViewManager.showHTMLString(campaign);
        }
    }

    public void resetAnonymousId() {

        InsightDataRequest param = new InsightDataRequest(mContext);
        param.setEventCustom("reset_anonymous_id", "user");
        if (!Anonymous.getInstance().isFileExists()) {
            Anonymous.getInstance().saveIndexToStorageLocal(mContext, "0");
            InsightSharedPref.setUID(mContext, "0");
        }

        JsonObject paramObject = (JsonObject) insightsJson.parse(param.getDataRequest().toString());
        isApiDetail.logEvent(getQueryParam(), paramObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomApiCallBack<JsonObject>() {
                    @Override
                    public void onNext(JsonObject object) {
                        super.onNext(object);
                        param.updateAnonymousIndex();
                    }
                });

        dlvApiDetail.logDelivery(getQueryParam(), paramObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomApiCallBack<JsonObject>() {
                });


    }

    @Override
    public void onLoadAd() {
        adxAction(ActionEvent.IMPRESSION_ACTION);
    }

    @Override
    public void onCloseView() {

    }

    @Override
    public void onAdxClick() {
        adxAction(ActionEvent.ADX_CLICK_ACTION);
    }

    private void adxAction(String eventAction) {

        InsightDataRequest data = new InsightDataRequest(mContext);
        ContextModel context = new ContextModel(mContext);
        data.setEventCustom(eventAction, ActionEvent.ADVERTISING_CATEGORY);
        data.setContextModel(context);

        JsonObject paramObject = (JsonObject) insightsJson.parse(data.getDataRequest().toString());

        isApiDetail.logEvent(getQueryParam(), paramObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomApiCallBack<JsonObject>() {
                });

        dlvApiDetail.logDelivery(getQueryParam(), paramObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomApiCallBack<JsonObject>() {
                });
    }

    public static void setInsightsConfig(@NonNull Context mContext, @NonNull String portalId, @NonNull String propertyId) {
        if (TextUtils.isEmpty(InsightSharedPref.getPortalId(mContext))
                || TextUtils.isEmpty(InsightSharedPref.getPropertyId(mContext))) {
            InsightSharedPref.setPortalId(mContext, portalId);
            InsightSharedPref.setPropertyId(mContext, propertyId);
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
