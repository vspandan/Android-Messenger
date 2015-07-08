package com.bitefast.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.bitefast.services.GcmDataSavingAsyncTask;
import com.bitefast.R;
import com.bitefast.util.RegistrationDetails;
import com.bitefast.util.SystemUiHider;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.User;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BitefastSignUp_2 extends ActionBarActivity implements View.OnClickListener{
    private Button signupbutton=null;
    private boolean successful=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.getLogger("BitefastSignUp").log(Level.INFO, "loading");
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));

        setContentView(R.layout.activity_bf_sign_up2);
        signupbutton = (Button) findViewById(R.id.confirm);
        signupbutton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                try {
                    Logger.getLogger("BitefastSignUp").log(Level.INFO, "Opening sign up form");
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    final EditText addr1 = (EditText) findViewById(R.id.addressLine1);
                    final EditText street = (EditText) findViewById(R.id.streetValue);
                    final EditText landmark = (EditText) findViewById(R.id.landmark);
                    final EditText city = (EditText) findViewById(R.id.cityValue);
                    addr1.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                            if(actionId == 0 || actionId== EditorInfo.IME_ACTION_DONE)
                            {
                                street.requestFocus();
                            }
                            return false;
                        }
                    });
                    street.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                            if(actionId == 0 || actionId== EditorInfo.IME_ACTION_DONE)
                            {
                                landmark.requestFocus();
                            }
                            return false;
                        }
                    });
                    landmark.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                            if(actionId == 0 || actionId== EditorInfo.IME_ACTION_DONE)
                            {
                                signupbutton.requestFocus();
                            }
                            return false;
                        }
                    });
                    try{

                        String phn= getIntent().getStringExtra("Phone");
                        String nameval= getIntent().getStringExtra("Name");
                        String emailVal= getIntent().getStringExtra("Email");
                        String addrVal=addr1.getText().toString().trim();
                        String streetval=street.getText().toString().trim();
                        String landMarkVal= landmark.getText().toString().trim();
                        String regId=new RegistrationDetails().getRegistrationId(getApplicationContext());
                        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                        if(addrVal.isEmpty()){
                            alertDialog.setMessage("Please Enter Address");
                            alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.show();
                            addr1.requestFocus();
                        }
                        else {
                            Logger.getLogger("BitefastSignUp").log(Level.INFO, "Saving device ANDROID_ID details:" + androidId);
                            new RegistrationDetails().storeUserInfo(getApplicationContext(), phn, nameval, emailVal, addrVal, streetval, landMarkVal, city.getText().toString());
                            if (streetval.trim().length()==0)
                                streetval="Optional";
                            if (landMarkVal.trim().length()==0)
                                landMarkVal="Optional";
                            new GcmDataSavingAsyncTask().insertUser(androidId, regId, phn, nameval, emailVal, addrVal, streetval, landMarkVal, city.getText().toString(), false);
                            Logger.getLogger("BitefastSignUp").log(Level.INFO, "Saving device regid details:" + regId);

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
                                Intent i = new Intent(BitefastSignUp_2.this, UserListActivity.class);
                                startActivity(i);
                            }
                            else {

                                Intent i = new Intent(this,
                                        ChatActivity.class);
                                i.putExtra("SENDTO", "BITEFAST_ADMIN");
                                startActivity(i);
                            }
                        }
                    } catch(Exception e) {
                        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                        alertDialog.setMessage("UnSuccessful");
                        alertDialog.setButton("Please Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();

                    }
                }
                catch (Exception e){

                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onBackPressed()
    {

    }

}
