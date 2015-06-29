package com.spandan.bitefast.bitefast;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
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
        registerReceiver(broadcastReceiver, new IntentFilter("com.spandan.bitefast.bitefast"));
        if(cd.isConnectingToInternet()) {
            if (new RegistrationDetails(getApplicationContext()).getRegistrationId().isEmpty())
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
                    new RegistrationDetails(getApplicationContext()).storeRegistrationId(regId);
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent i=null;

            String action=intent.getStringExtra("ACTION");
            Logger.getLogger("MainActivity").log(Level.INFO, action);
            if("FINDUSER".equals(action)) {
                String phoneNum=intent.getStringExtra("Phoneno");
                String admin=intent.getStringExtra("Admin");
                    /*String ph=new RegistrationDetails(getApplicationContext()).getPhoneNum();*/
                String ph="9440077887";
                if (ph.equals(phoneNum)&& "1".equals(admin)) {
                    i = new Intent(MainActivity.this, UserListActivity.class);
                    i.putExtra("UserType", true);
                } else {
                    i = new Intent(MainActivity.this, ChatActivity.class);
                    i.putExtra("TOUSER", "888551544");
                    i.putExtra("UserType", false);
                }
            }
        }
    };

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

            /*if(new RegistrationDetails(getApplicationContext()).isLoggedIn()){*/
            if(true){
                new GcmDataSavingAsyncTask().finduser(new RegistrationDetails(getApplicationContext()).getRegistrationId(),"9440077887");
            }
            else {
                Intent i=null;
                i = new Intent(MainActivity.this, BitefastSignUp.class);
                Logger.getLogger("MainActivity").log(Level.INFO, "registering");
            }

        }

    }



}
