package com.drivefactor.traffic.traffic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nicholas on 12/14/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private final String TAG = GeofenceController.class.getName();

    // Database Version
    private static int DATEBASE_VERSION = 1;


    // Database Name
    private static String DATABASE_NAME;


    // Table Name
    private String TABLE_NAME;

    // Primary Column Name
    private String PRIMARY_COLUMN_NAME;

    // Primary Column Type
    private String PRIMARY_COLUMN_TYPE;



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATEBASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + PRIMARY_COLUMN_NAME + " " + PRIMARY_COLUMN_TYPE + " PRIMARY KEY)");

    }

    public DatabaseHandler setTableName(String name) {
        TABLE_NAME = name;
        return this;
    }


    public DatabaseHandler setPrimaryColumnName(String name) {
        PRIMARY_COLUMN_NAME = name;
        return this;
    }

    public DatabaseHandler setPrimaryColumnType(String type) {
        PRIMARY_COLUMN_TYPE = type;
        return this;
    }

    public void addColumn(String name,String type) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor dbCursor = db.query(this.TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        Log.d(TAG,"column names " + columnNames);


        db.execSQL("ALTER TABLE " + this.TABLE_NAME + " ADD COLUMN" + name + " " + type);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
