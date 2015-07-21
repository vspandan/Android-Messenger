package com.bitefast.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bitefast.R;
import com.bitefast.util.RegistrationDetails;
import com.bitefast.util.SystemUiHider;
import com.bitefast.util.Utilities;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MobileInfoLogin extends ActionBarActivity implements View.OnClickListener {
    private Button signupbutton = null;
    private boolean successful = true;
    private String regId = null;
    private String androidId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        regId = new RegistrationDetails().getRegistrationId(getApplicationContext());
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "loading");
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));
        setContentView(R.layout.activity_mobileinfo);
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        signupbutton = (Button) findViewById(R.id.confirm);
        signupbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                try {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Opening sign up form");
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    final EditText phone = (EditText) findViewById(R.id.phoneValue);
                    String phn = phone.getText().toString().trim();
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    if (!Utilities.isPhnNoValid(phn)) {
                        alertDialog.setMessage("Invalid Phone Num");
                        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                        phone.requestFocus();

                    } else {
                        new RegistrationDetails().setPhoneNum(getApplicationContext(), phn);
                        final String phoneNum = phn;
                        final List<User> user = new ArrayList<User>();
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
                                        user.add(msgService.profileInfo(phoneNum).execute());
                                    } catch (Exception ex) {
                                        System.exit(1);
                                        ex.printStackTrace();
                                    }

                                }
                            });
                            t.start();
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (user.size() != 0) {
                            User usr = user.get(0);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, phoneNum + ":" + usr.toString());
                            new RegistrationDetails().setAdmin(getApplicationContext(),usr.getAdmin());
                            if (usr.getUserNum() == null || usr.getEmailId() == null) {
                                Intent i = new Intent(this,
                                        EmailInfoForm.class);
                                startActivity(i);
                            } else if (usr.getAdmin()) {

                                 Intent i = new Intent(MobileInfoLogin.this, UserListActivity.class);
                                 startActivity(i);
                             } else{
                                new RegistrationDetails().setUserName(getApplication(), usr.getUserName());
                                new RegistrationDetails().setEmailId(getApplication(), usr.getEmailId());
                                Intent i = new Intent(this,
                                        ChatActivity.class);
                                i.putExtra("SENDTO", "BITEFAST_ADMIN");
                                startActivity(i);
                            }
                        }
                        else{
                                Intent i = new Intent(this,
                                        EmailInfoForm.class);
                                startActivity(i);
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }
}
