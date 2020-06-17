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

    JSONObject getExtraData() {
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

    public static class Builder {

        private String orderId;
        private Number revenue;
        private String promotionCode;
        private Number discountAmount;
        private Number tax;
        private Number deliveryCost;
        private String srcSearchTerm;
        private List<Other> others;

        public Builder() {
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder revenue(Number revenue) {
            this.revenue = revenue;
            return this;
        }

        public Builder discountAmount(Number discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }

        public Builder srcSearchTerm(String srcSearchTerm) {
            this.srcSearchTerm = srcSearchTerm;
            return this;
        }

        public Builder promotionCode(String promotionCode) {
            this.promotionCode = promotionCode;
            return this;
        }

        public Builder tax(Number tax) {
            this.tax = tax;
            return this;
        }

        public Builder deliveryCost(Number deliveryCost) {
            this.deliveryCost = deliveryCost;
            return this;
        }

        public Builder otherList(List<Other> otherList) {
            this.others = otherList;
            return this;
        }


        public ExtraItem build() {
            return new ExtraItem(this);
        }

    }

    private ExtraItem(Builder builder) {
        orderId = builder.orderId;
        revenue = builder.revenue;
        promotionCode = builder.promotionCode;
        discountAmount = builder.discountAmount;
        tax = builder.tax;
        deliveryCost = builder.deliveryCost;
        srcSearchTerm = builder.srcSearchTerm;
        others = builder.others;
    }
}
