package com.bitefast.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitefast.R;
import com.bitefast.adapters.ChatArrayAdapter;
import com.bitefast.adapters.DrawerListAdapter;
import com.bitefast.beans.ChatMessage;
import com.bitefast.beans.NavItem;
import com.bitefast.datasource.ChatDataSource;
import com.bitefast.layoutclasses.PreferencesFragment;
import com.bitefast.services.GCMNotificationIntentService;
import com.bitefast.services.GcmDataSavingAsyncTask;
import com.bitefast.util.RegistrationDetails;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.spandan.bitefast.gcmbackend.messaging.model.UserDetails;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatActivity extends ActionBarActivity {
    private static final String TAG = "ChatActivity";

    private String regId = "";
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean isAdmin = false;
    private GoogleCloudMessaging gcm;
    private Intent intent;
    private static Random random;
    private String sendTo;
    private Context context = null;
    private String androidId = null;
    private String androidIdReceiver = null;
    private ChatDataSource chatDataSource;
    private ArrayList<NavItem> mNavItems;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerPane;
    private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        random = new Random();
        mNavItems = new ArrayList<NavItem>();
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        regId = new RegistrationDetails().getRegistrationId(getApplicationContext());
        isAdmin = new RegistrationDetails().isAdmin(getApplicationContext());
        Log.d(TAG, "Is Admin: " + isAdmin);
        sendTo = getIntent().getStringExtra("SENDTO");
        setContentView(R.layout.activity_chat);

        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (isAdmin) {
            this.setTitle(sendTo);
            nMgr.cancel((int) (Long.parseLong("8886799788") / 10));
        } else
            nMgr.cancel(9999);
        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);

        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));
        buttonSend = (Button) findViewById(R.id.buttonSend);
        intent = new Intent(this, GCMNotificationIntentService.class);

        registerReceiver(broadcastReceiver, new IntentFilter("com.bitefast.chatmessage"));
        registerReceiver(broadcastReceiver1, new IntentFilter("com.bitefast.chatmessage.sentack"));
        registerReceiver(broadcastReceiver2, new IntentFilter("com.bitefast.chatmessage.deliveryack"));

        gcm = GoogleCloudMessaging.getInstance(context);

        chatText = (EditText) findViewById(R.id.chatText);
        chatText.setBackgroundColor(Color.WHITE);
        chatText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });
        chatDataSource = new ChatDataSource(getApplicationContext());
        chatDataSource.open();
        updateUI();
        TextView userProfileName = (TextView) findViewById(R.id.profileUserName);
        userProfileName.setText(new RegistrationDetails().getUserName(getApplicationContext()).toUpperCase());

        TextView userPhnNum = (TextView) findViewById(R.id.phnNum);
        userPhnNum.setText(new RegistrationDetails().getPhoneNum(getApplicationContext()));
        mNavItems.add(new NavItem("Menu", "Go through Item List"));
        mNavItems.add(new NavItem("Order", "View Past Orders"));
        mNavItems.add(new NavItem("Settings", "Update Your Profile"));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.chatActivity);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
    }

    private void updateUI() {
        chatArrayAdapter = new ChatArrayAdapter(context, R.layout.activity_chat_singlemessage);
        List<ChatMessage> chatMessages = chatDataSource.getSortedChatMessages(sendTo);
        Iterator<ChatMessage> itr = chatMessages.iterator();
        while (itr.hasNext()) {
            ChatMessage chatMessage = itr.next();
            chatArrayAdapter.add(chatMessage);
        }
        listView = (ListView) findViewById(R.id.listView1);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        listView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        listView.setSelection(listView.getCheckedItemCount() - 1);

    }

    private boolean sendChatMessage() {
        String message = chatText.getText().toString().trim();
        if (message == null || message.isEmpty())
            return false;

        //Saving message to LocalDB
        ChatMessage chat = new ChatMessage(sendTo, message, false, sendTo, false, false);
        String id = chatDataSource.createChat(chat);

        HashMap<String, String> dataBundle = new HashMap<String, String>();
        dataBundle.put("DEVICEID", androidId);
        dataBundle.put("ACTION", "CHAT");
        if (isAdmin)
            dataBundle.put("FROM", "BITEFAST_ADMIN");
        else
            dataBundle.put("FROM", new RegistrationDetails().getPhoneNum(getApplicationContext()));
        dataBundle.put("SENDTO", sendTo);
        dataBundle.put("CHATMESSAGE", message);
        dataBundle.put("MSGTIMESTAMP", "" + chat.getTimestamp());
        dataBundle.put("ID", "" + id);

        new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle));

        chatArrayAdapter.add(chat);
        chatText.setText("");
        return true;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, " Chat onReceive: " + intent.getStringExtra("CHATMESSAGE"));
            String from = intent.getStringExtra("FROM");
            if (from.equals(sendTo)) {
                androidIdReceiver = intent.getStringExtra("DEVICEID");
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setTo(from);
                chatMessage.setPhn(new RegistrationDetails().getPhoneNum(getApplicationContext()));
                chatMessage.setLeft(true);
                chatMessage.setMessage(intent.getStringExtra("CHATMESSAGE"));
                chatMessage.setTimestamp(intent.getLongExtra("MSGTIMESTAMP", new Date().getTime()));
                chatMessage.setMsgId(intent.getStringExtra("ID"));
                chatMessage.setDelivered(true);
                chatMessage.setSent(true);
                chatArrayAdapter.add(chatMessage);
            }
        }
    };

    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.getLogger(this.getClass().getName() + ":SENTACT:").log(Level.INFO, "Received BroadCast");
            updateUI();
        }
    };

    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.getLogger(this.getClass().getName() + ":DELIVERYACK:").log(Level.INFO, "Received BroadCast");
            updateUI();
        }
    };

    //TODO onclick update the table saying that its not new message anymore
    //TODO if activity is already open then this also has to be considered as read
    //TODO going back should kill

    @Override
    protected void onResume() {
        chatDataSource.open();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiver1);
        unregisterReceiver(broadcastReceiver2);
        chatDataSource.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getIntent().removeExtra("SENDTO");
        if (isAdmin) {
            Intent i = new Intent(this,
                    UserListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("See You Soon");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            alertDialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isAdmin) {
            getMenuInflater().inflate(R.menu.menu_chat_admin, menu);
        } else {
            /*getMenuInflater().inflate(R.menu.menu_chat, menu);*/
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_menu1) {
            return true;
        }

        if (id == R.id.action_orders) {
            return true;
        }


        if (id == R.id.action_menu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Order Amount");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String orderId = getRandomOrderId();
                    String amount = input.getText().toString();
                    chatText.setText("Thanks for Ordering.\nYour Bill Amount: " + amount + "\nOrder Id: " + orderId);
                    sendChatMessage();
                    new GcmDataSavingAsyncTask().saveOrder(orderId, sendTo, amount);

                    UserDetails details = new GcmDataSavingAsyncTask().fetchDetails(new RegistrationDetails().getPhoneNum(getApplicationContext()));
                    if (details != null) {
                        String streetName=details.getStreet();
                        String landmark=details.getLandmark();
                        if("Optional".equals(streetName))
                            streetName="";

                        if("Optional".equals(landmark))
                            landmark="";
                        String text="Your Order (" + orderId + ") will be delivered to:" + details.getName().toUpperCase() + " " + details.getAddr().toUpperCase() + " " + streetName.toUpperCase() + landmark.toUpperCase() + details.getCity().toUpperCase();
                        chatText.setText(Html.fromHtml(text));

                        sendChatMessage();
                        chatText.setText("If there is a change let us know");
                        sendChatMessage();
                    } else {
                        chatText.setText("Share delivery address or We can call you");
                        sendChatMessage();
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getRandomOrderId() {
        return Integer.toString(Math.abs(random.nextInt()));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }

    private void selectItemFromDrawer(int position) {
        Fragment fragment = new PreferencesFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction();

        mDrawerList.setItemChecked(position, true);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.popup);
        relativeLayout.setVisibility(View.VISIBLE);
        Button close = (Button) findViewById(R.id.button_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });
        mDrawerLayout.closeDrawer(mDrawerPane);
    }
}

