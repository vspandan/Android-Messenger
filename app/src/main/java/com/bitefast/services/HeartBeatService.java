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

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.getLogger("HeartBeatService:").log(Level.INFO, "HEARTBEAT_MSG");
        getApplicationContext().sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        getApplicationContext().sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        return super.onStartCommand(intent, flags, startId);
    }
}