package ants.mobile.insight;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;


import androidx.annotation.RequiresApi;

import com.google.gson.JsonObject;

import ants.mobile.ants_insight.Constants.ActionEvent;
import ants.mobile.ants_insight.Insights;
import ants.mobile.ants_insight.Model.DeliveryResponse;
import ants.mobile.insights.R;

public class HomeActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_popup_windown_custom);

    }

}
