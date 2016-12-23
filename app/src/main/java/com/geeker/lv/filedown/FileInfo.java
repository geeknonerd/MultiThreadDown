package com.geeker.lv.filedown;

import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by lv on 16-12-22.
 */
public class FileInfo implements Serializable{

    public static final String FILEINFO = "FileInfo";

    private int id;
    private String name;
    private String url;
    private long size;
    private long progress;

    public FileInfo() {
    }

    public FileInfo(String name, String url, long size, long progress) {
        this.name = name;
        this.url = url;
        this.size = size;
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "FileInfo:" + id +"," +
                name +"," +
                url +"," +
                size +"," +
                progress;
    }

    public class FileEntry implements BaseColumns{
        public static final String TABLE_NAME = "file_info";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_PROGRESS = "progress";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " BIGINT," +
                COLUMN_URL + " VARCHAR(40)," +
                COLUMN_PROGRESS + " BIGINT," +
                COLUMN_SIZE + " BIGINT)";

        public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
