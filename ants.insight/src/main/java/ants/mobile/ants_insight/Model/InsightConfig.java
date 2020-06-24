package ants.mobile.ants_insight.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InsightConfig {
    @SerializedName("user_identify")
    private List<UserModel> userModels;
    @SerializedName("identify_id")
    private UserModel identifyId;
    @SerializedName("portal_id")
    private String portalId;
    @SerializedName("property_id")
    private String propertyId;
    @SerializedName("insight_url")
    private String insightUrl;
    @SerializedName("delivery_url")
    private String deliveryUrl;
    @SerializedName("is_delivery")
    private boolean isDelivery;
    @SerializedName("fb_purchase_token")
    private String fbPurchaseToken;
    @SerializedName("fb_add_to_cart_token")
    private String fbAddToCartToken;
    @SerializedName("fb_check_out_token")
    private String fbCheckoutToken;
    @SerializedName("fb_add_to_cart_app_id")
    private String fbAddToCartAppId;
    @SerializedName("fb_view_product_app_id")
    private String fbViewProductAppId;
    @SerializedName("fb_purchase_app_id")
    private String fbPurchaseAppId;
    @SerializedName("fb_checkout_app_id")
    private String fbCheckOutAppId;
    @SerializedName("fb_view_product_token")
    private String fbViewProductToken;
    @SerializedName("gg_add_to_cart_link_id")
    private String ggAddToCartLinkId;
    @SerializedName("gg_purchase_link_id")
    private String ggPurchaseLinkId;
    @SerializedName("gg_view_list_link_id")
    private String ggViewListLinkId;
    @SerializedName("gg_product_view_link_id")
    private String ggViewProductLinkId;
    @SerializedName("gg_dev_token")
    private String ggDevToken;

    List<UserModel> getUserModels() {
        return userModels;
    }

    UserModel getIdentifyId() {
        return identifyId;
    }

    public String getPortalId() {
        return portalId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getInsightUrl() {
        return insightUrl;
    }

    public String getDeliveryUrl() {
        return deliveryUrl;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public String getFbPurchaseToken() {
        return fbPurchaseToken;
    }

    public String getFbAddToCartToken() {
        return fbAddToCartToken;
    }

    public String getFbCheckoutToken() {
        return fbCheckoutToken;
    }

    public String getFbViewProductToken() {
        return fbViewProductToken;
    }

    public String getFbAddToCartAppId() {
        return fbAddToCartAppId;
    }

    public String getFbViewProductAppId() {
        return fbViewProductAppId;
    }

    public String getFbPurchaseAppId() {
        return fbPurchaseAppId;
    }

    public String getFbCheckOutAppId() {
        return fbCheckOutAppId;
    }

    public String getGgAddToCartLinkId() {
        return ggAddToCartLinkId;
    }

    public String getGgPurchaseLinkId() {
        return ggPurchaseLinkId;
    }

    public String getGgViewListLinkId() {
        return ggViewListLinkId;
    }

    public String getGgViewProductLinkId() {
        return ggViewProductLinkId;
    }

    public String getDevToken() {
        return ggDevToken;
    }
}
