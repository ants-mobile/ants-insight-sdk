package ants.mobile.ants_insight.Service;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ants.mobile.ants_insight.BuildConfig;
import ants.mobile.ants_insight.Constants.Constants;
import ants.mobile.ants_insight.Constants.Event;
import ants.mobile.ants_insight.Constants.ParamsKey;
import ants.mobile.ants_insight.CustomApiCallBack;
import ants.mobile.ants_insight.InsightSDK;
import ants.mobile.ants_insight.InsightSharedPref;
import ants.mobile.ants_insight.Model.GoogleTrackingModel;
import ants.mobile.ants_insight.Model.ProductItem;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by luonglc on 18/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class GoogleTracking {
    private GoogleTrackingAPI googleTrackingAPI;
    private List<ProductItem> productList;
    private String insightEventName;
    private int eventType;
    private String devToken = "";
    private String linkId = "";

    public static class Builder {
        private List<ProductItem> productList;
        private String insightEventName;

        public Builder setProductList(List<ProductItem> productList) {
            this.productList = productList;
            return this;
        }

        public Builder insightEventName(String insightEventName) {
            this.insightEventName = insightEventName;
            return this;
        }

        public GoogleTracking build() {
            return new GoogleTracking(this);
        }
    }

    private GoogleTracking(Builder builder) {
        productList = builder.productList;
        insightEventName = builder.insightEventName;
        if (googleTrackingAPI == null)
            googleTrackingAPI = ApiClient.getGoogleTrackingInstance();
    }

    public void googleTrackingEvent() {
        GoogleTrackingModel body = new GoogleTrackingModel();
        body.setProductItems(productList);
        googleTrackingAPI.trackingEvent(getQueryParam(), body).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomApiCallBack<Object>() {
                    @Override
                    public void onNext(Object o) {
                        super.onNext(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });

    }

    /**
     * get query param
     *
     * @return Map<String, String>
     */

    private Map<String, String> getQueryParam() {
        Map<String, String> param = new HashMap<>();

        String currency = "";
        long valueSum = 0;
        for (ProductItem productItem : productList) {
            valueSum = (long) (valueSum + productItem.getProductPrice());
            currency = productItem.getVariant();
        }

        param.put(ParamsKey.GG_DEV_TOKEN, InsightSharedPref.getStringValue(Constants.PREF_GG_DEV_TOKEN));
        param.put(ParamsKey.GG_LINK_ID, linkId);
        param.put(ParamsKey.GG_EVENT_TYE, getGoogleTrackingEventName(insightEventName));
        param.put(ParamsKey.GG_RD_ID, getDeviceId());
        param.put(ParamsKey.GG_ID_TYPE, "idfa");
        param.put(ParamsKey.GG_LAT, "1");
        param.put(ParamsKey.GG_APP_VERSION, getAppVersion());
        param.put(ParamsKey.GG_SDK_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
        param.put(ParamsKey.GG_OS_VERSION, android.os.Build.VERSION.RELEASE);
        param.put(ParamsKey.GG_TIME_STAMP, "1592449733");
        param.put(ParamsKey.GG_VALUE, String.valueOf(valueSum));
        param.put(ParamsKey.GG_CURRENCY, currency);
        return param;
    }

    private String getGoogleTrackingEventName(String insightEventName) {
        String fbEventName = "";
        switch (insightEventName) {
            case Event.PURCHASE:
                fbEventName = "in_app_purchase";
                eventType = Constants.GG_PURCHASE;
                linkId = InsightSharedPref.getStringValue(Constants.PREF_GG_PURCHASE_LINK_ID);
                break;
            case Event.ADD_TO_CART:
                fbEventName = "add_to_cart";
                linkId = InsightSharedPref.getStringValue(Constants.PREF_GG_ADD_TO_CART_LINK_ID);
                eventType = Constants.GG_ADD_TO_CART;
                break;
            case Event.PRODUCT_LIST_VIEW:
                fbEventName = "view_item_list";
                linkId = InsightSharedPref.getStringValue(Constants.PREF_GG_VIEW_LIST_LINK_ID);
                eventType = Constants.GG_VIEW_LIST;
                break;
            case Event.VIEW_PRODUCT_DETAIL:
                eventType = Constants.GG_VIEW_PRODUCT;
                linkId = InsightSharedPref.getStringValue(Constants.PREF_GG_VIEW_PRODUCT_LINK_ID);
                fbEventName = "view_item";
                break;
            case Event.PRODUCT_SEARCH:
                eventType = Constants.GG_PRODUCT_SEARCH;
                linkId = InsightSharedPref.getStringValue(Constants.PREF_GG_SEARCH_PRODUCT_LINK_ID);
                fbEventName = "view_search_results";
            default:
                break;
        }
        return fbEventName;
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        UUID androidId_UUID = null;
        String androidId = Settings.Secure.getString(InsightSDK.getInstance().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        try {
            androidId_UUID = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return androidId_UUID.toString().toUpperCase();
    }

    private String getAppVersion() {
        String version = "";
        try {
            version = InsightSDK.getInstance().getPackageManager()
                    .getPackageInfo(InsightSDK.getInstance().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

}
