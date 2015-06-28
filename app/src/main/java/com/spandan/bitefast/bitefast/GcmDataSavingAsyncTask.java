package com.spandan.bitefast.bitefast;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GcmDataSavingAsyncTask {
    public Messaging regService = null;
    public AsyncTask<Void, Void, String> sendTask;

    public void insertUser(final int phn,final boolean isAdmin) {

        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (regService == null) {
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory() ,null);
                    builder.setApplicationName("BiteFast");
                    regService = builder.build();
                }
                String msg = phn+":"+isAdmin;
                try {
                    regService.messagingEndpoint().insertUser(phn,isAdmin).execute();
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
