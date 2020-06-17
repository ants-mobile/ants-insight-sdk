package ants.mobile.ants_insight.Model;

import android.provider.Settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by luonglc on 15/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public class CustomEventsData {
    @SerializedName("_eventName")
    @Expose
    private String eventName;
    @SerializedName("fb_content")
    @Expose
    private List<ProductItem> productsList;
    @SerializedName("fb_content_type")
    @Expose
    private String typeName;
    @SerializedName("_valueToSum")
    @Expose
    private double valueSum;
    @SerializedName("fb_currency")
    @Expose
    private String currency;

    public JSONObject getCustomEventsData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_eventName", eventName);
            jsonObject.putOpt("fb_content", getProducts());
            jsonObject.put("fb_content_type", "product");
            jsonObject.putOpt("_valueToSum", 120000000);
            jsonObject.put("fb_currency", "vnd");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<ProductItem> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<ProductItem> productsList) {
        this.productsList = productsList;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getValueSum() {
        return valueSum;
    }

    public void setValueSum(double valueSum) {
        this.valueSum = valueSum;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private JSONArray getProducts() {
        if (productsList == null)
            return null;
        JSONArray array = new JSONArray();
        for (ProductItem productItem : productsList) {
            array.put(productItem.getProduct());
        }
        return array;
    }
}
