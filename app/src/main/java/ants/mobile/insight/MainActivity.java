package ants.mobile.insight;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ants.mobile.ants_insight.Constants.ActionEvent;
import ants.mobile.ants_insight.Insights;
import ants.mobile.ants_insight.Model.Dimension;
import ants.mobile.ants_insight.Model.ExtraItem;
import ants.mobile.ants_insight.Model.Other;
import ants.mobile.ants_insight.Model.ProductItem;
import ants.mobile.ants_insight.Model.UserItem;
import ants.mobile.insights.R;

public class MainActivity extends Activity implements Insights.InsightsCallBackListener {

    private TextView tvContent;
    private Insights insights;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvContent = findViewById(R.id.tvContent);


        tvContent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        Insights.InsightsCallBackListener callBack = new Insights.InsightsCallBackListener() {
            @Override
            public void onSuccess(JsonObject eventResponse) {

            }

            @Override
            public void onError(String error) {

            }
        };

        Other other = new Other("key", "value");

        List<Other> others = new ArrayList<>();
        others.add(new Other("key_1", "value"));
        others.add(new Other("key_2", "value_2"));
        others.add(new Other("key_n", "value_n"));

        ProductItem item = new ProductItem();
        item.setProductName("Iphone 11");
        item.setProductId("ID001");
        item.setProductImageUrl("https://ants.vn/vi/");
        item.setProductBrand("Phone/Smart Phone/Apple");
        item.setProductPrice(1900.2f);
        item.setProductSku("IP001");
        item.setProductUrl("https://www.apple.com/iphone-11/");
        item.setQuantity(1);
        item.setSellerId("SI021KQ");
        item.setOtherList(others);

        List<ProductItem> productList = new ArrayList<>();
        productList.add(item);

        List<Dimension> dimensionList = new ArrayList<>();
        dimensionList.add(new Dimension("warehouse", "ID023", "Ho Chi Minh"));
        dimensionList.add(new Dimension("payment", "ID12", "ANTSPay"));

        List<Other> otherList = new ArrayList<>();
        otherList.add(new Other("key_1", "value"));
        otherList.add(new Other("key_2", "value_2"));
        otherList.add(new Other("key_n", "value_n"));

        ExtraItem extraItem = new ExtraItem();
        extraItem.setOrderId("ID1");
        extraItem.setDeliveryCost(10000);
        extraItem.setDiscountAmount(5);
        extraItem.setPromotionCode("EZ19J");
        extraItem.setTax(10);
        extraItem.setRevenue(100000);
        extraItem.setSrcSearchTerm("phone");
        extraItem.setOthers(otherList);

        Insights insight = new Insights(this, callBack);
        insight.logEvent(ActionEvent.PURCHASE_ACTION, productList, extraItem, dimensionList);
        insight.deliveryEvent(ActionEvent.CHECKOUT_ACTION, productList, extraItem, dimensionList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            tvContent.setText("accept permission");
        }
    }

    @Override
    public void onSuccess(JsonObject eventResponse) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        insights.unregisterReceiver();
    }

}