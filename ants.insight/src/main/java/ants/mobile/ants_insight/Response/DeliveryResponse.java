package ants.mobile.ants_insight.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ants.mobile.ants_insight.adx.Campaign;

public class DeliveryResponse {
    @SerializedName("status")
    private boolean status;
    @SerializedName("campaigns")
    private List<Campaign> campaign;

    public boolean campaignStatus() {
        return status;
    }

    public List<Campaign> getCampaign() {
        return campaign;
    }
}
