package com.example.techtik.cuttoff.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.util.Log;

import com.example.techtik.cuttoff.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000;
    public static final int PERMISSION_RC = 0;
    public static final int DIALER_RC = 10;
    public static final String[] MUST_HAVE_PERMISSIONS = {Manifest.permission.READ_CONTACTS, CALL_PHONE, READ_CALL_LOG,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.RECORD_AUDIO, CALL_PHONE};
    public static SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref= getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        checkDefaultDialer();

        if(checkAndRequestPermissions()){
            //start the app
            startApp();
        }

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

    private boolean checkAndRequestPermissions(){
        boolean dialer_val=checkDefaultDialer();
        boolean permission_val;
        if(dialer_val){
            permission_val=checkPermission();
            if(permission_val)
                return true;
        }

        return false;

    }

    private boolean checkDefaultDialer(){

        String packageName = getApplication().getPackageName();
        TelecomManager telecomManager= (TelecomManager) getSystemService(TELECOM_SERVICE);

        String currentPackage=telecomManager.getDefaultDialerPackage();

        if(packageName.equals(currentPackage)){
            //TODO Implement method to check permissions
            //chk permissions
            //startApp();
            return true;
        }

        //if no permission then ask for it
        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName);
        startActivityForResult(intent, DIALER_RC);
        return false;
    }

    public boolean checkPermission(){
//        if(ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){
//
//        }
//        else {
//            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.READ_CONTACTS},
//                    0);
//        }

        //Check which permissions are granted
        List<String> listPermissionNeeded=new ArrayList<>();
        for(String perm:MUST_HAVE_PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(SplashActivity.this,perm)!=PackageManager.PERMISSION_GRANTED){
                listPermissionNeeded.add(perm);
            }
        }

        String[] arr= (String[]) listPermissionNeeded.toArray();

        //Ask for non granted permissions
        if(!listPermissionNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    PERMISSION_RC);
            return false;
        }

        //App has all permissions
        return true;
    }



    public void startApp(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(loginIntent);
                finish();
            }
        },SPLASH_TIMEOUT);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==DIALER_RC){
            //chk
            if(resultCode==RESULT_OK){
                Log.v("requestResult","User accepted request to become default dialer");
                //TODO Implement method to check permissions

                //check for permissions
                boolean permission_val=checkPermission();
                if(permission_val)
                    startApp();
            }
            else if(resultCode==RESULT_CANCELED)
                Log.v("requestResult","User declined request to become default dialer");
            //TODO Implement method to make default app once more
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_RC){

            HashMap<String,Integer> permissionResults=new HashMap<>();
            int deniedCount=0;

            //Gather permission grant results

            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                    permissionResults.put(permissions[i],grantResults[i]);
                    deniedCount++;
                }
            }

            //Check if all permissions are granted

            if(deniedCount==0){
                //Start the app
                startApp();
            }

            //At least one or all permission is denied
            else{
                for(Map.Entry<String,Integer> entry:permissionResults.entrySet()){

                    String perName=entry.getKey();
                    int permResult=entry.getValue();

                    //permission is denied and never ask again is not checked
                    //so we will ask explaining the usage of permission
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,perName)){
                        showDialog("", "This app needs permissions to function properly.",
                                "Yes, Grant permissions",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    checkPermission();
                                }, "No Exit App", (dialog, which) -> {
                                    dialog.dismiss();
                                    finish();
                                },false);
                    }
                    //permissions is denied and never ask again is checked
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        showDialog("", "You have denied some permissions . Allow all permissions at Settings",
                                "Go to Settings",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    //Open app settings
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                }, "No exit App",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    finish();
                                },false);
                        break;
                    }

                }
            }

        }
    }

    private AlertDialog showDialog(String title, String msg, String positiveLabel,
                                   DialogInterface.OnClickListener onPositiveClick,
                                   String negativeLabel, DialogInterface.OnClickListener onNegativeClick,
                                   boolean isCancellable){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancellable);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel,onPositiveClick);
        builder.setNegativeButton(negativeLabel,onNegativeClick);

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        return alertDialog;

    }

}
