package ants.mobile.ants_insight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ants.mobile.ants_insight.Constants.Event;
import ants.mobile.ants_insight.Model.CustomEventsData;
import ants.mobile.ants_insight.Model.DataRequestFaceBook;
import ants.mobile.ants_insight.Model.ProductItem;
import ants.mobile.ants_insight.Service.ApiClient;
import ants.mobile.ants_insight.Service.FacebookApiDetail;

import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_ADD_TO_CART_TOKEN;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_ADD_TO_CART_APP_ID;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_CHECK_OUT_TOKEN;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_PURCHASE_TOKEN;
import static ants.mobile.ants_insight.Constants.Constants.PREF_FB_VIEW_PRODUCT_TOKEN;
import static ants.mobile.ants_insight.Constants.Constants.TO_FB_ADD_TO_CART;
import static ants.mobile.ants_insight.Constants.Constants.TO_FB_CHECKOUT;
import static ants.mobile.ants_insight.Constants.Constants.TO_FB_PURCHASE;
import static ants.mobile.ants_insight.Constants.Constants.TO_FB_VIEW_PRODUCT;

/**
 * Created by luonglc on 17/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class FacebookMarketing {

    private FacebookApiDetail facebookApiDetail;
    private List<ProductItem> productList;
    private String insightEventName;
    private int eventType;

    public static class Builder {
        private List<ProductItem> productList;
        private String insightEventName;

        Builder setProductList(List<ProductItem> productList) {
            this.productList = productList;
            return this;
        }

        Builder insightEventName(String insightEventName) {
            this.insightEventName = insightEventName;
            return this;
        }

        public FacebookMarketing build() {
            return new FacebookMarketing(this);
        }
    }

    private FacebookMarketing(Builder builder) {
        productList = builder.productList;
        insightEventName = builder.insightEventName;
        if (facebookApiDetail == null)
            facebookApiDetail = ApiClient.getFbApiInstance();
    }

    void callApiFacebook() {
        String appId = InsightSharedPref.getStringValue(PREF_FB_ADD_TO_CART_APP_ID);
        CustomEventsData eventsData = new CustomEventsData();
        eventsData.setEventName(getFbEventName(insightEventName));
        eventsData.setProductsList(productList);
        long valueSum = 0;
        String currency = "";
        for (ProductItem productItem : productList) {
            valueSum = (long) (valueSum + productItem.getProductPrice());
            currency = productItem.getVariant();
        }
        eventsData.setCurrency(currency);
        eventsData.setValueSum(valueSum);
        eventsData.setTypeName("product");

        List<CustomEventsData> eventsDataList = new ArrayList<>();
        eventsDataList.add(eventsData);

        DataRequestFaceBook data = new DataRequestFaceBook.Builder().setEvent("CUSTOM_APP_EVENTS")
                .setAppTrackingEnabled(true)
                .setAdvertiserTrackingEnabled(true)
                .setData(eventsDataList).build();
        facebookApiDetail.fbLogEvent(appId, getQueryParam(eventType), data);
    }

    /**
     * get query param
     *
     * @param type : delivery or insight
     * @return Map<String, String>
     */

    private Map<String, String> getQueryParam(int type) {
        Map<String, String> param = new HashMap<>();
        switch (type) {
            case TO_FB_PURCHASE:
                param.put("access_token", InsightSharedPref.getStringValue(PREF_FB_PURCHASE_TOKEN));
                eventType = TO_FB_PURCHASE;
                break;
            case TO_FB_ADD_TO_CART:
                param.put("access_token", InsightSharedPref.getStringValue(PREF_FB_ADD_TO_CART_TOKEN));
                eventType = TO_FB_ADD_TO_CART;
                break;
            case TO_FB_VIEW_PRODUCT:
                param.put("access_token", InsightSharedPref.getStringValue(PREF_FB_VIEW_PRODUCT_TOKEN));
                eventType = TO_FB_VIEW_PRODUCT;
                break;
            case TO_FB_CHECKOUT:
                param.put("access_token", InsightSharedPref.getStringValue(PREF_FB_CHECK_OUT_TOKEN));
                eventType = TO_FB_CHECKOUT;
                break;
            default:
                break;
        }
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
            case Event.VIEW:
                fbEventName = "fb_mobile_content_view";
                break;
            case Event.PRODUCT_SEARCH:
                fbEventName = "fb_mobile_search";
            default:
                break;
        }
        return fbEventName;
    }

}
