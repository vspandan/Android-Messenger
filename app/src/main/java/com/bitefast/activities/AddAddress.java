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

import com.bitefast.services.GcmDataSavingAsyncTask;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.bitefast.R;
import com.bitefast.util.RegistrationDetails;
import com.bitefast.util.SystemUiHider;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class AddAddress extends ActionBarActivity implements View.OnClickListener{
    private Button signupbutton=null;
    private boolean successful=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.getLogger("MobileInfoLogin").log(Level.INFO, "loading");
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));

        setContentView(R.layout.activity_address_form);
        if (!isTaskRoot())
        {
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
                    Logger.getLogger("MobileInfoLogin").log(Level.INFO, "Opening sign up form");
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

                        String phn= new RegistrationDetails().getPhoneNum(getApplicationContext());
                        String addrVal=addr1.getText().toString().trim();
                        String streetval=street.getText().toString().trim();
                        String landMarkVal= landmark.getText().toString().trim();
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
                        else if(streetval.isEmpty()){
                            alertDialog.setMessage("Please Enter Street");
                            alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.show();
                            street.requestFocus();
                        }
                        else {
                            new GcmDataSavingAsyncTask().insertAddress(phn, addrVal, streetval, landMarkVal, city.getText().toString());
                            startActivity(new Intent(AddAddress.this, ProfileView.class));
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
        super.onBackPressed();
        startActivity(new Intent(AddAddress.this, ProfileView.class));
    }

}
