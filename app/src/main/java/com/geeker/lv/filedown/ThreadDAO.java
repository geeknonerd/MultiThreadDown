package com.geeker.lv.filedown;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lv on 16-12-22.
 */
public class ThreadDAO {

    private static DBHelper dbHelper;

    public ThreadDAO(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public synchronized void insertThread(ThreadInfo ti) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert(ThreadInfo.ThreadEntry.TABLE_NAME, null, ti.toContentVales());
        db.close();
    }

    public synchronized void deleteThread(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ThreadInfo.ThreadEntry.TABLE_NAME,
                ThreadInfo.ThreadEntry.COLUMN_URL +" = ?",new String[]{url});
        db.close();
    }

    public synchronized void updateThread(ThreadInfo ti) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(ThreadInfo.ThreadEntry.TABLE_NAME, ti.toContentVales(),
                ThreadInfo.ThreadEntry.COLUMN_URL + " = ? and " +
                        ThreadInfo.ThreadEntry.COLUMN_SID + " = ? "
                        , new String[]{ti.getUrl(),""+ti.getSid()});
        db.close();
    }

    public synchronized List<ThreadInfo> getThreads(String url,int sid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor query = db.query(ThreadInfo.ThreadEntry.TABLE_NAME, ThreadInfo.ThreadEntry.COLUMNS,
                ThreadInfo.ThreadEntry.COLUMN_URL + " = ? and " +
                        ThreadInfo.ThreadEntry.COLUMN_SID + " = ? ",
                new String[]{url,""+sid}, null, null, null);
        List<ThreadInfo> list = new ArrayList<>();
        if (query==null) {
            Log.e("DAO", "getThread:null");
            return null;
        }
//        Log.e("DAO", "getThread");
        while (query.moveToNext()) {
            Log.e("DAO", "Thread:"+ThreadInfo.ThreadEntry.toThreadInfo(query).toString());
            list.add(ThreadInfo.ThreadEntry.toThreadInfo(query));
        }
        query.close();
        db.close();
        return list;
    }

    public synchronized boolean isExists(ThreadInfo ti) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor query = db.query(ThreadInfo.ThreadEntry.TABLE_NAME,
                new String[]{ThreadInfo.ThreadEntry.COLUMN_URL, ThreadInfo.ThreadEntry.COLUMN_SID},
                ThreadInfo.ThreadEntry.COLUMN_URL + " = ? and " +
                        ThreadInfo.ThreadEntry.COLUMN_SID + " = ? "
                , new String[]{ti.getUrl(),""+ti.getSid()}, null, null, null);
        boolean flag = query.moveToNext();
        query.close();
        db.close();
        return flag;
    }
}
