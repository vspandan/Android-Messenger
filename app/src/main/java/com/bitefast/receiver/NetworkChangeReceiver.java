package com.bitefast.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.bitefast.services.GCMNotificationIntentService;
import com.bitefast.services.GcmDataSavingAsyncTask;
import com.bitefast.services.HeartBeatService;
import com.bitefast.util.RegistrationDetails;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by rubbernecker on 14/7/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    HeartBeatService.class.getName());
            intent.putExtra("Status","on");
            startWakefulService(context, (intent.setComponent(comp)));

        }

        if (!wifi.isAvailable() && !mobile.isAvailable()) {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    HeartBeatService.class.getName());
            intent.putExtra("Status","on");
            startWakefulService(context, (intent.setComponent(comp)));

        }
    }
}
