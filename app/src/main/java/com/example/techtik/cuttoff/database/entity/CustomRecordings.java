package com.example.techtik.cuttoff.database.entity;

import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.Util.DataConverter;

import java.util.ArrayList;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Entity(tableName = "tbl_customRecordings")
public class CustomRecordings {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "customMessage")
    public String mCustomMessage;

//    @ColumnInfo(name = "contact")
//    public Contact mContact;

    @ColumnInfo(name = "contact_name")
    private String name = "";

    @ColumnInfo(name = "contact_phone")
    private ArrayList<String> phones = new ArrayList<>();

    @ColumnInfo(name = "contact_img")
    private String photoUri;


    @Ignore()
    public CustomRecordings(){

    }

    @Ignore()
    public CustomRecordings(long id, String mCustomMessage) {
        this.id = id;
        this.mCustomMessage = mCustomMessage;
    }


    public CustomRecordings(long id, String mCustomMessage, String name, ArrayList<String> phones, String photoUri) {
        this.id = id;
        this.mCustomMessage = mCustomMessage;
        this.name = name;
        this.phones = phones;
        this.photoUri = photoUri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getmCustomMessage() {
        return mCustomMessage;
    }

    public void setmCustomMessage(String mCustomMessage) {

        this.mCustomMessage = mCustomMessage;
    }

//    public Contact getmContact() {
//        return mContact;
//    }
//
//    public void setmContact(Contact mContact) {
//        this.mContact = mContact;
//    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

}
