package com.bitefast.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bitefast.R;
import com.bitefast.util.RegistrationDetails;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;
import com.spandan.bitefast.gcmbackend.messaging.model.UserDetails;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileView extends ActionBarActivity {

    private List<UserDetails> userAddresses = new ArrayList<UserDetails>();
    private Messaging msgService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_profile_view);

        TextView addrButton = (TextView) findViewById(R.id.addrbutton);
        addrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(ProfileView.this, AddAddress.class));
            }
        });
        TextView phoneNum = (TextView) findViewById(R.id.phoneNum);
        phoneNum.setText(new RegistrationDetails().getPhoneNum(getApplicationContext()));
        TextView email = (TextView) findViewById(R.id.emailid);
        email.setText(new RegistrationDetails().getEmailId(getApplicationContext()));
        TextView name = (TextView) findViewById(R.id.profileName);
        name.setText(new RegistrationDetails().getUserName(getApplicationContext()).toUpperCase());
        populateAddress();
    }

    public void populateAddress() {


        AsyncTask<Void, Void, String> sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (msgService == null) {
                    Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    builder.setApplicationName("BiteFast");
                    msgService = builder.build();
                }
                String msg = "";
                try {
                    UserDetails userDetails = msgService.fetchAddress(new RegistrationDetails().getPhoneNum(getApplicationContext())).execute();
                    msg = userDetails.getJsonAddressListString();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                try {

                    TableLayout table = (TableLayout) findViewById(R.id.profileTable);
                    List<HashMap<String, String>> value = (List<HashMap<String, String>>) JSONValue
                            .parseWithException(msg);
                    for (HashMap<String, String> val : value) {
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Addr:" + val.toString());
                        final TableRow row = (TableRow) LayoutInflater.from(ProfileView.this).inflate(R.layout.addrs_element, null);
                        ((TextView) row.findViewById(R.id.streetValueTV)).setText(val.get("Street"));
                        ((TextView) row.findViewById(R.id.cityValueTV)).setText(val.get("City"));
                        ((TextView) row.findViewById(R.id.addressLineTV)).setText(val.get("Address"));
                        ((TextView) row.findViewById(R.id.landmarkTV)).setText(val.get("LandMark"));
                        ((TextView) row.findViewById(R.id.Id)).setText(val.get("id"));

                        row.setOnLongClickListener(new View.OnLongClickListener() {

                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog alertDialog = new AlertDialog.Builder(ProfileView.this).create();
                                alertDialog.setMessage("Delete Address?");
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            msgService.removeAddress(((TextView) row.findViewById(R.id.Id)).getText().toString());
                                            row.setVisibility(View.INVISIBLE);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                                alertDialog.show();
                                return false;
                            }
                        });
                        table.addView(row);
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

            }
        };
        sendTask.execute(null, null, null);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileView.this,ChatActivity.class);
        if(! new RegistrationDetails().isAdmin(getApplicationContext()))
            intent.putExtra("SENDTO", "BITEFAST_ADMIN");
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            new RegistrationDetails().onLogout(getApplicationContext());
            AlertDialog alertDialog = new AlertDialog.Builder(ProfileView.this).create();
            alertDialog.setMessage("Logout?");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ProfileView.this, MainActivity.class));
                }
            });
            alertDialog.show();
            return true;
        }

        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!new RegistrationDetails().isAdmin(getApplicationContext())) {
            getMenuInflater().inflate(R.menu.menu_profile_view, menu);
        }
        return true;
    }


}
