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
}
