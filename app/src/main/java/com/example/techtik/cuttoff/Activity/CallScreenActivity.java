package com.example.techtik.cuttoff.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telecom.Call;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.CallManager;
import com.example.techtik.cuttoff.Util.Stopwatch;
import com.example.techtik.cuttoff.databinding.OnGoingCallBinding;

public class CallScreenActivity extends AppCompatActivity {


    // Current states
    boolean mIsCallingUI = false;
    boolean mIsCreatingUI = true;

    // Utilities
    Stopwatch mCallTimer = new Stopwatch();
    Callback mCallback=new Callback();

    // PowerManager
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    // Audio
    AudioManager mAudioManager;

    OnGoingCallBinding onGoingCallBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_call_screen);
        DataBindingUtil.setContentView(this,R.layout.activity_call_screen);
        //call binding
        onGoingCallBinding=DataBindingUtil.setContentView(this,R.layout.on_going_call);

        //onGoingCallBinding.textStopwatch.setText("Billy here");

        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) {
                km.requestDismissKeyguard(this, null);
            }
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        CallManager.registerCallback(mCallback);

    }

    /**
     * Updates the ui given the call state
     *
     * @param state the current call state
     */




    /**
     * Callback class
     * Listens to the call and do stuff when something changes
     */
    public class Callback extends Call.Callback{

        @Override
        public void onStateChanged(Call call, int state) {
            /*
              Call states:
              1   = Call.STATE_DIALING
              2   = Call.STATE_RINGING
              3   = Call.STATE_HOLDING
              4   = Call.STATE_ACTIVE
              7   = Call.STATE_DISCONNECTED
              8   = Call.STATE_SELECT_PHONE_ACCOUNT
              9   = Call.STATE_CONNECTING
              10  = Call.STATE_DISCONNECTING
              11  = Call.STATE_PULLING_CALL
             */

            super.onStateChanged(call, state);
            Log.v("State changed:",String.valueOf(state));
            //TODO update UI method


        }

        @Override
        public void onDetailsChanged(Call call, Call.Details details) {
            super.onDetailsChanged(call, details);
            Log.v("Details changed",details.toString());
        }

    }

}
