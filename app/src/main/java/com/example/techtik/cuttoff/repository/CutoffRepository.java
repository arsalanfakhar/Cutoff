package com.example.techtik.cuttoff.repository;

import android.app.Application;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.Util.database.CustomRecDAO;
import com.example.techtik.cuttoff.Util.database.CutoffDatabase;
import com.example.techtik.cuttoff.Util.database.DefaultRecDAO;
import com.example.techtik.cuttoff.Util.database.entity.CustomRecordings;
import com.example.techtik.cuttoff.Util.database.entity.DefaultRecordings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

public class CutoffRepository {

    private Application application;
    private DefaultRecDAO defaultRecDAO;
    private CustomRecDAO customRecDAO;

    //Initialize here
    public CutoffRepository(Application application) {
        this.application = application;

        CutoffDatabase cutoffDatabase=CutoffDatabase.getInstance(application);
        defaultRecDAO=cutoffDatabase.getDefaultRecDAO();
        customRecDAO=cutoffDatabase.getCustomRecDAO();
    }

    public LiveData<List<DefaultRecordings>> getAllDefaultRecordings(){
        return defaultRecDAO.getAllRecordings();
    }

    public LiveData<List<CustomRecordings>> getAllCustomRecordings(){
        return customRecDAO.getAllRecordings();
    }

    public void addRecording(CustomRecordings customRecordings){
        //We can use executors instead of asyn task if we do not want to use its complex methods
        Executor executor= Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            customRecDAO.addCustomRec(customRecordings);
        });
    }

    public void updateRecording(CustomRecordings customRecordings){
        Executor executor= Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            customRecDAO.updateRec(customRecordings);
        });
    }

    public void deleteRecording(CustomRecordings customRecordings){
        Executor executor= Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            customRecDAO.deleteRec(customRecordings);
        });

    }


    public List<Contact> fetchContacts(){
        List<Contact> contactList=new ArrayList<>();
        try {
            String[] projection=new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.PhoneLookup.PHOTO_URI
            };

            Cursor cursor = application.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ? AND in_default_directory = ? ", new String[]{"1", "1"}, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " COLLATE NOCASE");

            if(cursor!=null){
                while (cursor.moveToNext()){
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    //String label = cursor.getString(cursor.getColumnIndex("phonebook_label"));
                    String lookup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

                    String photoUri=cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));

                    ArrayList<String> phoneNumbers=getNumbers(contactId);
                    if(phoneNumbers.size()>0){
                        Contact contact=new Contact();
                        contact.setName(name);
                        contact.setPhones(phoneNumbers);
                        contact.setEmail(getEmail(contactId));
//                        contact.setImageLetter(label);
                        contact.setLookUp(lookup);
                        contact.setPhotoUri(photoUri);
                        contactList.add(contact);
                    }



                }
            }
            cursor.close();

        }
        catch (Exception e){
            e.printStackTrace();

            Log.e("error",e.getMessage());
        }
        return contactList;
    }

    private ArrayList<String> getNumbers(String contactId){
        ArrayList<String> phones = new ArrayList<>();
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.Data.RAW_CONTACT_ID};
        final Cursor phone = application.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

        if (phone != null) {
            while (phone.moveToNext()) {
                String raw_id = phone.getString(phone.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));

                int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                String phoneNo = phone.getString(contactNumberColumnIndex);

                if (phones.size() == 0) {
                    // this is the first phone number
                    phones.add(phoneNo);
                } else {
                    // another phone numbers of contact
                    // so compare if that numaber is already added or not
                    boolean alreadyAdded = false;
                    for (String number : phones) {
                        // the below comparision returns true if two numbers are identical enough for caller ID purposes.
                        if (PhoneNumberUtils.compare(phoneNo, number)) {
                            // number is matched with any of already added numbers
                            alreadyAdded = true;
                            break;
                        }
                    }
                    if (!alreadyAdded)
                        phones.add(phoneNo);
                }
            }
            phone.close();
        }
        return phones;
    }

    /**
     * get the email of contact using contact id
     */
    private String getEmail(String contactId) {
        String emailStr = "";
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Email.ADDRESS};
        final Cursor email = application.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

        if (email != null) {
            if (email.getCount() > 0) {
                email.moveToFirst();
                final int contactEmailColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                emailStr = email.getString(contactEmailColumnIndex);
            }
            email.close();
        }
        return emailStr;

    }


}
