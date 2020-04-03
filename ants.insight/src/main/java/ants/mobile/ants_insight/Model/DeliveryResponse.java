package ants.mobile.ants_insight.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import adx.Campaign;

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
