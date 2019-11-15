package com.example.techtik.cuttoff.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;

import com.example.techtik.cuttoff.R;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 3000;
    public static final int PERMISSION_RC = 10;
    public static final int DIALER_RC = 10;
    public static final String[] MUST_HAVE_PERMISSIONS = {CALL_PHONE, READ_CONTACTS, READ_CALL_LOG};
    public static SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref= getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        checkDefaultDialer();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(checkDefaultDialer())
//                    checkPermissions(null);
//
//
//            }
//        },SPLASH_TIMEOUT);

    }

    private void checkDefaultDialer(){

        String packageName = getApplication().getPackageName();
        TelecomManager telecomManager= (TelecomManager) getSystemService(TELECOM_SERVICE);

        String currentPackage=telecomManager.getDefaultDialerPackage();

        if(packageName.equals(currentPackage)){
            //TODO Implement method to check permissions
            //chk permissions
            startApp();
            return;
        }

        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName);
        startActivityForResult(intent, DIALER_RC);
    }



    public void startApp(){
        requestPermission();
        Intent loginIntent = new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    public void requestPermission(){
        if(ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){

        }
        else {
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.READ_CONTACTS},
                    0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==DIALER_RC){
            //chk
            if(resultCode==RESULT_OK){
                Log.v("requestResult","User accepted request to become default dialer");
                //TODO Implement method to check permissions
                startApp();
            }
            else if(resultCode==RESULT_CANCELED)
                Log.v("requestResult","User declined request to become default dialer");
            //TODO Implement method to make default app once more
        }

    }
}
