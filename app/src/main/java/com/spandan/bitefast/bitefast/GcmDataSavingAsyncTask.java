package com.spandan.bitefast.bitefast;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.User;
import com.spandan.bitefast.gcmbackend.registration.Registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GcmDataSavingAsyncTask {
    public Messaging msgService = null;
    public AsyncTask<Void, Void, String> sendTask;

    public void saveMessage(final String regId, final String from, final String to, final String message) {
        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (msgService == null) {
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    msgService = builder.build();
                }
                String msg="";
                try {
                    msgService.saveMessage(regId, from, to, message).execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
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
    public void insertUser(final String regId, final String phn, final String name, final String email, final String addr, final String street, final String landmark, final String city, final boolean isAdmin) {

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
                    msgService.insertUser(regId, phn, name, email, addr, street, landmark, city, isAdmin).execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    msg += "InsertUser Error: " + ex.getMessage();
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




    public void registerDevice(final String regId,final String phoneNum) {
        Logger.getLogger("REGISTRATION").log(Level.INFO, "Starting");
        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (msgService == null) {
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    msgService = builder.build();
                }
                String msg = "";
                try {
                    msgService.register(regId, phoneNum).execute();
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
