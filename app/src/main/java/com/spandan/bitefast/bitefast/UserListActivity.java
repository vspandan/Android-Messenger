package com.spandan.bitefast.bitefast;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserListActivity extends ActionBarActivity {

    private final HashMap<String,Boolean> msgReadStatus=new HashMap<String, Boolean>();
    private static final String TAG = "UserListActivity";
    private Intent intent;
    private MessageSender messageSender;
    private GoogleCloudMessaging gcm;
    private Context context = null;
    private String regId=null;
    private List<String> senderList=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("BiteFast");
        senderList=new ArrayList<String>();
        ActionBar bar = getSupportActionBar();

        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));

        setContentView(R.layout.activity_user_list);

        /*Set<String> temp=new RegistrationDetails().retrieveChatUserList(getApplicationContext());*/
        /*if(temp!=null&&temp.size()!=0)
            updateUI(temp);*/
        context=getApplicationContext();
        regId=new RegistrationDetails().getRegistrationId(getApplicationContext());
        intent = new Intent(this, GCMNotificationIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.spandan.bitefast.bitefast.chatmessage"));
        messageSender = new MessageSender();
        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
        HashMap dataBundle =new HashMap();
        dataBundle.put("ACTION", "USERLIST");
        new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle),regId);

    }

    private String msg;
    private String from;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            msg=intent.getExtras().getString("CHATMESSAGE");
            from=intent.getExtras().getString("FROM");
            Logger.getLogger("UserListActivity:BroadCastReceiver:DATA:").log(Level.INFO, from + ":" + msg);
            updateUI(from, msg);
        }
    };

    private void updateUI(Set<String> list) {
        updateUIActivity(new ArrayList<String>(list));
    }

    private void updateUI(String from, String message) {
        if (senderList.contains(from))
            senderList.remove(from);
        senderList.add(from);

        /*new RegistrationDetails().storeChatUserList(getApplicationContext(), new LinkedHashSet<String>(senderList));*/
        updateUIActivity(senderList);
        //refresh colors
    }

    public void updateUIActivity(List<String> senderList){

        Collections.reverse(senderList);

        String[] userListArr = senderList.toArray(new String[senderList.size()]);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, userListArr);


        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;

                String itemValue = (String) adapter.getItem(position);
                view.setBackgroundColor(Color.CYAN);
                Intent i = new Intent(getApplicationContext(),
                        ChatActivity.class);
                msgReadStatus.put(itemValue,true);
                i.putExtra("SENDTO", itemValue);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setMessage("See You Soon");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        alertDialog.show();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}