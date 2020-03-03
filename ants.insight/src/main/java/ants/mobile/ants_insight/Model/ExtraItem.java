package ants.mobile.ants_insight.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ExtraItem {
    private String orderId;
    private Number revenue;
    private String promotionCode;
    private Number discountAmount;
    private Number tax;
    private Number deliveryCost;
    private String srcSearchTerm;
    private List<Other> others;

    public ExtraItem() {
    }

    public ExtraItem(String orderId, Number revenue, String promotionCode, Number discountAmount) {
        this.orderId = orderId;
        this.revenue = revenue;
        this.promotionCode = promotionCode;
        this.discountAmount = discountAmount;
    }

    public ExtraItem(String orderId, Number revenue, String promotionCode, Number discountAmount, Number tax, Number deliveryCost, String srcSearchTerm) {
        this.orderId = orderId;
        this.revenue = revenue;
        this.promotionCode = promotionCode;
        this.discountAmount = discountAmount;
        this.tax = tax;
        this.deliveryCost = deliveryCost;
        this.srcSearchTerm = srcSearchTerm;
    }

    public ExtraItem(String orderId, Number revenue, String promotionCode) {
        this.orderId = orderId;
        this.revenue = revenue;
        this.promotionCode = promotionCode;
    }

    public JSONObject getExtraData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", orderId);
            jsonObject.put("revenue", revenue);
            jsonObject.put("promotion_code", promotionCode);
            jsonObject.put("discount_amount", discountAmount);
            jsonObject.put("tax", tax);
            jsonObject.put("delivery_cost", deliveryCost);
            jsonObject.put("src_search_term", srcSearchTerm);

            if (others != null && others.size() > 0) {
                for (Other other : others) {
                    jsonObject.put(other.key, other.value);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Number getRevenue() {
        return revenue;
    }

    public void setRevenue(Number revenue) {
        this.revenue = revenue;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public Number getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Number discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Number getTax() {
        return tax;
    }

    public void setTax(Number tax) {
        this.tax = tax;
    }

    public Number getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Number deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public String getSrcSearchTerm() {
        return srcSearchTerm;
    }

    public void setSrcSearchTerm(String srcSearchTerm) {
        this.srcSearchTerm = srcSearchTerm;
    }

    public List<Other> getOthers() {
        return others;
    }

    public void setOthers(List<Other> others) {
        this.others = others;
    }
}
