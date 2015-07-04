package com.spandan.bitefast.bitefast;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.User;
import com.spandan.bitefast.gcmbackend.registration.Registration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends Activity {
    private static final int SPLASH_SHOW_TIME = 3000;
    private CheckInternetConnectivity cd = null;
    private GoogleCloudMessaging gcm;
    private AsyncTask<Void, Void, String> sendTask;
    private static final String SENDER_ID = "281575560274";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cd=new CheckInternetConnectivity(getApplicationContext());
        if(cd.isConnectingToInternet()) {
            String regId=new RegistrationDetails().getRegistrationId(getApplicationContext());
            if (regId.isEmpty()||regId==null)
                register();
            new BackgroundSplashTask().execute();
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("No Network Connectivity");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            alertDialog.show();
        }

    }

    public void register() {
        Logger.getLogger("REGISTRATION").log(Level.INFO, "Starting");
        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    String regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;
                    new RegistrationDetails().storeRegistrationId(getApplicationContext(),regId);
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

    private class BackgroundSplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent i=null;
            if(new RegistrationDetails().isLoggedIn(getApplicationContext())){
                Logger.getLogger("MainActivity").log(Level.INFO, "sign up");
                final boolean values[] = new boolean[1];
                try {

                    final String phoneNum =new RegistrationDetails().getPhoneNum(getApplicationContext());
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            Messaging msgService = null;
                            if (msgService == null) {
                                Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                                        new AndroidJsonFactory(), null);
                                builder.setApplicationName("BiteFast");
                                msgService = builder.build();
                            }
                            User usr = new User();
                            try {
                                usr = msgService.isAdmin(phoneNum).execute();
                                values[0] = usr.getAdmin();
                            } catch (Exception ex) {
                                System.exit(1);
                                ex.printStackTrace();
                            }
                            Logger.getLogger("LaunchActivity").log(Level.INFO, phoneNum + ":" + usr.toString());
                        }
                    });
                    t.start();
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Logger.getLogger("LaunchActivity").log(Level.INFO, "Retrieved User: " + values[0]);
                if(values[0]) {
                    new RegistrationDetails().setAdmin(getApplicationContext());
                    i = new Intent(MainActivity.this, UserListActivity.class);
                    i.putExtra("UserType", true);
                }
                else {
                    i = new Intent(MainActivity.this, ChatActivity.class);
                    i.putExtra("SENDTO","BITEFAST_ADMIN");
                    i.putExtra("UserType",false);
                }
            }
            else {
                i = new Intent(MainActivity.this, BitefastSignUp.class);
                Logger.getLogger("MainActivity").log(Level.INFO, "registering");
            }
            startActivity(i);
            finish();
        }

    }

}
