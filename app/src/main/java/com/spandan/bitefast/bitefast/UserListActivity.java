package com.spandan.bitefast.bitefast;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserListActivity extends ActionBarActivity {

    private final HashMap<String,Boolean> msgReadStatus=new HashMap<String, Boolean>();
    private static final String TAG = "UserListActivity";
    private Intent intent;
    private String regId=null;
    private List<String> senderList=null;
    private UserListArrayAdapter userListArrayAdapter;
    private ListView listView = null;
    private UserDataSource userDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("BiteFast");
        senderList=new ArrayList<String>();
        ActionBar bar = getSupportActionBar();

        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));

        setContentView(R.layout.activity_user_list);

        intent = new Intent(this, GCMNotificationIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.spandan.bitefast.bitefast.chatmessage"));

        listView = (ListView) findViewById(R.id.list);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        userListArrayAdapter = new UserListArrayAdapter(
                getApplicationContext(), android.R.layout.simple_list_item_1);

        userListArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(userListArrayAdapter.getCount() - 1);
            }
        });

        listView.setAdapter(userListArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;

                UserListItem itemValue = (UserListItem) userListArrayAdapter.getItem(position);
                view.setBackgroundColor(Color.CYAN);
                Intent i = new Intent(getApplicationContext(),
                        ChatActivity.class);
                msgReadStatus.put(itemValue.message, true);
                i.putExtra("SENDTO", itemValue.message);
                startActivity(i);
                finish();
            }
        });

        userDataSource=new UserDataSource(getApplicationContext());
        userDataSource.open();
        List<UserListBean> chatMessages=userDataSource.getSortedChatMessages();
        Iterator<UserListBean> itr=chatMessages.iterator();
        while(itr.hasNext()){
            UserListBean chatMessage=itr.next();
            userListArrayAdapter.add(new UserListItem(Boolean.getBoolean(chatMessage.read),chatMessage.name));
        }
    }

    private String msg;
    private String from;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            msg=intent.getExtras().getString("CHATMESSAGE");
            from=intent.getExtras().getString("FROM");
            Logger.getLogger("UserListActivity:BroadCastReceiver:DATA:").log(Level.INFO, from + ":" + msg);

            UserListItem userListItem=new UserListItem(false,from);
            int pos=userListArrayAdapter.getPosition(userListItem);
            Logger.getLogger("UserListActivity:Position:").log(Level.INFO, "" + pos);
            if(pos>=0) {
                boolean status=userDataSource.deleteChat(from);
                Logger.getLogger("UserListActivity:Delete Status:").log(Level.INFO, "" + status);
                userListArrayAdapter.remove(userListArrayAdapter.getItem(pos));
            }
            userListArrayAdapter.add(userListItem);
            UserListBean bean=new UserListBean();
            bean.name=from;
            bean.read=""+false;
            userDataSource.createChat(bean);
            listView.setAdapter(userListArrayAdapter);
        }
    };

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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}