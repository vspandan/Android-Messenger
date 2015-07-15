package com.bitefast.services;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by rubbernecker on 15/7/15.
 */
public class GcmKeepAlive extends CountDownTimer {

    protected CountDownTimer timer;
    protected Context mContext;
    protected Intent gTalkHeartBeatIntent;
    protected Intent mcsHeartBeatIntent;


    public GcmKeepAlive(Context context) {
        super(30* 1000,30*1000);
        mContext = context;
        gTalkHeartBeatIntent = new Intent("com.google.android.intent.action.GTALK_HEARTBEAT");
        mcsHeartBeatIntent = new Intent("com.google.android.intent.action.MCS_HEARTBEAT");
        Log.d("GcmKeepAlive", "starting heartbeat countdown timer");
        this.start();
    }


    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {
        Log.d("GcmKeepAlive", "sending heart beat to keep gcm alive");
        mContext.sendBroadcast(gTalkHeartBeatIntent);
        mContext.sendBroadcast(mcsHeartBeatIntent);
        this.start();
    }
}