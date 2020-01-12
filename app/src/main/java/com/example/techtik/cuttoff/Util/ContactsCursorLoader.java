package com.example.techtik.cuttoff.Util;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.loader.content.CursorLoader;

public class ContactsCursorLoader extends CursorLoader {

    public static String CURSOR_NAME_COLUMN = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY;
    public static String CURSOR_NUMBER_COLUMN = ContactsContract.CommonDataKinds.Phone.NUMBER;

    /**
     * Cursor selection string
     */
    public static final String[] CONTACTS_PROJECTION_DISPLAY_NAME_PRIMARY =
            new String[]{
                    ContactsContract.CommonDataKinds.Phone._ID, // 0
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY, // 1
                    ContactsContract.CommonDataKinds.Phone.PHOTO_ID, // 2
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, // 3
                    ContactsContract.CommonDataKinds.Phone.NUMBER, // 4
            };

    /**
     * Constructor
     *
     * @param context
     * @param phoneNumber String
     * @param contactName String
     */
    public ContactsCursorLoader(Context context, String phoneNumber, String contactName) {
        super(
                context,
                buildUri(phoneNumber, contactName),
                getProjection(context),
                getWhere(context),
                null,
                getSortKey(context) + " ASC");
    }

    /**
     * Returns the projection string
     *
     * @param context
     * @return String
     */
    private static String[] getProjection(Context context) {
        return CONTACTS_PROJECTION_DISPLAY_NAME_PRIMARY;
    }

    /**
     * Get a filter string
     *
     * @param context
     * @return String
     */
    private static String getWhere(Context context) {
        return "(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " IS NOT NULL" +
                " OR " + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE + " IS NOT NULL" + ")" +
                " AND " + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1" +
                " AND (" + ContactsContract.RawContacts.ACCOUNT_NAME + " IS NULL" +
                " OR ( " + ContactsContract.RawContacts.ACCOUNT_TYPE + " NOT LIKE '%whatsapp%'" +
                " AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " NOT LIKE '%tachyon%'" + "))";
    }

    private static String getSortKey(Context context) {
        return ContactsContract.Contacts.SORT_KEY_PRIMARY;
    }

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
            builder = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(phoneNumber)).buildUpon();
            builder.appendQueryParameter(ContactsContract.STREQUENT_PHONE_ONLY, "true");
        } else if (contactName != null && !contactName.isEmpty()) {
            builder = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(contactName)).buildUpon();
            builder.appendQueryParameter(ContactsContract.PRIMARY_ACCOUNT_NAME, "true");
        } else {
            builder = ContactsContract.CommonDataKinds.Phone.CONTENT_URI.buildUpon();
        }

        builder.appendQueryParameter(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX, "true");
        builder.appendQueryParameter(ContactsContract.REMOVE_DUPLICATE_ENTRIES, "true");
        return builder.build();
    }

}
