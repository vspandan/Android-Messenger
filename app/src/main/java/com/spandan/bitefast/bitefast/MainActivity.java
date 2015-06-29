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
            register();
            new BackgroundSplashTask().execute();
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("No Network Connectivity");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

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
            if(new RegistrationDetails(getApplicationContext()).isLoggedIn()){
                Logger.getLogger("MainActivity").log(Level.INFO, "sign up");
                //TODO check admin status and also validate the user details with datastore values.
                if(true) {
                    i = new Intent(MainActivity.this, UserListActivity.class);
                    i.putExtra("UserType", true);
                }
                else {
                    i = new Intent(MainActivity.this, ChatActivity.class);
                    i.putExtra("TOUSER","888551544");
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };
    private boolean isAdmin() {
        return false;
    }
}

/*
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;

import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    private GoogleCloudMessaging gcm = null;
    private String userName=null;
    private String password=null;
    private boolean isAdmin=false;
    private MessageSender messageSender = null;
    private Context context = null;
    private boolean verified=false;
    private boolean userExists=false;
    private Button signupbutton=null;
    private Button signinbutton=null;
    private EditText pwd=null;
    private EditText phnNo=null;
    private CheckInternetConnectivity cd = null;
    private boolean isInternetPresent = false;
    private String regId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.getLogger("MainActivity").log(Level.INFO, "creating main activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        context = getApplicationContext();
        cd=new CheckInternetConnectivity(context);
        signupbutton = (Button) findViewById(R.id.signup);
        signinbutton = (Button) findViewById(R.id.signin);
        signupbutton.setOnClickListener(this);
        signinbutton.setOnClickListener(this);
        messageSender = new MessageSender();
    }

    @Override
    public void onClick(View v) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        pwd   = (EditText)findViewById(R.id.passwordB);
        phnNo   = (EditText)findViewById(R.id.phoneNum);
        userName=phnNo.getText().toString().trim();
        password=pwd.getText().toString().trim();
        //TODO determine if user is an admin
        if(userName.equals("8885551544"))
            isAdmin=true;
        Intent intent=null;
        System.out.print("Spandan:::::"+userName.length()+" "+password.length());
        isInternetPresent=cd.isConnectingToInternet();
        if(!isInternetPresent) {
            alertDialog.setMessage("No Network Connectivity");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }
        else if(userName.length()==10&&password.length()>=4) {
            Logger.getLogger("MainActivity").log(Level.INFO, "validated");
            String regex = "^[789]\\d{9}$";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userName);
            if(matcher.matches()) {
                if (isInternetPresent) {
                    Logger.getLogger("MainActivity").log(Level.INFO, "registering");
                    new GcmRegistrationAsyncTask(this).register(userName);
                }
                regId = new RegistrationDetails(context).getRegistrationId();
                Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory() ,null);
                builder.setApplicationName("BiteFast");
                Messaging messaging = builder.build();
                try {
                    Logger.getLogger("Main Activity").log(Level.INFO, "Checking for existing user");
                    if(messaging.messagingEndpoint().findUser(userName)!=null){
                        Logger.getLogger("Main Activity").log(Level.INFO, "Found user");
                        userExists=true;
                    }
                    if(messaging.messagingEndpoint().verifiedUser(userName)!=null){
                        verified=true;
                    }
                    switch (v.getId()) {
                        case R.id.signup:
                            if(!userExists) {
                                intent = new Intent(this, BitefastSignUp.class);
                                intent.putExtra("pwd", pwd.getText().toString().trim());
                                intent.putExtra("phnNo", phnNo.getText().toString().trim());
                                startActivity(intent);
                            }
                            else{
                                alertDialog.setMessage("User Exists");
                                alertDialog.setButton("Please Login", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                            }
                            break;
                        case R.id.signin:
                            //TODO logic check OTP verification details
                            //TODO retrieve username
                            if (userExists) {
                                if (!verified) {
                                    alertDialog.setTitle("Account Not Verified");
                                    alertDialog.setMessage("Enter OTP");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            EditText phnNo = (EditText) findViewById(R.id.phoneNum);
                                            Intent intent = new Intent(MainActivity.this, Otp_Form.class);
                                            intent.putExtra("phnNo", phnNo.getText().toString().trim());
                                            intent.putExtra("uname", userName);
                                            startActivity(intent);
                                        }
                                    });
                                    alertDialog.show();
                                } else {
                                    */
/*Bundle dataBundle = new Bundle();
                                    dataBundle.putString("ACTION", "SIGNIN");
                                    dataBundle.putString("USER_NAME", userName);*//*

                                    */
/*messageSender.sendMessage(dataBundle, gcm);*//*

                                    */
/*new GcmMessagingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle),regId);*//*

                                    Intent i = null;
                                    if (isAdmin) {
                                        i = new Intent(this,
                                                UserListActivity.class);
                                        i.putExtra("UserType", isAdmin);

                                    } else {
                                        i = new Intent(getApplicationContext(),
                                                ChatActivity.class);
                                        //TODO change...
                                        i.putExtra("TOUSER", "8885551544");
                                        i.putExtra("UserType", isAdmin);
                                    }
                                    startActivity(i);
                                }
                            } else {
                                alertDialog.setMessage("User Not Found");
                                alertDialog.setButton("Please Register", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                alertDialog.show();
                            }
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else{
                alertDialog.setMessage("Invalid Mobile Number");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        }else{
            alertDialog.setMessage("Enter Mobile Number/Password");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();

        }
    }
    @Override
    public void onBackPressed()
    {
        AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Exit?");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        alertDialog.show();
    }



}

*/
