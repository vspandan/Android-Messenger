package com.bitefast.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.multidex.MultiDex;
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
import com.bitefast.R;
import com.bitefast.util.SystemUiHider;
import com.bitefast.util.Utilities;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BitefastSignUp extends ActionBarActivity implements View.OnClickListener {
    private Button signupbutton = null;
    private boolean successful = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.getLogger("BitefastSignUp").log(Level.INFO, "loading");
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));
        setContentView(R.layout.activity_bf_sign_up);
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
                    final EditText phone = (EditText) findViewById(R.id.phoneValue);
                    final EditText name = (EditText) findViewById(R.id.nameValue);
                    final EditText email = (EditText) findViewById(R.id.emailValue);
                    phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                                name.requestFocus();
                            }
                            return false;
                        }
                    });
                    name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                                email.requestFocus();
                            }
                            return false;
                        }
                    });
                    email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                                signupbutton.requestFocus();
                            }
                            return false;
                        }
                    });

                    String phn = phone.getText().toString().trim();
                    String nameval = name.getText().toString().trim();
                    String emailVal = email.getText().toString().trim();
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    if (!Utilities.isPhnNoValid(phn)) {
                        alertDialog.setMessage("Invalid Phone Num");
                        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                        phone.requestFocus();

                    } else if (nameval.isEmpty()) {
                        alertDialog.setMessage("Please Enter Name");
                        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                        name.requestFocus();
                    } else if (!Utilities.isEmailValid(emailVal)) {
                        alertDialog.setMessage("InValid Email");
                        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialog.show();
                        email.requestFocus();
                    } else {
                        Intent i = new Intent(BitefastSignUp.this, BitefastSignUp_2.class);
                        i.putExtra("Phone", phn);
                        i.putExtra("Name", nameval);
                        i.putExtra("Email", emailVal);
                        startActivity(i);
                    }
                } catch (Exception e) {
                    Logger.getLogger("BitefastSignUp").log(Level.INFO, e.getMessage());
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
