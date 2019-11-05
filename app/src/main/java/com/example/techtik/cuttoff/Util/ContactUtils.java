package com.example.techtik.cuttoff.Util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.techtik.cuttoff.Models.Contact;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class ContactUtils {

    //Constants
    public static final Contact UNKNOWN=new Contact("Unknown","",null);
    public static final Contact VOICEMAIL=new Contact("Voicemail","",null);
    public static final Contact ERROR=new Contact("Error","",null);


    /**
     * Returns a contact by a given phone number
     *
     * @param context
     * @param phoneNumber
     * @return Contact
     */
     public static Contact getContactByPhoneNumber(@NonNull Context context,@NonNull String phoneNumber){

         if(phoneNumber.isEmpty()) return null;

         //Check for permission to read contacts
         if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
             //Don't prompt the user now, they are getting a call
             return null;
         }

         Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
         String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI};
         Contact contact;

         Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
         if (cursor == null)
             return null;
         if (cursor.moveToFirst()) {
             contact = new Contact(cursor.getString(0), phoneNumber, cursor.getString(1));
         } else {
             contact = new Contact(null, phoneNumber, null);
             return contact;
         }
         cursor.close();

         return contact;
     }

}
