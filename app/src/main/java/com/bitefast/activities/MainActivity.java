package com.bitefast.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDex;

import com.bitefast.R;
import com.bitefast.services.GcmDataSavingAsyncTask;
import com.bitefast.services.HeartBeatService;
import com.bitefast.util.CheckInternetConnectivity;
import com.bitefast.util.Config;
import com.bitefast.util.RegistrationDetails;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends Activity {
    private static final int SPLASH_SHOW_TIME = 1000;
    private CheckInternetConnectivity cd = null;
    private GoogleCloudMessaging gcm;
    private AsyncTask<Void, Void, String> sendTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(getBaseContext(), HeartBeatService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cd = new CheckInternetConnectivity(getApplicationContext());
        if (cd.isConnectingToInternet()) {
            new AppRegister().execute();
        } else {
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

    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    private class AppRegister extends AsyncTask<Void, Void, Void> {
        String msg = "";
        String regId = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            new BackgroundSplashTask().execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                }

                regId = gcm.register(Config.GOOGLE_SENDER_ID);
                msg = "Device registered, registration ID=" + regId;
                new RegistrationDetails().storeRegistrationId(getApplicationContext(), regId);
            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (regId.isEmpty()){
                    Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setMessage("Check Your Internet Connectivity");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    alertDialog.show();
                }
                else
                {
                    Intent i = null;
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "1");
                    if (!new RegistrationDetails().getPhoneNum(getApplicationContext()).isEmpty()) {
                        final List<User> user = new ArrayList<User>();
                        try {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "2");
                            final String phoneNum = new RegistrationDetails().getPhoneNum(getApplicationContext());
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
                                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, new RegistrationDetails().getPhoneNum(getApplicationContext()));
                                        user.add(msgService.profileInfo(phoneNum).execute());
                                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, new RegistrationDetails().getPhoneNum(getApplicationContext()));
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
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, user.toString());
                        if(user.size()!=0) {
                            User usr = user.get(0);
                            new RegistrationDetails().setAdmin(getApplicationContext(),usr.getAdmin());
                            if (usr.getEmailId() == null || usr.getUserNum() == null) {
                                i = new Intent(MainActivity.this, EmailInfoForm.class);

                            } else if (usr.getAdmin()) {
                                i = new Intent(MainActivity.this, UserListActivity.class);
                            } else {
                                new RegistrationDetails().setUserName(getApplication(), usr.getUserName());
                                new RegistrationDetails().setEmailId(getApplication(), usr.getEmailId());
                                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                new GcmDataSavingAsyncTask().updateUserRegid(androidId, new RegistrationDetails().getRegistrationId(getApplicationContext()), new RegistrationDetails().getPhoneNum(getApplicationContext()));
                                i = new Intent(MainActivity.this, ChatActivity.class);
                                i.putExtra("SENDTO", "BITEFAST_ADMIN");
                            }
                        }
                    }
                    else {
                        i = new Intent(MainActivity.this, MobileInfoLogin.class);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "phone registering");
                    }
                    startActivity(i);
                }
            }
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

    }

}
