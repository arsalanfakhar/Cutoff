package com.example.techtik.cuttoff.Models;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.example.techtik.cuttoff.Util.ContactUtils;

import java.text.DateFormat;
import java.util.Date;

public class RecentCall {

    // Attributes
    private Context mContext;
    private String mCallerName;
    private String mNumber;
    private int mCallType;
    private String mCallDuration;
    private Date mCallDate;
    private String mCallPhotoUri;

    // Call Types
    public static final int mOutgoingCall = CallLog.Calls.OUTGOING_TYPE;
    public static final int mIncomingCall = CallLog.Calls.INCOMING_TYPE;
    public static final int mMissedCall = CallLog.Calls.MISSED_TYPE;

    /**
     * Constructor
     *
     * @param number   caller's number
     * @param type     call's type (out/in/missed)
     * @param duration call's duration
     * @param date     call's date
     */
    public RecentCall(Context context, String number, int type, String duration, Date date) {
        this.mContext = context;
        this.mNumber = number;
        this.mCallerName = ContactUtils.getContactByPhoneNumber(this.mContext, number).getName();
        this.mCallType = type;
        this.mCallDuration = duration;
        this.mCallDate = date;
    }

    public RecentCall(Context context, Cursor cursor) {
        this.mContext = context;
        mNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        String callerName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
        if ((callerName == null || callerName.isEmpty()) && mNumber != null) {
            Contact contact = ContactUtils.getContactByPhoneNumber(context, mNumber);
            if (contact != null) {
                mCallerName = contact.getName();
//                mCallPhotoUri=contact.getPhotoUri();
            }
        } else {
            mCallerName = callerName;
        }
        mCallDuration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
        mCallDate = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
        mCallType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
        mCallPhotoUri=cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI));
    }

    public String getCallerName() {
        return this.mCallerName;
    }

    public String getCallerNumber() {
        return this.mNumber;
    }

    public int getCallType() {
        return this.mCallType;
    }

    public String getCallDuration() {
        return this.mCallDuration;
    }

    public Date getCallDate() {
        return this.mCallDate;
    }

    public String getCallDateString() {
        DateFormat dateFormat = DateFormat.getDateInstance();
        return dateFormat.format(this.mCallDate);
    }

    public String getCallTime(){
        String time=DateFormat.getTimeInstance(DateFormat.SHORT).format(this.mCallDate);

        //if time is 5:55pm it is formatted to 05:55pm
        String str=time.substring(0,2);
        if(!str.equals("10") && !str.equals("11") && !str.equals("12")){
            String str2="0"+str.substring(0,1);
            return str2+time.substring(1);
        }

        //else time is ok to return
        return time;
    }

    public String getCallerPhoto(){
        return this.mCallPhotoUri;
    }

}

