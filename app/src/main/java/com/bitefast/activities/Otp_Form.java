package com.bitefast.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.bitefast.services.GcmDataSavingAsyncTask;
import com.bitefast.services.MessageSender;
import com.bitefast.R;
import com.bitefast.util.RegistrationDetails;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.User;

import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Otp_Form extends ActionBarActivity implements View.OnClickListener{
    private Button signupbutton=null;
    private boolean confirmed=true;
    private MessageSender messageSender=null;
    private GoogleCloudMessaging gcm = null;
    private Context context = null;
    private String regId=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_form);
        signupbutton = (Button) findViewById(R.id.confirm);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));
        signupbutton.setOnClickListener(this);
        messageSender=new MessageSender();
        context=getApplicationContext();
        regId=new RegistrationDetails().getRegistrationId(getApplicationContext());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                //TOD logic to verify otp
                if(confirmed) {
                    new RegistrationDetails().otpVerified(getApplicationContext());
                    final String phoneNum =new RegistrationDetails().getPhoneNum(getApplicationContext());
                    final boolean values[] = new boolean[1];
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
                    if(values[0]) {
                        new RegistrationDetails().setAdmin(getApplicationContext());
                        HashMap<String,String> dataBundle =new HashMap<String,String>();
                        dataBundle.put("ACTION", "USERLIST");
                        new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle));
                        Intent i = new Intent(Otp_Form.this, UserListActivity.class);
                        startActivity(i);
                    }
                    else {

                        Intent i = new Intent(this,
                                ChatActivity.class);
                        i.putExtra("SENDTO", "BITEFAST_ADMIN");
                        startActivity(i);
                    }
                }
                else{
                    //TOD Action for failure
                    startActivity(new Intent(this, Otp_Form.class));
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, MainActivity.class));
    }

}
