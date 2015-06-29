package com.spandan.bitefast.bitefast;

import android.os.AsyncTask;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GcmMessagingAsyncTask  {
    public Messaging regService = null;
    public AsyncTask<Void, Void, String> sendTask;

    public void sendMessage(final String jsondata,final String regId) {

        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (regService == null) {
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory() ,null);
                    builder.setApplicationName("BiteFast");
                    regService = builder.build();
                }
                String msg = jsondata;
                try {
                    regService.sendMessage(jsondata,regId).execute();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    msg += "; Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.getLogger("Messaging").log(Level.INFO, msg);
            }
        };
        sendTask.execute(null, null, null);
    }

}
