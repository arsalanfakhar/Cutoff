package com.example.techtik.cuttoff.Util;

import android.content.Context;
import android.net.Uri;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.widget.Toast;

import com.example.techtik.cuttoff.Activity.CallScreenActivity;
import com.example.techtik.cuttoff.Models.Contact;

public class CallManager {
    public static Call mCall;
    private static boolean sIsAutoCalling = false;
    private static int sAutoCallPosition = 0;

    // -- Call Actions -- //

    //Answer incoming call
    public static void answer(){
        if(mCall!=null){
            mCall.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    /**
     * Ends call
     * If call ended from the other side, disconnects
     *
     * @return true whether there's no more calls awaiting
     */
    public static void reject(){
       if(mCall!=null){
           if(mCall.getState()==Call.STATE_RINGING){
               //TODO Check whether you can send a message here
               mCall.reject(false,null);
           }
           else {
               mCall.disconnect();
           }
           if(sIsAutoCalling)
               sAutoCallPosition++;

       }
    }

    /**
     * Put call on hold
     *
     * @param hold
     */
    public static void hold(boolean hold){
        if(mCall!=null){
            if(hold)
                mCall.hold();
            else
                mCall.unhold();
        }
    }

    /**
     * Open keypad
     *
     * @param c
     */
    public static void keypad(char c) {
        if (mCall != null) {
            mCall.playDtmfTone(c);
            mCall.stopDtmfTone();
        }
    }

    /**
     * Add a call to the current call
     *
     * @param call
     */
    public static void addCall(Call call){
        if(mCall!=null)
            mCall.conference(call);
    }

    /**
     * Registers a Callback object to the current call
     *
     * @param callback the callback to register
     */
    public static void registerCallback(CallScreenActivity.Callback callback){
        if(mCall==null)
            return;
        mCall.registerCallback(callback);
    }

    /**
     * Unregisters the Callback from the current call
     *
     * @param callback the callback to unregister
     */
    public static void unregisterCallback(Call.Callback callback) {
        if (mCall == null)
            return;
        mCall.unregisterCallback(callback);
    }

    // -- Getters -- //

    /**
     * Gets the phone number of the contact from the end side of the current call
     * in the case of a voicemail number, returns "Voicemail"
     *
     * @return String - phone number, or voicemail. if not recognized, return null.
     */
    public static Contact getDisplayContact(Context context){
        String uri=null;

        if(mCall.getState()==Call.STATE_DIALING){
            Toast.makeText(context, "Dialing", Toast.LENGTH_LONG).show();
        }

        if (mCall.getDetails().getHandle() != null) {
            uri = Uri.decode(mCall.getDetails().getHandle().toString());// Callers details

        }

        if (uri != null && uri.isEmpty()) return ContactUtils.UNKNOWN;

        // If uri contains 'voicemail' this is a... voicemail dah
        if (uri.contains("voicemail")) return
                ContactUtils.VOICEMAIL;

        String telephoneNumber = null;

        // If uri contains 'tel' this is a normal number
        if (uri.contains("tel:")) telephoneNumber = uri.replace("tel:", "");

        if (telephoneNumber == null || telephoneNumber.isEmpty())
            return ContactUtils.UNKNOWN; // Unknown number

        if (telephoneNumber.contains(" ")) telephoneNumber = telephoneNumber.replace(" ", "");

        Contact contact = ContactUtils.getContactByPhoneNumber(context, telephoneNumber); // Get the contacts with the number

        if (contact == null || contact.getName().isEmpty())
            return new Contact(telephoneNumber, telephoneNumber, null); // No known contacts for the number, return the number

        return contact;

    }



    /**
     * Returnes the current state of the call from the Call object (named mCall)
     *
     * @return Call.State
     */
    public static int getState(){
        if(mCall==null)
            return Call.STATE_DISCONNECTED;
        return mCall.getState();
    }

}
