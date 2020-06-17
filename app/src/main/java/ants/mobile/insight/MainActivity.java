package ants.mobile.insight;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ants.mobile.ants_insight.Constants.Event;
import ants.mobile.ants_insight.Insights;
import ants.mobile.ants_insight.Model.Dimension;
import ants.mobile.ants_insight.Model.ExtraItem;
import ants.mobile.ants_insight.Model.Other;
import ants.mobile.ants_insight.Model.ProductItem;
import ants.mobile.insights.R;

public class MainActivity extends Activity {

    private TextView tvGoToHomeActivity;
    private TextView tvShowAds;
    private Insights insights;

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
        insights = new Insights.Builder().getContext(this).build();

        List<Other> otherList = new ArrayList<>();
        otherList.add(new Other("key_1", "value"));
        otherList.add(new Other("key_2", "value_2"));
        otherList.add(new Other("key_n", "value_n"));

        ProductItem item = new ProductItem.Builder().productId("Adadad").
                productName("Adadad").coupon("adad").otherList(otherList).build();

        List<ProductItem> productList = new ArrayList<>();
        productList.add(item);

        List<Dimension> dimensionList = new ArrayList<>();
        dimensionList.add(new Dimension("warehouse", "ID023", "Ho Chi Minh"));
        dimensionList.add(new Dimension("payment", "ID12", "ANTSPay"));

        ExtraItem extraItem = new ExtraItem.Builder().orderId("").deliveryCost(3).build();

        insights.logEvent(Event.IMPRESSION, productList, extraItem, dimensionList);
        tvShowAds.setOnClickListener(v -> insights.logEvent(Event.VIEW_CART, productList, extraItem, dimensionList));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            tvGoToHomeActivity.setText("accept permission");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        insights.unregisterReceiver();
    }

    private void createAlertDialog() {

        AlertDialog insightDialog = new AlertDialog.Builder(this).setCancelable(true).create();
        insightDialog.show();
        WindowManager.LayoutParams lp = insightDialog.getWindow().getAttributes();
        if (lp != null) {

            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT / 3;
            lp.gravity = Gravity.CENTER;
            insightDialog.getWindow().setAttributes(lp);
        }
        insightDialog.setCanceledOnTouchOutside(false);
        insightDialog.getWindow().setAttributes(lp);
        insightDialog.getWindow().setBackgroundDrawableResource(ants.mobile.ants_insight.R.color.TRANSPARENT);
        insightDialog.setContentView(R.layout.adx_template_center);

    }

}