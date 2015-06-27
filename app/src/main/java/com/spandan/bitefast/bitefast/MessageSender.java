package com.spandan.bitefast.bitefast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MessageSender {
    public AsyncTask<Void, Void, String> sendTask;
    public AtomicInteger ccsMsgId = new AtomicInteger();

    public void sendMessage(final Bundle data, final GoogleCloudMessaging gcm ) {

        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String id = Integer.toString(ccsMsgId.incrementAndGet());

                try {
                    gcm.send(Config.GOOGLE_PROJECT_ID + "@gcm.googleapis.com", id,
                            data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "Message ID: "+id+ " Sent data:" + data;
            }

            @Override
            protected void onPostExecute(String result) {
                Logger.getLogger("SENT").log(Level.INFO,result );
            }

        };
        sendTask.execute(null, null, null);
    }

}
