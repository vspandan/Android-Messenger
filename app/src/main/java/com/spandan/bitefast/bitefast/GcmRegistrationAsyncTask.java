package com.spandan.bitefast.bitefast;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.registration.Registration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Created by spandanv on 6/26/2015.
*/

public class GcmRegistrationAsyncTask {
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
    private static final String SENDER_ID = "281575560274";

    public GcmRegistrationAsyncTask(Context context) {
        Logger.getLogger("REGISTRATION").log(Level.INFO, "entered");
        this.context = context;
    }

    private AsyncTask<Void, Void, String> sendTask;

    public void register(final String phoneNum) {
        Logger.getLogger("REGISTRATION").log(Level.INFO, "Starting");
        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (regService == null) {
                    Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    // end of optional local run code
                    builder.setApplicationName("BiteFast");
                    regService = builder.build();
                }

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    regService.register(regId, phoneNum).execute();
                    new RegistrationDetails(context).storeRegistrationId(regId);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    msg = "Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
            }
        };
        sendTask.execute(null, null, null);
    }
}
