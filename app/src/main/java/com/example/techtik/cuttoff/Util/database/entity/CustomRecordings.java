package com.example.techtik.cuttoff.Util.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_customRecordings")
public class CustomRecordings {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "customMessage")
    public String mCustomMessage;


    @Ignore()
    public CustomRecordings(){

    }

    public CustomRecordings(long id, String mCustomMessage) {
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
