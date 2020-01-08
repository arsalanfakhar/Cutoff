package com.example.techtik.cuttoff.database.entity;

import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.Util.DataConverter;

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

    @ColumnInfo(name = "contact")
    public Contact mContact;


    @Ignore()
    public CustomRecordings(){

    }

    @Ignore()
    public CustomRecordings(long id, String mCustomMessage) {
        this.id = id;
        this.mCustomMessage = mCustomMessage;
    }


    public CustomRecordings(long id, String mCustomMessage,Contact mContact) {
        this.id = id;
        this.mCustomMessage = mCustomMessage;
        this.mContact=mContact;
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

    public Contact getmContact() {
        return mContact;
    }

    public void setmContact(Contact mContact) {
        this.mContact = mContact;
    }
}
