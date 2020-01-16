package com.example.techtik.cuttoff.Service;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.widget.Toast;

import com.example.techtik.cuttoff.Activity.CallScreenActivity;
import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.Util.CallManager;
import com.example.techtik.cuttoff.viewmodel.ComfortFragmentViewModel;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);


        CallManager.mCall=call;
        //check the state of the app

        Contact callingContact=CallManager.getDisplayContact(this);


        ComfortFragmentViewModel comfortFragmentViewModel=new ComfortFragmentViewModel(getApplication());
//        CustomRecordings customRecordings=new CustomRecordings();
//
//        customRecordings.setmContact(callingContact);
//        customRecordings.setmCustomMessage("asdsa");
//        comfortFragmentViewModel.addRecording(customRecordings) ;


//        boolean app_state=SplashActivity.pref.getBoolean("app_state",false);
        boolean app_state =getApplicationContext().getSharedPreferences("MyPref", 0).getBoolean("app_state",false);

        int call_state=CallManager.getState();
        if(app_state && call_state==Call.STATE_RINGING){

            //Check custom status for contact
            String custom_message=comfortFragmentViewModel.getCustomMessageByContact(callingContact.getName(),callingContact.getPhones(),callingContact.getPhotoUri());
            if(custom_message!=null) {
                //send the message
                CallManager.rejectWithMessage(custom_message);
            }
            else {
                //Else Get default status
                long status_id=getApplicationContext().getSharedPreferences("MyPref", 0).getLong("active_recording_id",1);
                String mess=comfortFragmentViewModel.getDefaultMessageById(status_id);
                if(mess!=null){
                    Log.v("mess_val",mess);
                    CallManager.rejectWithMessage(mess);
                }
                else {
                    Log.v("error getting value",mess);
                }

            }



            //disconnect the call and send message

        }
        else {
            //launch intent to show call screen
            Intent intent=new Intent(this, CallScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        //set the call variable

    }


    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);

        CallManager.mCall=null;
    }

}
