package com.spandan.bitefast.bitefast;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.User;
import com.spandan.bitefast.gcmbackend.messaging.model.UserDetails;
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
    public Messaging regService = null;
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
                String msg = "";
                Logger.getLogger("Messaging:SaveMessage:DATA:").log(Level.INFO, from + ":" + to + ":" + message);
                try {
                    msgService.saveMessage(regId, from, to, message).execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    msg += "SaveMessage Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.getLogger("Messaging:SaveMessage:POST:").log(Level.INFO, msg);
            }
        };
        sendTask.execute(null, null, null);
    }

    public UserDetails fetchDetails(final String androidId) {
        final UserDetails[] userDetails = new UserDetails[1];
        try {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    Messaging msgService = null;
                    if (msgService == null) {
                        Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                                new AndroidJsonFactory(), null);
                        builder.setApplicationName("BiteFast");
                        msgService = builder.build();
                    }
                    try {
                        UserDetails userdetails1 = msgService.fetchAddress(androidId).execute();
                        userDetails[0] = userdetails1;
                        Logger.getLogger("Messaging:FetchDetails:DATAFETCHED:").log(Level.INFO, userDetails[0].toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userDetails[0];
    }


    public void saveOrder(final String order, final String usr, final String message) {
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
                Logger.getLogger("Messaging:SaveOrder:DATA:").log(Level.INFO, "");
                try {
                    msgService.saveOrder(order, usr, message).execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    msg += "SaveOrder Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.getLogger("Messaging:SaveOrder:POST:").log(Level.INFO, msg);
            }
        };
        sendTask.execute(null, null, null);
    }


    public void insertUser(final String androidId, final String regId, final String phn, final String name, final String email, final String addr, final String street, final String landmark, final String city, final boolean isAdmin) {


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
                Logger.getLogger("Messaging:InsertUser:DATA").log(Level.INFO, ":");
                try {
                    msgService.insertUser(androidId, regId, phn, name, email, addr, street, landmark, city, isAdmin).execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    msg += "InsertUser Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.getLogger("Messaging:InsertUser:POST:").log(Level.INFO, msg);
            }
        };
        sendTask.execute(null, null, null);
    }


    public void updateUserRegid(final String androidId, final String regId) {


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
                Logger.getLogger("Messaging:UpdateUserRegid:DATA").log(Level.INFO, ":");
                try {
                    msgService.updateUserRegid(androidId, regId).execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    msg += "UpdateUserRegid Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.getLogger("Messaging:UpdateUserRegid:POST:").log(Level.INFO, msg);
            }
        };
        sendTask.execute(null, null, null);
    }

    public void sendMessage(final String jsondata, final String regId) {

        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (regService == null) {
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    regService = builder.build();
                }
                String msg = "";
                Logger.getLogger("Messaging:SendMessage:DATA:").log(Level.INFO, jsondata);
                try {
                    regService.sendMessage(jsondata, regId).execute();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    msg = "SendMessage Error: " + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logger.getLogger("Messaging:SendMessage:POST").log(Level.INFO, msg);
            }
        };
        sendTask.execute(null, null, null);
    }
}
