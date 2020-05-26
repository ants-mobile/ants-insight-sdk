package ants.mobile.insight;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ants.mobile.ants_insight.Constants.ActionEvent;
import ants.mobile.ants_insight.Insights;
import ants.mobile.ants_insight.Model.Dimension;
import ants.mobile.ants_insight.Model.ExtraItem;
import ants.mobile.ants_insight.Model.Other;
import ants.mobile.ants_insight.Model.ProductItem;
import ants.mobile.insights.R;

public class MainActivity extends Activity {

    private TextView tvGoToHomeActivity;
    private TextView tvShowAds;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvGoToHomeActivity = findViewById(R.id.btn_go_to_home_activity);
        tvShowAds = findViewById(R.id.btn_show_ads);

        tvGoToHomeActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        Insights.initialization(this);
        Insights insights = new Insights(this);

        List<Other> otherList = new ArrayList<>();
        otherList.add(new Other("key_1", "value"));
        otherList.add(new Other("key_2", "value_2"));
        otherList.add(new Other("key_n", "value_n"));

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
        item.setOtherList(otherList);

        List<ProductItem> productList = new ArrayList<>();
        productList.add(item);

        List<Dimension> dimensionList = new ArrayList<>();
        dimensionList.add(new Dimension("warehouse", "ID023", "Ho Chi Minh"));
        dimensionList.add(new Dimension("payment", "ID12", "ANTSPay"));

        ExtraItem extraItem = new ExtraItem();
        extraItem.setOrderId("ID1");
        extraItem.setDeliveryCost(10000);
        extraItem.setDiscountAmount(5);
        extraItem.setPromotionCode("EZ19J");
        extraItem.setTax(10);
        extraItem.setRevenue(100000);
        extraItem.setSrcSearchTerm("phone");
        extraItem.setOthers(otherList);


        insights.logEvent(ActionEvent.CART_VIEW, productList, extraItem, dimensionList);
        tvShowAds.setOnClickListener(v -> insights.resetAnonymousId());


//        WebView webView = new WebView(this);
//        webView.loadUrl("https://www.google.com/");
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 800);
//        webView.setLayoutParams(params);
//
//
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setCancelable(false);
//        AlertDialog dialog = alert.create();
//        dialog.setContentView(webView);
//        dialog.setView(webView);
//        dialog.setCanceledOnTouchOutside(true);
//        Window window = dialog.getWindow();
//
//        WindowManager.LayoutParams wlp = window.getAttributes();
//
//        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        wlp.height = 300;
//
//        window.setAttributes(wlp);
//        dialog.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            tvGoToHomeActivity.setText("accept permission");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void createAlertDialog(final Activity activity, View view) {

        final AlertDialog dialog = new AlertDialog.Builder(activity).setCancelable(true).create();
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        if (lp != null) {

            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = 400;
            lp.gravity = Gravity.TOP;
            dialog.getWindow().setAttributes(lp);
        }
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view);


    }

}