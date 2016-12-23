package com.geeker.lv.filedown;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lv on 16-12-22.
 */
public class DownService extends Service {

    private final String TAG = getClass().getName();

    public static final String DOWN_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/down/";
    public static final String ACTION_START = "ACTION_DOWN_START";
    public static final String ACTION_STOP = "ACTION_DOWN_STOP";
    public static final String ACTION_UPDATE = "ACTION_DOWN_UPDATE";
    public static final int MSG_INIT = 0;
    private DownTask mTask = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fi = (FileInfo) intent.getSerializableExtra(FileInfo.FILEINFO);
//            Log.e(TAG, fi.toString());
            new InitThread(fi).start();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfo fi = (FileInfo) intent.getSerializableExtra(FileInfo.FILEINFO);
//            Log.e(TAG, fi.toString());
            mTask.pause();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_INIT:
                    Log.e(TAG, "MSG_INIT");
                    FileInfo _fi = (FileInfo) msg.obj;
                    mTask = new DownTask(DownService.this, _fi,3);
                    mTask.down();
                    break;

                default:
                    break;
            }
        }
    };

    private class InitThread extends Thread{

        private FileInfo mFi;

        public InitThread(FileInfo mFi) {
            this.mFi = mFi;
        }

        @Override
        public void run() {
            super.run();
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(mFi.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = conn.getContentLength();
                }
                if (length<=0) return;
                File path = new File(DOWN_PATH);
                if (!path.exists()) {
                    path.mkdir();
                }
                File file = new File(path, mFi.getName());
                raf = new RandomAccessFile(file, "rwd");
                raf.setLength(length);
                mFi.setSize(length);
                Log.e(TAG, "Length:" + length);
                mHandler.obtainMessage(MSG_INIT,mFi).sendToTarget();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (raf!=null) raf.close();
                    if (conn!=null) conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
