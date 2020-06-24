package ants.mobile.insight;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    List<ProductItem> productItems;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}