package com.bitefast.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.bitefast.beans.ChatMessage;
import com.bitefast.datasource.ChatDataSource;
import com.bitefast.sqlite.MySQLiteHelper;
import com.bitefast.util.RegistrationDetails;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.spandan.bitefast.gcmbackend.messaging.Messaging;

import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rubbernecker on 14/7/15.
 */
public class HeartBeatService extends Service {


    public Context context = null;
    private GcmKeepAlive gcmKeepAlive;
    private String androidId;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        gcmKeepAlive.start();
        Log.d("HeartBeatService", "starting heartbeat countdown timer");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        context = getApplicationContext();
        gcmKeepAlive = new GcmKeepAlive(context);
    }

    private class GcmKeepAlive extends CountDownTimer {

        protected CountDownTimer timer;
        protected Context mContext;
        protected Intent gTalkHeartBeatIntent;
        protected Intent mcsHeartBeatIntent;


        public GcmKeepAlive(Context context) {
            super(30 * 1000, 30 * 1000);
            mContext = context;
            gTalkHeartBeatIntent = new Intent("com.google.android.intent.action.GTALK_HEARTBEAT");
            mcsHeartBeatIntent = new Intent("com.google.android.intent.action.MCS_HEARTBEAT");
            Log.d("GcmKeepAlive", "starting heartbeat countdown timer ");
            this.start();
        }


        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Log.d("GcmKeepAlive", "sending heart beat to keep gcm alive");
            //TODO find and send unsend messages
            new ReqUndeliveredMessage().execute();
            mContext.sendBroadcast(gTalkHeartBeatIntent);
            mContext.sendBroadcast(mcsHeartBeatIntent);
            this.start();
        }
    }

    private class ReqUndeliveredMessage extends AsyncTask<Void, Void, Void> {
        private Messaging msgService = null;
        private List<ChatMessage> chats= new ArrayList<ChatMessage>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (msgService == null) {
                Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null);
                builder.setApplicationName("BiteFast");
                msgService = builder.build();
            }

            reSendUndeliveredMessages();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                msgService.reSendMessages(new RegistrationDetails().getPhoneNum(context)).execute();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        //NOTE: duplicate db calls I know; But no option. Please do take care while making changes
        protected void reSendUndeliveredMessages() {
            MySQLiteHelper dbHelper = new MySQLiteHelper(getApplicationContext());
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ChatDataSource chatDataSource = new ChatDataSource();
            Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAT,
                    chatDataSource.allColumns, MySQLiteHelper.COLUMN_SENT_STATUS + " = 0", null, null, null, null);
            int count = cursor.getCount();
            Log.d("GcmKeepAlive", "resending unsent message : " + count);

            chats.clear();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ChatMessage chat = chatDataSource.cursorToComment(cursor);
                chats.add(chat);
                cursor.moveToNext();
            }
            cursor.close();
            database.close();
            dbHelper.close();
            try {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        for(ChatMessage chat : chats) {
                            HashMap<String, String> dataBundle = new HashMap<String, String>();
                            dataBundle.put("DEVICEID", androidId);
                            dataBundle.put("ACTION", "CHAT");
                            if (new RegistrationDetails().isAdmin(getApplicationContext()))
                                dataBundle.put("FROM", "BITEFAST_ADMIN");
                            else
                                dataBundle.put("FROM", chat.getPhn());
                            dataBundle.put("SENDTO", chat.getTo());
                            dataBundle.put("CHATMESSAGE", chat.getMessage());
                            dataBundle.put("MSGTIMESTAMP", "" + chat.getTimestamp());
                            dataBundle.put("ID", "" + chat.getMsgId());
                            try {
                                msgService.sendMessage(JSONValue.toJSONString(dataBundle)).execute();
                            } catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                });
                t.start();
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e){

            }

        }
    }


}