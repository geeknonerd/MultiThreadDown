package com.geeker.lv.filedown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();

    private TextView tvView;
    private ProgressBar pbView;
    private FileInfo fi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvView = (TextView) findViewById(R.id.tv_show_name);
        pbView = (ProgressBar) findViewById(R.id.pb_show_progress);
        pbView.setMax(100);
        fi = new FileInfo("SHome.apk", "http://192.168.0.11/SHome.apk", 0, 0);
        tvView.setText(fi.getName());
        IntentFilter itf = new IntentFilter();
        itf.addAction(DownService.ACTION_UPDATE);
        registerReceiver(mReceiver, itf);
    }

    public void startDown(View v) {
        Intent i = new Intent(this, DownService.class);
        i.setAction(DownService.ACTION_START);
        i.putExtra(FileInfo.FILEINFO,fi);
        startService(i);
    }

    public void stopDown(View v) {
        Intent i = new Intent(this, DownService.class);
        i.setAction(DownService.ACTION_STOP);
        i.putExtra(FileInfo.FILEINFO,fi);
        startService(i);
    }

    public void instance(View v) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/down/SHome.apk");
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownService.ACTION_UPDATE.equals(intent.getAction())) {
                int pg = intent.getIntExtra("progress", 0);
                Log.e(TAG,"Finish:" + pg);
                pbView.setProgress(pg);
                if (pg == 100) {
                    Toast.makeText(MainActivity.this, "Down Finish!", Toast.LENGTH_SHORT).show();

                }
            }
        }
    };
}
