package com.example.techtik.cuttoff.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name // TODO I make this public for use in another class like DeleteAllData
    public static final String DATABASE_NAME = "PeaceOnDB";

    private static final String TABLE_CUSTOM_RECORDINGS = "custom_recordings";
    private static final String TABLE_DEFAULT_RECORDINGS = "default_recordings";
    private static final String TABLE_HISTORY = "history";
    private static final String TABLE_CONTACTS = "contacts";

    public static String TAG = "PeaceOn";
    SQLiteDatabase db;


    private Context context;
    String DB_TABLE;



    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createCustomRecordingTable = "CREATE TABLE IF NOT EXISTS "+TABLE_CUSTOM_RECORDINGS+" (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
                "custom_recordings TEXT NOT NULL)";
        String createDefaultRecordingTable = "CREATE TABLE IF NOT EXISTS "+TABLE_DEFAULT_RECORDINGS+" (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
                "default_recordings TEXT NOT NULL)";
        String createHistoryTable = "CREATE TABLE IF NOT EXISTS "+TABLE_HISTORY+" (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
                "date TEXT NOT NULL, history_name TEXT NOT NULL, history_number TEXT NOT NULL)";
        String createContactsTable = "CREATE TABLE IF NOT EXISTS "+TABLE_CONTACTS+" (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
                "contacts_name TEXT NOT NULL, contacts_number TEXT NOT NULL)";


        sqLiteDatabase.execSQL(createCustomRecordingTable);
        sqLiteDatabase.execSQL(createDefaultRecordingTable);
        sqLiteDatabase.execSQL(createHistoryTable);
        sqLiteDatabase.execSQL(createContactsTable);
    }

    public void addRecording(String custom_recordings){
        ContentValues contentValues = new ContentValues();

        contentValues.put("custom_recordings", custom_recordings);

        long status = insertTableData(TABLE_CUSTOM_RECORDINGS, contentValues);
        if (status != -1) {
            Log.i(TAG, "Row Succesfully Inserted");
        }
    }


    public long insertTableData(String tablename, ContentValues values)
            throws SQLException {
        DB_TABLE = tablename;
        db = this.getWritableDatabase();
        long returnValue = db.insert(DB_TABLE, null, values);
        db.close();
        return returnValue;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }
}