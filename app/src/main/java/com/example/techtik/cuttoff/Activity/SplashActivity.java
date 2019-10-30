package com.example.techtik.cuttoff.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.example.techtik.cuttoff.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermission();
                Intent loginIntent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        },SPLASH_TIMEOUT);

    }
    public void requestPermission(){
        if(ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){

        }
        else {
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.READ_CONTACTS},
                    0);
        }
    }
}
