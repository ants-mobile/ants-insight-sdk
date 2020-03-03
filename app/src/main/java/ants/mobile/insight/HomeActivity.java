package ants.mobile.insight;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


import com.google.gson.JsonObject;

import ants.mobile.ants_insight.Insights;
import ants.mobile.insights.R;

public class HomeActivity extends Activity implements Insights.InsightsCallBackListener {

    private TextView tvContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void onSuccess(JsonObject eventResponse) {

    }

    @Override
    public void onError(String error) {

    }
}
