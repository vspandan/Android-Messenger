package com.bitefast.activities;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bitefast.services.GCMNotificationIntentService;
import com.bitefast.R;
import com.bitefast.util.RegistrationDetails;
import com.bitefast.datasource.UserDataSource;
import com.bitefast.adapters.UserListArrayAdapter;
import com.bitefast.beans.UserListBean;
import com.bitefast.beans.UserListItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserListActivity extends ActionBarActivity {

    private static final String TAG = "UserListActivity";
    private Intent intent;
    private String regId=null;
    private List<String> senderList=null;
    private UserListArrayAdapter userListArrayAdapter;
    private ListView listView = null;
    private UserDataSource userDataSource;
    private String iam=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("BiteFast");
        senderList=new ArrayList<String>();
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));
        setContentView(R.layout.activity_user_list);
        iam=new RegistrationDetails().getPhoneNum(getApplicationContext());
        intent = new Intent(this, GCMNotificationIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.bitefast.beans.chatmessage"));
        userDataSource=new UserDataSource(getApplicationContext());
        userDataSource.open();

        listView = (ListView) findViewById(R.id.list);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;

                UserListItem itemValue = userListArrayAdapter.getItem(position);
                view.setBackgroundColor(Color.CYAN);
                Intent i = new Intent(getApplicationContext(),
                        ChatActivity.class);
                boolean status=userDataSource.updateChat(itemValue.message,""+true);
                Logger.getLogger("UserListActivity:Update:").log(Level.INFO, "" + status);
                i.putExtra("SENDTO", itemValue.message);
                startActivity(i);
            }
        });
        updateUI();
    }

    private void updateUI(){
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

        userListArrayAdapter.clear();
        List<UserListBean> chatMessages=userDataSource.getSortedChatMessages(iam);
        Iterator<UserListBean> itr=chatMessages.iterator();
        while(itr.hasNext()){
            UserListBean chatMessage=itr.next();
            boolean stat=Boolean.parseBoolean(chatMessage.read);
            Logger.getLogger("UserListActivity:UPDATEUI:DATA:").log(Level.INFO, chatMessage.name + ":" + chatMessage.read+":"+stat);
            userListArrayAdapter.add(new UserListItem(stat,chatMessage.name));
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
            }
            UserListBean bean=new UserListBean(from,""+ false,iam);
            userDataSource.createUserListEntry(bean);
            updateUI();
        }
    };

    @Override
    public void onBackPressed() {

        AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        alertDialog.setMessage("See You Soon");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
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