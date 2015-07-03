package com.spandan.bitefast.bitefast;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.simple.JSONValue;

import java.util.HashMap;


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
        signupbutton.setOnClickListener(this);
        messageSender=new MessageSender();
        context=getApplicationContext();
        regId=new RegistrationDetails().getRegistrationId(getApplicationContext());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                //TODO logic to verify otp
                if(confirmed) {
                    String userName = null;
                    Bundle extras = getIntent().getExtras();
                    if(extras !=null) {
                         userName = extras.getString("USER_NAME");
                    }
                    HashMap<String,String> dataBundle =new HashMap<String,String>();
                    dataBundle.put("ACTION", "USERLIST");
                    dataBundle.put("USER_NAME", userName);
                    new GcmMessagingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle), regId);
                    new RegistrationDetails().otpVerified(getApplicationContext());
                    Intent i = new Intent(this,
                            ChatActivity.class);
                    i.putExtra("TOUSER", userName);
                    startActivity(i);
                }
                else{
                    //TODO Action for failure
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
