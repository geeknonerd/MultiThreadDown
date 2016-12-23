package com.geeker.lv.filedown;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lv on 16-12-22.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "filedown.db";
    private static final int VERSION = 1;
    private static DBHelper db = null;

    private DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (db == null) {
            db = new DBHelper(context);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ThreadInfo.ThreadEntry.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ThreadInfo.ThreadEntry.SQL_DELETE);
        db.execSQL(ThreadInfo.ThreadEntry.SQL_CREATE);
    }
}
