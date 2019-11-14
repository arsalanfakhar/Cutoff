package com.example.techtik.cuttoff.Util.database;

import com.example.techtik.cuttoff.Util.database.entity.CustomRecordings;

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

}
