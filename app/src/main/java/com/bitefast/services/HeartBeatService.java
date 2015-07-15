package com.bitefast.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rubbernecker on 14/7/15.
 */
public class HeartBeatService extends Service {

    private GcmKeepAlive gcmKeepAlive;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        gcmKeepAlive.start();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gcmKeepAlive = new GcmKeepAlive(this);
    }
}