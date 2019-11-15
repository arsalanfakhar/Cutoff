package com.example.techtik.cuttoff.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_defaultRecordings")
public class DefaultRecordings {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "defaultMessage")
    public String mCustomMessage;

    @Ignore()
    public DefaultRecordings(){

    }

    public DefaultRecordings(long id, String mCustomMessage) {
        this.id = id;
        this.mCustomMessage = mCustomMessage;
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

}
