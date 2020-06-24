package ants.mobile.ants_insight.Model;

import android.annotation.SuppressLint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by luonglc on 18/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class GoogleTrackingModel {
    @SerializedName("app_event_data")
    @Expose
    private List<ProductItem> productItems;

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }
}
