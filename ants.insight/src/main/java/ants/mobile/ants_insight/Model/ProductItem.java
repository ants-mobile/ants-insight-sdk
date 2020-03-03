package ants.mobile.ants_insight.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ProductItem {
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

    public ProductItem() {
    }

    public JSONObject getProduct() {
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

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public void setProductUrl(String mUrl) {
        this.productUrl = mUrl;
    }

    public void setProductPrice(float productPrice) {
        this.productPrice = productPrice;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<Other> getOtherList() {
        return otherList;
    }

    public void setOtherList(List<Other> otherList) {
        this.otherList = otherList;
    }
}