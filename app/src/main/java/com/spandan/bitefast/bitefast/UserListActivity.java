package com.spandan.bitefast.bitefast;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserListActivity extends ActionBarActivity {
    private static final String TAG = "UserListActivity";
    private Intent intent;
    private MessageSender messageSender;
    private GoogleCloudMessaging gcm;
    private Context context = null;
    private String regId=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("BiteFast");
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));

        setContentView(R.layout.activity_user_list);
        context=getApplicationContext();
        regId=new RegistrationDetails().getRegistrationId(getApplicationContext());
        intent = new Intent(this, GCMNotificationIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.spandan.bitefast.bitefast.userlist"));
        messageSender = new MessageSender();
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
        HashMap dataBundle =new HashMap();
        dataBundle.put("ACTION", "USERLIST");
        new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle),regId);
        /*Button refreshButton = (Button) findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                *//*HashMap dataBundle =new HashMap();
                dataBundle.put("ACTION", "USERLIST");
                new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle),regId);*//*
            }
        });*/
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.getLogger("Received").log(Level.INFO,intent.getStringExtra("USERLIST") );
            updateUI(intent.getStringExtra("USERLIST"));
        }
    };

    private void updateUI(String userList) {

        String[] userListArr = userList.split(":");

        List<String> list = new ArrayList<String>();
        for (String s : userListArr) {
            if (s != null && s.length() > 0) {
                list.add(s);
            }
        }
        userListArr = list.toArray(new String[list.size()]);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, userListArr);
        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;

                String itemValue = (String) adapter.getItem(position);

                Intent i = new Intent(getApplicationContext(),
                        ChatActivity.class);
                i.putExtra("SENDTO", itemValue);
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu) {
            //TODO
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Exit?");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}