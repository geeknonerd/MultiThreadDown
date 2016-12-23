package com.geeker.lv.filedown;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by lv on 16-12-22.
 */
public class ThreadInfo {

    private int id;
    private String url;
    private long progress;
    private long start;
    private long end;
    private int sid;



    public ThreadInfo() {
    }

    public ThreadInfo(int sid, String url, long start, long end,long progress) {
        this.sid = sid;
        this.progress = progress;
        this.url = url;
        this.start = start;
        this.end = end;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public ContentValues toContentVales() {
        ContentValues cv = new ContentValues();
        cv.put(ThreadEntry.COLUMN_SID,sid);
        cv.put(ThreadEntry.COLUMN_URL,url);
        cv.put(ThreadEntry.COLUMN_START,start);
        cv.put(ThreadEntry.COLUMN_STOP,end);
        cv.put(ThreadEntry.COLUMN_PROGRESS,progress);
        return cv;
    }

    @Override
    public String toString() {
        return id+","+sid+","+url+","+start+","+end+","+progress;
    }

    public static class ThreadEntry implements BaseColumns{
        public static final String TABLE_NAME = "thread_info";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_START = "start";
        public static final String COLUMN_STOP = "stop";
        public static final String COLUMN_PROGRESS = "progress";
        public static final String COLUMN_SID = "sid";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_URL + " TEXT," +
                COLUMN_SID + " INT," +
                COLUMN_PROGRESS + " BIGINT," +
                COLUMN_START + " BIGINT," +
                COLUMN_STOP + " BIGINT)";

        public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String[] COLUMNS = {_ID,COLUMN_SID,COLUMN_URL,COLUMN_PROGRESS,COLUMN_START,COLUMN_STOP};

        public static ThreadInfo toThreadInfo(Cursor cs) {
            if (cs == null) return null;
            ThreadInfo ti = new ThreadInfo();
            ti.setId(cs.getInt(cs.getColumnIndexOrThrow(_ID)));
            ti.setSid(cs.getInt(cs.getColumnIndexOrThrow(COLUMN_SID)));
            ti.setUrl(cs.getString(cs.getColumnIndexOrThrow(COLUMN_URL)));
            ti.setStart(cs.getLong(cs.getColumnIndexOrThrow(COLUMN_START)));
            ti.setEnd(cs.getLong(cs.getColumnIndexOrThrow(COLUMN_STOP)));
            ti.setProgress(cs.getLong(cs.getColumnIndexOrThrow(COLUMN_PROGRESS)));
            return ti;
        }
    }
}
