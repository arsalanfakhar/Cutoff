package com.example.techtik.cuttoff.database;

import com.example.techtik.cuttoff.Models.Contact;
import com.example.techtik.cuttoff.database.entity.CustomRecordings;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao()
public interface CustomRecDAO {

    @Insert
    public long addCustomRec(CustomRecordings customRecordings);

    @Delete
    public void deleteRec(CustomRecordings customRecordings);

    @Update
    public void updateRec(CustomRecordings customRecordings);

    @Query("Select * from tbl_customRecordings")
    public LiveData<List<CustomRecordings>> getAllRecordings();

    @Query("Select customMessage from tbl_customRecordings where contact_name=:name")
    public String getRecordingMessage(String name);

}
