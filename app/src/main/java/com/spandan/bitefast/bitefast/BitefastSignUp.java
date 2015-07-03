package com.spandan.bitefast.bitefast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.bitefast.util.SystemUiHider;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BitefastSignUp extends ActionBarActivity implements View.OnClickListener{
    private Button signupbutton=null;
    private boolean successful=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.getLogger("BitefastSignUp").log(Level.INFO, "loading");
        super.onCreate(savedInstanceState);
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
                    EditText phone = (EditText) findViewById(R.id.phoneValue);
                    EditText name = (EditText) findViewById(R.id.nameValue);
                    EditText email = (EditText) findViewById(R.id.emailValue);
                    EditText addr1 = (EditText) findViewById(R.id.addressLine1);
                    EditText street = (EditText) findViewById(R.id.streetValue);
                    EditText landmark = (EditText) findViewById(R.id.landmark);
                    Spinner city = (Spinner) findViewById(R.id.cityValue);
                    try{

                        String phn=phone.getText().toString().trim();
                        String regId=new RegistrationDetails().getRegistrationId(getApplicationContext());
                        new RegistrationDetails().storeUserInfo(getApplicationContext(), phn, name.getText().toString(), email.getText().toString(), addr1.getText().toString(), street.getText().toString(), landmark.getText().toString(), city.getSelectedItem().toString());
                        new GcmDataSavingAsyncTask().registerDevice(regId,phn);
                        new GcmDataSavingAsyncTask().insertUser(regId, phn, name.getText().toString(), email.getText().toString(), addr1.getText().toString(), street.getText().toString(), landmark.getText().toString(), city.getSelectedItem().toString(), false);
                        Logger.getLogger("BitefastSignUp").log(Level.INFO, "Saving device regid details:" + regId);

                        Intent i= new Intent(this, Otp_Form.class);
                        i.putExtra("USER_NAME", phn);
                        startActivity(i);
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
