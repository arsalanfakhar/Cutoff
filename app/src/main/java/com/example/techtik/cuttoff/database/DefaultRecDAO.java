package com.example.techtik.cuttoff.database;


import com.example.techtik.cuttoff.database.entity.DefaultRecordings;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;

import androidx.room.Insert;
import androidx.room.Query;


@Dao()
public interface DefaultRecDAO {
    @Insert
    public long addCustomRec(DefaultRecordings defaultRecordings);

    @Query("Select * from tbl_defaultRecordings")
    public LiveData<List<DefaultRecordings>> getAllRecordings();

}
