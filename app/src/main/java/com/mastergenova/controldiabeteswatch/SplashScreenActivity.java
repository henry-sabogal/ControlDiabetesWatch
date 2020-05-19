package com.mastergenova.controldiabeteswatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class SplashScreenActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enables Always-on
        setAmbientEnabled();

        startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
        finish();
    }
}
