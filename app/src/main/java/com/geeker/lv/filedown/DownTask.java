package com.geeker.lv.filedown;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lv on 16-12-22.
 */
public class DownTask {

    private final String TAG = getClass().getName();

    private Context mContext;
    private FileInfo mFi;
    private boolean isPause;
    private ThreadDAO mDao;
    private int threadCount;
    private List<DownRunTask> mTasks;
    private ExecutorService executorService;

    public DownTask(Context context, FileInfo mFi, int count) {
        this.mContext = context;
        this.mFi = mFi;
        isPause = false;
        mDao = new ThreadDAO(context);
        threadCount = count;
        executorService = Executors.newCachedThreadPool();
    }

    public void down() {
        mTasks = new ArrayList<>();
        long pLen = mFi.getSize()/threadCount;
        for(int i=0;i<threadCount;i++) {
            List<ThreadInfo> list = mDao.getThreads(mFi.getUrl(),i);
            ThreadInfo _ti = null;
            Log.e(TAG,"DB Thread Num:" + list.size());
            if (list==null || list.size() == 0) {
                Log.e(TAG,"New Thread");
                _ti = new ThreadInfo(i, mFi.getUrl(), i*pLen,
                        i==threadCount-1?mFi.getSize():(i+1)*pLen-1,0);
            }else {
                Log.e(TAG, "Get Thread");
                _ti = list.get(0);
            }
            DownRunTask dt = new DownRunTask(_ti);
            executorService.submit(dt);
            mTasks.add(dt);
        }
    }

    public long getProgress() {
        long _progress = 0;
        for (DownRunTask dt : mTasks) {
            _progress += dt.mTi.getProgress();
        }
        return _progress;
    }

    public void pause() {
        isPause = true;
    }

    private class DownRunTask implements Runnable {

        ThreadInfo mTi = null;

        public DownRunTask(ThreadInfo mTi) {
            this.mTi = mTi;
        }

        @Override
        public void run() {
            if (!mDao.isExists(mTi)) {
                Log.e(TAG,"Insert Thread");
                mDao.insertThread(mTi);
            }
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream is = null;
            try {
                URL url = new URL(mTi.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                long start = mTi.getStart() + mTi.getProgress();
                conn.setRequestProperty("Range","bytes=" + start+"-" + mTi.getEnd());
                File file = new File(DownService.DOWN_PATH, mFi.getName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                Intent i = new Intent(DownService.ACTION_UPDATE);
                long finish = mTi.getProgress();
                Log.e(TAG,"ResponseCode:" + conn.getResponseCode());
                if (conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    is = conn.getInputStream();
                    byte[] buf = new byte[4 * 1024];
                    int len = -1;
                    BufferedInputStream bis = new BufferedInputStream(is);
                    long time = System.currentTimeMillis();
                    while ((len = bis.read(buf)) != -1) {
                        raf.write(buf,0,len);
                        finish += len;
                        mTi.setProgress(finish);
//                        Log.e(TAG,"Finish:" + finish);
                        if (System.currentTimeMillis() - time > 1000) {
                            time = System.currentTimeMillis();
//                            Log.e(TAG,"progress:" + finish * 100 / mFi.getSize());
                            i.putExtra("progress", (int) (getProgress() * 100 / mFi.getSize()));
                            mContext.sendBroadcast(i);
                        }
                        if (isPause) {
                            Log.e(TAG,"Update");
                            mDao.updateThread(mTi);
                            return;
                        }
                    }
                    Log.e(TAG,"last_progress:" + finish * 100 / mFi.getSize());
                    i.putExtra("progress",(int) (getProgress() * 100 / mFi.getSize()));
                    mContext.sendBroadcast(i);
                    if (getProgress() == mFi.getSize()) mDao.deleteThread(mTi.getUrl());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (is!=null) is.close();
                    if (raf!=null) raf.close();
                    if (conn!=null) conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
