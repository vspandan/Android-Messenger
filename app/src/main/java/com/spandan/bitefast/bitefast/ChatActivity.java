package com.spandan.bitefast.bitefast;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.Random;

public class ChatActivity extends ActionBarActivity {
    private static final String TAG = "ChatActivity";
    private String regId = "";
    private ChatArrayAdapter chatArrayAdapter;
	private ListView listView;
	private EditText chatText;
    private Button buttonSend;
    private boolean isAdmin=false;
    private GoogleCloudMessaging gcm;
    private Intent intent;
	private static Random random;
    private String sendTo;
    private MessageSender messageSender;
    private Context context=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        context=getApplicationContext();
        regId=new RegistrationDetails().getRegistrationId(getApplicationContext());
        isAdmin=new RegistrationDetails().isAdmin(getApplicationContext());
        Log.d(TAG, "Is Admin: " + isAdmin);
        sendTo = getIntent().getStringExtra("SENDTO");
		setContentView(R.layout.activity_chat);
        if (isAdmin)
            this.setTitle(sendTo);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xffffac26));
        buttonSend = (Button) findViewById(R.id.buttonSend);
        intent = new Intent(this, GCMNotificationIntentService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("com.spandan.bitefast.bitefast.chatmessage"));
		random = new Random();
        messageSender = new MessageSender();
		listView = (ListView) findViewById(R.id.listView1);
        gcm = GoogleCloudMessaging.getInstance(context);

		chatArrayAdapter = new ChatArrayAdapter(context, R.layout.activity_chat_singlemessage);
		listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
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

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
	}

    private boolean sendChatMessage(){

        HashMap<String,String> dataBundle = new HashMap<String,String>();
        dataBundle.put("ACTION", "CHAT");
        dataBundle.put("FROM", new RegistrationDetails().getPhoneNum(getApplicationContext()));
        //added this for debugging purpose
        /*dataBundle.put("SENDTO", new RegistrationDetails().getPhoneNum(getApplicationContext()));*/
        dataBundle.put("SENDTO", sendTo);
        dataBundle.put("CHATMESSAGE", chatText.getText().toString());
        new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle), regId);
        new GcmDataSavingAsyncTask().saveMessage(regId,new RegistrationDetails().getPhoneNum(getApplicationContext()), sendTo,chatText.getText().toString());

        chatArrayAdapter.add(new ChatMessage(false, chatText.getText().toString().trim()));
        chatText.setText("");
        return true;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, " Chat onReceive: " + intent.getStringExtra("CHATMESSAGE"));
            chatArrayAdapter.add(new ChatMessage(true, intent.getStringExtra("CHATMESSAGE").trim()));
        }
    };
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    @Override
    public void onBackPressed()
    {
        if(isAdmin) {
            HashMap dataBundle =new HashMap();
            dataBundle.put("ACTION", "USERLIST");
            new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle),regId);
            Intent i = new Intent(this,
                    UserListActivity.class);
            i.putExtra("UserType", isAdmin);
            startActivity(i);
        } else {
            AlertDialog alertDialog=new AlertDialog.Builder(this).create();
            alertDialog.setMessage("Exit?");
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
        if(isAdmin) {
            getMenuInflater().inflate(R.menu.menu_chat_admin, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_chat, menu);
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
            //TODO
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Order Amount");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String orderId=getRandomOrderId();
                    String amount=input.getText().toString();
                    chatText.setText("Thanks for Ordering.\nYour Bill Amount: "+amount+"\nOrder Id: "+ orderId);
                    sendChatMessage();
                    new GcmDataSavingAsyncTask().saveOrder(orderId,sendTo,amount);
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
        if (id == R.id.action_more) {
            //TODO
            return true;
        }
        if (id == R.id.action_more_help) {
            //TODO
            return true;
        }
        if (id == R.id.action_help) {
            //TODO
            return true;
        }
        if (id == R.id.action_signout) {
            //TODO
            return true;
        }
        if (id == R.id.action_search) {
            //TODO
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String getRandomOrderId() {
        return Long.toString(random.nextLong());
    }
}
