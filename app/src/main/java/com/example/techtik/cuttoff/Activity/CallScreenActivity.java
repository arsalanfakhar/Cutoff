package com.example.techtik.cuttoff.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.R;
import com.example.techtik.cuttoff.Util.CallManager;
import com.example.techtik.cuttoff.Util.Stopwatch;
import com.example.techtik.cuttoff.databinding.OnGoingCallBinding;

public class CallScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long END_CALL_MILLIS = 1500;

    // Handler variables
    private static final int TIME_START = 1;
    private static final int TIME_STOP = 0;
    private static final int TIME_UPDATE = 2;
    private static final int REFRESH_RATE = 100;
    private static int mState;


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

    // Handlers
    Handler mCallTimeHandler = new CallTimeHandler();

    OnGoingCallBinding onGoingCallBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_call_screen);
        DataBindingUtil.setContentView(this,R.layout.activity_call_screen);
        //call binding
        onGoingCallBinding=DataBindingUtil.setContentView(this,R.layout.on_going_call);

        //onGoingCallBinding.textStopwatch.setText("Billy here");

        // This activity needs to show even if the screen is off or locked

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

        //Hide the navigation bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        // Display caller information
        displayInformation();

        // Initiate PowerManager and WakeLock (turn screen on/off according to distance from face)
        try {
            field=PowerManager.class.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        }catch (Throwable ignored) {

        }

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());

        // Audio Manager
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);

        //Click listeners
        onGoingCallBinding.answerBtn.setOnClickListener(this);
        onGoingCallBinding.rejectBtn.setOnClickListener(this);
        onGoingCallBinding.buttonMute.setOnClickListener(this);
        onGoingCallBinding.buttonSpeaker.setOnClickListener(this);
        onGoingCallBinding.buttonHold.setOnClickListener(this);
    }


    // -- Call Actions -- //

    /**
     * /*
     * Answers incoming call and changes the ui accordingly
     */
    private void activateCall(){
        CallManager.answer();
        switchToCallingUI();
    }

    /**
     * End current call / Incoming call and changes the ui accordingly
     */
    private void endCall(){
       mCallTimeHandler.sendEmptyMessage(TIME_STOP);
       CallManager.reject();
       releaseWakeLock();

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
                finish();
           }
       },END_CALL_MILLIS);

    }

    /**
     * Updates the ui given the call state
     *
     * @param state the current call state
     */
    private void updateUI(int state){
        @StringRes int statusTextRes;
        switch (state) {
            case Call.STATE_ACTIVE: // Ongoing
                statusTextRes = R.string.status_call_active;
                break;
            case Call.STATE_DISCONNECTED: // Ended
                statusTextRes = R.string.status_call_disconnected;
                break;
            case Call.STATE_RINGING: // Incoming
                statusTextRes = R.string.status_call_incoming;
                //showBiometricPrompt(this);
                break;
            case Call.STATE_DIALING: // Outgoing
            case Call.STATE_CONNECTING: // Connecting (probably outgoing)
                statusTextRes = R.string.status_call_dialing;
                break;
            case Call.STATE_HOLDING: // On Hold
                statusTextRes = R.string.status_call_holding;
                break;
            default:
                statusTextRes = R.string.status_call_active;
                break;
        }
        onGoingCallBinding.textStatus.setText(statusTextRes);
        if (state != Call.STATE_RINGING && state != Call.STATE_DISCONNECTED)
            switchToCallingUI();
        if (state == Call.STATE_DISCONNECTED)
            endCall();
        mState=state;
    }


    /**
     * Switches the ui to an active call ui.
     */
    private void switchToCallingUI(){
        if(mIsCallingUI)
            return;
        else
            mIsCallingUI=true;

        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        acquireWakeLock();
        mCallTimeHandler.sendEmptyMessage(TIME_START); // Starts the call timer

        // Change the buttons layout
        onGoingCallBinding.answerBtn.hide();
        onGoingCallBinding.buttonHold.setVisibility(View.VISIBLE);
        onGoingCallBinding.buttonMute.setVisibility(View.VISIBLE);
        onGoingCallBinding.buttonKeypad.setVisibility(View.VISIBLE);
        onGoingCallBinding.buttonSpeaker.setVisibility(View.VISIBLE);
        onGoingCallBinding.buttonAddCall.setVisibility(View.VISIBLE);
        moveRejectButtonToMiddle();

    }

    /**
     * Moves the reject button to the middle
     */
    private void moveRejectButtonToMiddle(){
        ConstraintSet ongoingSet = new ConstraintSet();
        ongoingSet.clone(onGoingCallBinding.ongoingCallLayout);
        ongoingSet.connect(R.id.reject_btn,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END);
        ongoingSet.connect(R.id.reject_btn, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START);
        ongoingSet.setHorizontalBias(R.id.reject_btn, 0.5f);

        ongoingSet.setMargin(R.id.reject_btn, ConstraintSet.END, 0);

        if (!mIsCreatingUI) { //Don't animate if the activity is just being created
            Transition transition = new ChangeBounds();
            transition.setInterpolator(new AccelerateDecelerateInterpolator());
            transition.addTarget(onGoingCallBinding.rejectBtn);
            TransitionManager.beginDelayedTransition(onGoingCallBinding.ongoingCallLayout, transition);
        }

        ongoingSet.applyTo(onGoingCallBinding.ongoingCallLayout);
        //TODO complete this function implementation

    }

    /**
     * To disable back button
     */
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsCreatingUI=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallManager.unregisterCallback(mCallback);//The activity is gone, no need to listen to changes

        //missing
        releaseWakeLock();

    }

    /**
     * Turns on mute according to current state (if already on/off)
     *
     * @param v
     */
    private void toggleMute(View v){
        /**
         * Toggle the active state of a view
         */
        v.setActivated(!v.isActivated());
        if(v.isActivated()){
            onGoingCallBinding.buttonMute.setImageResource(R.drawable.ic_mic_off_black_24dp);
        }
        else {
            onGoingCallBinding.buttonMute.setImageResource(R.drawable.ic_mic_black_24dp);
        }
        mAudioManager.setMicrophoneMute(v.isActivated());
    }
    /**
     * Turns on/off the speaker according to current state (if already on/off)
     *
     * @param view
     */
    private void toggleSpeaker(View view) {
        view.setActivated(!view.isActivated());
        mAudioManager.setSpeakerphoneOn(view.isActivated());
    }

    /**
     * Puts the call on hold
     *
     * @param view
     */
    private void toggleHold(View view){
        view.setActivated(!view.isActivated());
        CallManager.hold(view.isActivated());
    }

    // -- Wake Lock -- //

    /**
     * Acquires the wake lock
     */
    private void acquireWakeLock() {
        if (!wakeLock.isHeld()) {
            Toast.makeText(CallScreenActivity.this,"Wake lock acquired",Toast.LENGTH_SHORT).show();
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }
    }

    /**
     * Releases the wake lock
     */
    private void releaseWakeLock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        CallManager.registerCallback(mCallback);
        updateUI(CallManager.getState());
    }

    // -- UI -- //

    private void displayInformation(){
        // Display the information about the caller
         Contact callerContact=CallManager.getDisplayContact(this);
         if(!callerContact.getName().isEmpty()){
            if(callerContact.getName()!=null)
                onGoingCallBinding.textCaller.setText(callerContact.getName());
            if(callerContact.getPhotoUri()!=null){
                onGoingCallBinding.imagePlaceholder.setVisibility(View.INVISIBLE);
                onGoingCallBinding.imagePhoto.setVisibility(View.VISIBLE);
                onGoingCallBinding.imagePhoto.setImageURI(Uri.parse(callerContact.getPhotoUri()));

            }
         }
         else {
            //TODO get main phone number
         }

    }

    @SuppressLint("HandlerLeak")
    class CallTimeHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TIME_START:
                    mCallTimer.start();
                    mCallTimeHandler.sendEmptyMessage(TIME_UPDATE); // Starts the time ui updates
                    break;
                case TIME_STOP:
                    mCallTimeHandler.removeMessages(TIME_UPDATE); // No more updates
                    mCallTimer.stop(); // Stops the timer
                    updateTimeUI(); // Updates the time ui
                    break;
                case TIME_UPDATE:
                    updateTimeUI(); // Updates the time ui
                    mCallTimeHandler.sendEmptyMessageDelayed(TIME_UPDATE, REFRESH_RATE); // Text view updates every milisecond (REFRESH RATE)
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.answer_btn:
                Toast.makeText(CallScreenActivity.this,"Call activated",Toast.LENGTH_SHORT).show();
                activateCall();
                break;
            case R.id.reject_btn:
                endCall();
                break;
            case R.id.button_mute:
                toggleMute(v);
                break;
            case R.id.button_speaker:
                toggleSpeaker(v);
                break;
            case R.id.button_hold:
                toggleHold(v);
                break;
        }
    }

    /**
     * Update the current call time ui
     */
    private void updateTimeUI(){
        onGoingCallBinding.textStopwatch.setText(mCallTimer.getStringTime());
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
            updateUI(state);

        }

        @Override
        public void onDetailsChanged(Call call, Call.Details details) {
            super.onDetailsChanged(call, details);
            Log.v("Details changed",details.toString());
        }

    }

}