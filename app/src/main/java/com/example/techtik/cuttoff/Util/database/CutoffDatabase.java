package com.example.techtik.cuttoff.Util.database;

import android.content.Context;
import android.os.AsyncTask;

import com.example.techtik.cuttoff.Util.database.entity.CustomRecordings;
import com.example.techtik.cuttoff.Util.database.entity.DefaultRecordings;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CustomRecordings.class, DefaultRecordings.class},version = 1)
public abstract class CutoffDatabase extends RoomDatabase {

    public abstract CustomRecDAO getCustomRecDAO();

    public abstract DefaultRecDAO getDefaultRecDAO();

    private static CutoffDatabase instance;

    public static synchronized CutoffDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context,CutoffDatabase.class,"cutoff_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }

        return instance;
    }

    //to initialize the database with data
    private static RoomDatabase.Callback callback=new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new InitializeDataAsyncTask(instance).execute();
        }
    };

    //Asyntask used to initialize
    private static class InitializeDataAsyncTask extends AsyncTask<Void,Void,Void>{

        private DefaultRecDAO defaultRecDAO;
        private CustomRecDAO customRecDAO;

        public InitializeDataAsyncTask(CutoffDatabase cutoffDatabase){
            defaultRecDAO=cutoffDatabase.getDefaultRecDAO();
            customRecDAO=cutoffDatabase.getCustomRecDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            DefaultRecordings defaultRecordings=new DefaultRecordings();
            defaultRecordings.setmCustomMessage("Cannot reach you.Will call you later");

            DefaultRecordings defaultRecordings2=new DefaultRecordings();
            defaultRecordings2.setmCustomMessage("In a meeting .");

            DefaultRecordings defaultRecordings3=new DefaultRecordings();
            defaultRecordings3.setmCustomMessage("At a class .");

            DefaultRecordings defaultRecordings4=new DefaultRecordings();
            defaultRecordings4.setmCustomMessage("At work.");

            defaultRecDAO.addCustomRec(defaultRecordings);
            defaultRecDAO.addCustomRec(defaultRecordings2);
            defaultRecDAO.addCustomRec(defaultRecordings3);
            defaultRecDAO.addCustomRec(defaultRecordings4);

            CustomRecordings customRecordings=new CustomRecordings();
            customRecordings.setmCustomMessage("Hello testing 123");
            customRecDAO.addCustomRec(customRecordings);

            return null;
        }
    }
}
