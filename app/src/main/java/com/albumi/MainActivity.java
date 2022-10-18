package com.albumi;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runSplash();
        //runDebug();
        //plug();
    }

    //==============================================================================================
    private void runSplash() {
        findViewById(R.id.logo).animate().alpha(0f).setDuration(1500);

        Intent intent = new Intent(this, LoaderActivity.class);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity(intent);
                overridePendingTransition(R.anim.up, R.anim.down);
                finish();
            }
        }, 2500);
    }
    //==============================================================================================
    private void runDebug() {
        findViewById(R.id.logo).animate().alpha(0f).setDuration(1500);

        Intent intent = new Intent(this, LoaderActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.up, R.anim.down);
                finish();
    }
    //==============================================================================================
    private void plug() {
        Intent intent = new Intent(this, PlugActivity.class);
        startActivity(intent);
        finish();
    }
    //==============================================================================================
}