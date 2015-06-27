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
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    Messaging messaging = builder.build();
                    EditText name = (EditText) findViewById(R.id.nameValue);
                    EditText addr1 = (EditText) findViewById(R.id.addressLine1);
                    EditText street = (EditText) findViewById(R.id.streetValue);
                    Spinner country = (Spinner) findViewById(R.id.countryValue);
                    Spinner city = (Spinner) findViewById(R.id.cityValue);
                    Intent data = getIntent();
                    messaging.messagingEndpoint().addUserRecord(name.getText().toString().trim(), addr1.getText().toString().trim(), street.getText().toString().trim(), city.getSelectedItem().toString().trim(), data.getStringExtra("phnNo"), country.getSelectedItem().toString().trim(), data.getStringExtra("pwd"));
                    if (successful) {

                        startActivity(new Intent(this, Otp_Form.class));
                    } else {
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
        startActivity(new Intent(this, MainActivity.class));
    }

}
