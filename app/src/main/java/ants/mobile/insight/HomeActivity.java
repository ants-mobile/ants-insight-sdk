package ants.mobile.insight;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.RequiresApi;

import ants.mobile.insights.R;

public class HomeActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adx_template_center);
    }

}
