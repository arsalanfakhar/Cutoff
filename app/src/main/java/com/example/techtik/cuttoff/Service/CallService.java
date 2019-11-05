package com.example.techtik.cuttoff.Service;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;

import com.example.techtik.cuttoff.Activity.CallScreenActivity;
import com.example.techtik.cuttoff.Util.CallManager;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        //launch intent to show call screen
        Intent intent=new Intent(this, CallScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        CallManager.mCall=call;

        //set the call variable
    }


    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);

        CallManager.mCall=null;
    }

}
