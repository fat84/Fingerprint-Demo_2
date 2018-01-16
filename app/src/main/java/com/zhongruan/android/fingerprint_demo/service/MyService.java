package com.zhongruan.android.fingerprint_demo.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zhongruan.android.fingerprint_demo.ui.MyApplication;

/**
 * Created by Administrator on 2017/8/7.
 */

public class MyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {

                int a = MyApplication.getYltIdCardEngine().initEngine();
                int b = MyApplication.getYltFingerEngine().initEngine();
                //发送广播
                Intent intent = new Intent();
                intent.setAction("MyService");
                intent.putExtra("a", a);
                intent.putExtra("b", b);
                sendBroadcast(intent);
            }
        }).start();
    }
}