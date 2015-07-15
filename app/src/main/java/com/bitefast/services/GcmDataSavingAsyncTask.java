package com.bitefast.services;

import android.content.Context;
import android.os.AsyncTask;

import com.bitefast.util.RegistrationDetails;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.UserDetails;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GcmDataSavingAsyncTask {
    public Messaging msgService = null;
    public AsyncTask<Void, Void, String> sendTask;

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
                        /*Logger.getLogger("Messaging:FetchDetails:DEVICEID.:").log(Level.INFO, androidId);*/
                        UserDetails userdetails1 = msgService.fetchAddress(androidId).execute();
                        userDetails[0] = userdetails1;
                        /*Logger.getLogger("Messaging:FetchDetails:DATAFETCHED:").log(Level.INFO, userDetails[0].toString());*/
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


    public void updateUserRegid(final String androidId, final String regId, final String phoneNum) {


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
                    msgService.updateUserRegid(androidId, regId, phoneNum).execute();
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

    public void reqUndeliveredMessage(final String phn) {

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
                /*Logger.getLogger("Messaging:SendMessage:DATA:").log(Level.INFO, jsondata);*/
                try {
                    msgService.reSendMessages(phn).execute();
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

    public void sendMessage(final String jsondata) {

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
                /*Logger.getLogger("Messaging:SendMessage:DATA:").log(Level.INFO, jsondata);*/
                try {
                    msgService.sendMessage(jsondata).execute();
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
