package com.example.administrator.droidplugintest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_SDCARD = 1;

    private static final String PACKAGE_NAME = "com.example.administrator.nettytest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
    }

    public void installPkg(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File apkFile = new File(Environment.getExternalStorageDirectory(), "app-debug.apk");
                try {
                    int result =  PluginManager.getInstance().installPackage(apkFile.getAbsolutePath(), 0);
                    Log.i("wangweijun", "resule:" +result);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void open(View view) {
        try {
            ApplicationInfo applicationInfo = PluginManager.getInstance().getApplicationInfo(PACKAGE_NAME,0);
            if (applicationInfo != null) {
                Intent intent = getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "没有安装呢",Toast.LENGTH_LONG).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void uninstall(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PluginManager.getInstance().deletePackage(PACKAGE_NAME, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                init();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                        REQUEST_PERMISSION_SDCARD);
            }

        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_SDCARD: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {

                }
                return;
            }

        }
    }

    private void init() {
        Toast.makeText(getApplicationContext(), "有權限",Toast.LENGTH_LONG).show();
    }

}
