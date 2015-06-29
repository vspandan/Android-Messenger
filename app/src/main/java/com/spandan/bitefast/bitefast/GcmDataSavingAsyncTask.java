package com.spandan.bitefast.bitefast;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.registration.Registration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GcmDataSavingAsyncTask {
    public Messaging msgService = null;
    public AsyncTask<Void, Void, String> sendTask;
    private static Registration regService = null;

    public void insertUser(final String phn, final boolean isAdmin) {

        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (msgService == null) {
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    msgService = builder.build();
                }
                String msg = phn + ":" + isAdmin;
                try {
                    msgService.insertUser(phn, isAdmin).execute();
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

    public void insertAppUserDetails(final String regId, final String phn, final String name, final String email, final String addr, final String street, final String landmark, final String city) {
        {

            sendTask = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    if (regService == null) {
                        Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                                new AndroidJsonFactory(), null);
                        builder.setApplicationName("BiteFast");
                        msgService = builder.build();
                    }
                    String msg="";
                    try {
                        msgService.insertAppUser(regId, phn, name, email, addr, street, landmark, city).execute();
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

    public void finduser(final String regId,final String phoneNum){

            sendTask = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    if (regService == null) {
                        Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                                new AndroidJsonFactory(), null);
                        builder.setApplicationName("BiteFast");
                        msgService = builder.build();
                    }
                    String msg="";
                    try {
                        msgService.findUser(regId, phoneNum).execute();
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

    public void registerDevice(final String regId,final long phoneNum,final Context context) {
        Logger.getLogger("REGISTRATION").log(Level.INFO, "Starting");
        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (regService == null) {
                    Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    regService = builder.build();
                }
                String msg = "";
                try {
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
            }
        };
        sendTask.execute(null, null, null);
    }

}
