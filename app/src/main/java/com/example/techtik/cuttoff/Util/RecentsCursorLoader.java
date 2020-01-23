package com.example.techtik.cuttoff.Util;

import android.content.Context;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import java.util.Date;

import androidx.loader.content.CursorLoader;

public final class RecentsCursorLoader extends CursorLoader {

    public static String CURSOR_NAME_COLUMN = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY;
    public static String CURSOR_NUMBER_COLUMN = ContactsContract.CommonDataKinds.Phone.NUMBER;

    /**
     * Cursor selection string
     */
    public static String[] RECENTS_PROJECTION_DISPLAY_NAME_PRIMARY =
            new String[]{
                    CallLog.Calls._ID,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.CACHED_PHOTO_URI,
                    CallLog.Calls.LAST_MODIFIED
            };

    private static String RECENTS_ORDER = CallLog.Calls.DATE + " DESC";

    /**
     * Constructor
     *
     * @param context
     * @param phoneNumber String
     * @param contactName String
     */
    public RecentsCursorLoader(Context context, String phoneNumber, String contactName) {
        super(
                context,
                buildUri(phoneNumber, contactName),
                RECENTS_PROJECTION_DISPLAY_NAME_PRIMARY,
                getSelection(contactName, phoneNumber),
                null,
                RECENTS_ORDER);
    }


    public RecentsCursorLoader(Context context,String phoneNumber, String contactName, String timestamp) {
        super(
                context,
                buildUriRecent(),
                RECENTS_PROJECTION_DISPLAY_NAME_PRIMARY,
                CallLog.Calls.DATE + ">= ?",
                new String[]{timestamp},
                RECENTS_ORDER);

    }


    private static String getSelection(String contactName, String phoneNumber) {
        if (contactName != null && !contactName.isEmpty())
            return CallLog.Calls.CACHED_NAME + " LIKE '%" + contactName + "%'";
        else if (phoneNumber != null && !phoneNumber.isEmpty())
            return CallLog.Calls.NUMBER + " LIKE '%" + phoneNumber + "%'";
        else return null;
    }


//    private static String getDateSelection(Long date){
//        if(date!=null)
//            return CallLog.Calls.DATE+"='01/18/2019'";
//        return null;
//    }

    /**
     * Builds contact uri by given name and phone number
     *
     * @param phoneNumber
     * @param contactName
     * @return Builder.build()
     */
    private static Uri buildUri(String phoneNumber, String contactName) {
        Uri.Builder builder;
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            builder = Uri.withAppendedPath(CallLog.Calls.CONTENT_FILTER_URI, Uri.encode(phoneNumber)).buildUpon();
            builder.appendQueryParameter(ContactsContract.STREQUENT_PHONE_ONLY, "true");
        } else {
            builder = CallLog.Calls.CONTENT_URI.buildUpon();
        }

        return builder.build();
    }

    private static Uri buildUriRecent() {
        Uri.Builder builder;
        builder = CallLog.Calls.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES, "true");
        return builder.build();
    }

}
