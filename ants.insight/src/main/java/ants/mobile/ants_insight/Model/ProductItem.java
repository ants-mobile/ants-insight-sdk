package ants.mobile.ants_insight.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductItem {
    @SerializedName("id")
    @Expose
    private String productId;
    @SerializedName("name")
    @Expose
    private String productName;
    @SerializedName("sku")
    @Expose
    private String productSku;
    @SerializedName("brand")
    @Expose
    private String productBrand;
    @SerializedName("category")
    @Expose
    private String productCategory;
    @SerializedName("variant")
    @Expose
    private String variant;
    @SerializedName("seller_id")
    @Expose
    private String sellerId;
    @SerializedName("image_url")
    @Expose
    private String productImageUrl;
    @SerializedName("url")
    @Expose
    private String productUrl;
    @SerializedName("price")
    @Expose
    private float productPrice;
    @SerializedName("coupon")
    @Expose
    private String coupon;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    private List<Other> otherList;

    JSONObject getProduct() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "product");
            jsonObject.put("id", productId);
            jsonObject.put("name", productName);
            jsonObject.put("sku", productSku);
            jsonObject.put("brand", productBrand);
            jsonObject.put("category", productCategory);
            jsonObject.put("variant", variant);
            jsonObject.put("seller_id", sellerId);
            jsonObject.put("image_url", productImageUrl);
            jsonObject.put("price", productPrice);
            jsonObject.put("url", productUrl);
            jsonObject.put("quantity", quantity);
            jsonObject.put("coupon", coupon);

            if (otherList != null && otherList.size() > 0) {
                for (Other other : otherList) {
                    jsonObject.put(other.key, other.value);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public float getProductPrice() {
        return productPrice;
    }

    public String getVariant() {
        return variant;
    }

    public static class Builder {

        private String productId;
        private String productName;
        private String productSku;
        private String productBrand;
        private String productCategory;
        private String variant;
        private String sellerId;
        private String productImageUrl;
        private String productUrl;
        private float productPrice;
        private String coupon;
        private int quantity;
        private List<Other> otherList;

        public Builder() {
        }

        public Builder(String productId, String productName, String productSku, String productBrand,
                       String productCategory, String variant, String sellerId,
                       String productImageUrl, String productUrl, float productPrice,
                       String coupon, int quantity, List<Other> otherList) {
            this.productId = productId;
            this.productName = productName;
            this.productSku = productSku;
            this.productBrand = productBrand;
            this.productCategory = productCategory;
            this.variant = variant;
            this.sellerId = sellerId;
            this.productImageUrl = productImageUrl;
            this.productUrl = productUrl;
            this.productPrice = productPrice;
            this.coupon = coupon;
            this.quantity = quantity;
            this.otherList = otherList;
        }

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder productSku(String productSku) {
            this.productSku = productSku;
            return this;
        }

        public Builder productBrand(String productBrand) {
            this.productBrand = productBrand;
            return this;
        }

        public Builder productCategory(String productCategory) {
            this.productCategory = productCategory;
            return this;
        }

        public Builder variant(String variant) {
            this.variant = variant;
            return this;
        }

        public Builder sellerId(String sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder productPrice(float productPrice) {
            this.productPrice = productPrice;
            return this;
        }

        public Builder productImageUrl(String productImageUrl) {
            this.productImageUrl = productImageUrl;
            return this;
        }

        public Builder coupon(String coupon) {
            this.coupon = coupon;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder otherList(List<Other> otherList) {
            this.otherList = otherList;
            return this;
        }


        public ProductItem build() {
            return new ProductItem(this);
        }

    }

    public ProductItem(Builder builder) {
        productId = builder.productId;
        productName = builder.productName;
        productSku = builder.productSku;
        productBrand = builder.productBrand;
        productCategory = builder.productCategory;
        variant = builder.variant;
        sellerId = builder.sellerId;
        productImageUrl = builder.productImageUrl;
        productUrl = builder.productUrl;
        productPrice = builder.productPrice;
        coupon = builder.coupon;
        quantity = builder.quantity;
        otherList = builder.otherList;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}