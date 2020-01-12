package com.example.techtik.cuttoff.Models;

import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

import androidx.room.Ignore;

public class Contact {
    private String name = "";
    private String email = "";
    private ArrayList<String> phones = new ArrayList<>();
    private String imageLetter = "";
    private String lookUp = "";
    private String key = "";
    private String photoUri;

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }

    public String getImageLetter() {
        return imageLetter;
    }

    public void setImageLetter(String imageLetter) {
        this.imageLetter = imageLetter;
    }

    public String getLookUp() {
        return lookUp;
    }

    public void setLookUp(String lookUp) {
        this.lookUp = lookUp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Contact(){

    }
    public Contact(String name,String phoneNum,String photoUri){
        this.name=name;
        this.phones.add(phoneNum);
        this.photoUri=photoUri;
    }

    @Ignore
    public Contact(Cursor cursor) {
        this.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
        this.photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        this.phones = new ArrayList<>();
        this.phones.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
    }


}
