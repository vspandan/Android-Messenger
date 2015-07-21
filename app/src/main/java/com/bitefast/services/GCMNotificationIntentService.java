package com.bitefast.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.bitefast.R;
import com.bitefast.activities.ChatActivity;
import com.bitefast.beans.UserListBean;
import com.bitefast.receiver.GcmBroadcastReceiver;
import com.bitefast.sqlite.MySQLiteHelper;
import com.bitefast.sqlite.MySQLiteHelper2;
import com.bitefast.util.Constants;
import com.bitefast.util.RegistrationDetails;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.simple.JSONValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GCMNotificationIntentService extends IntentService implements Constants {

    private String androidId = null;
    private SQLiteDatabase database = null;

    public GCMNotificationIntentService() {
        super("GCMNotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String messageType = gcm.getMessageType(intent);

        if (extras != null && messageType != null) {
            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
                    sendNotification("Send error", "Error", "Error");
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
                    sendNotification("Deleted messages on server", "Expired Message on Server", "Expired Message on Server");
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                        .equals(messageType)) {
                    String from = extras.get("FROM").toString();
                    String receivedMsg = extras.get("CHATMESSAGE").toString();
                    String msgID = extras.get("ID").toString();
                    String msgTS = extras.get("MSGTIMESTAMP").toString();
                    String sendTo = extras.get("SENDTO").toString();
                    String deviceId = extras.get("DEVICEID").toString();

                    try {
                        if ("CHAT".equals(extras.get("SM"))) {
                            //NOTE: duplicate db calls I know; But no option. Please do take care while making changes
                            Intent chatIntent = new Intent("com.bitefast.chatmessage");
                            chatIntent.putExtra("CHATMESSAGE", receivedMsg);
                            chatIntent.putExtra("FROM", from);
                            chatIntent.putExtra("MSGTIMESTAMP", new Date().getTime());
                            chatIntent.putExtra("ID", msgID);


                            MySQLiteHelper dbHelper = new MySQLiteHelper(getApplicationContext());
                            database = dbHelper.getWritableDatabase();


                            ContentValues values = new ContentValues();
                            values.put(MySQLiteHelper.COLUMN_MSG_ID, msgID);
                            values.put(MySQLiteHelper.COLUMN_TO, from);
                            values.put(MySQLiteHelper.COLUMN_MESSAGE, receivedMsg);
                            values.put(MySQLiteHelper.COLUMN_TIMESTAMP, new Date().getTime());
                            values.put(MySQLiteHelper.COLUMN_LEFT, 1);
                            values.put(MySQLiteHelper.COLUMN_PHN, from);
                            values.put(MySQLiteHelper.COLUMN_SENT_STATUS, 1);
                            values.put(MySQLiteHelper.COLUMN_DELIVERED_STATUS, 1);
                            String Query = "Select * from " + MySQLiteHelper.TABLE_CHAT + " where " + MySQLiteHelper.COLUMN_MSG_ID + " = '" + msgID +"'";
                            Cursor cursor = database.rawQuery(Query, null);
                            long insertId =0;
                            int count=cursor.getCount();
                            Logger.getLogger("Row Count1:").log(Level.INFO, "" + count);
                            if(count <= 0){
                                insertId = database.insert(MySQLiteHelper.TABLE_CHAT, null,
                                        values);
                                cursor.close();
                            }
                            cursor.close();
                            database.close();
                            dbHelper.close();

                            if(new RegistrationDetails().isAdmin(getApplicationContext())) {

                                Intent intent1 = new Intent("com.bitefast.update.userlist");
                                intent1.putExtra("CHATMESSAGE", receivedMsg);
                                intent1.putExtra("FROM", from);
                                intent1.putExtra("MSGTIMESTAMP", new Date().getTime());
                                intent1.putExtra("ID", msgID);

                                MySQLiteHelper2 dbHelper2 = new MySQLiteHelper2(getApplicationContext());
                                database = dbHelper2.getWritableDatabase();

                                ContentValues values1 = new ContentValues();
                                values1.put(MySQLiteHelper2.COLUMN_NAME, from);
                                values1.put(MySQLiteHelper2.COLUMN_READ, "" + false);
                                values1.put(MySQLiteHelper2.COLUMN_TIMESTAMP, new Date().getTime());
                                values1.put(MySQLiteHelper2.COLUMN_REGPHN, sendTo);
                                Query = "Select * from " + MySQLiteHelper2.TABLE_USER + " where " + MySQLiteHelper2.COLUMN_NAME + " = " + from;
                                cursor = database.rawQuery(Query, null);
                                count=cursor.getCount();
                                Logger.getLogger("Row Count2:").log(Level.INFO, "" + count);
                                if (count > 0) {
                                    database.delete(MySQLiteHelper2.TABLE_USER, MySQLiteHelper2.COLUMN_NAME + " = " + from,
                                            null);
                                    cursor.close();
                                }
                                database.insert(MySQLiteHelper2.TABLE_USER, null,
                                        values1);
                                database.close();
                                cursor.close();
                                dbHelper2.close();
                                sendBroadcast(intent1);
                            }
                            if (insertId>0) {
                                sendBroadcast(chatIntent);
                                sendNotification(receivedMsg,from,from);
                            }

                            HashMap<String, String> dataBundle = new HashMap<String, String>();
                            dataBundle.put("DEVICEID", androidId);
                            dataBundle.put("ACTION", "ACK");
                            dataBundle.put("FROM", new RegistrationDetails().getPhoneNum(getApplicationContext()));
                            dataBundle.put("SENDTO", from);
                            dataBundle.put("ID", "" + msgID);
                            dataBundle.put("MSGTIMESTAMP", "" + msgTS);
                            dataBundle.put("CHATMESSAGE", "ACK");
                            Logger.getLogger(this.getClass().getName()+":SENDINGACK:").log(Level.INFO, msgID);
                            new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle));
                        } else if ("DELVIRARY_ACK".equals(extras.get("SM"))) {
                            //NOTE: duplicate db calls I know; But no option. Please do take care while making changes
                            Logger.getLogger(this.getClass().getName()+":DELIVERYACK:").log(Level.INFO, msgID);
                            MySQLiteHelper dbHelper = new MySQLiteHelper(getApplicationContext());
                            database = dbHelper.getWritableDatabase();

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MySQLiteHelper.COLUMN_DELIVERED_STATUS, 1);
                            contentValues.put(MySQLiteHelper.COLUMN_SENT_STATUS, 1);
                            database.update(MySQLiteHelper.TABLE_CHAT, contentValues, MySQLiteHelper.COLUMN_MSG_ID + " = '" + msgID + "'",
                                    null);
                            database.close();
                            dbHelper.close();

                            Intent chatIntent = new Intent("com.bitefast.chatmessage.deliveryack");
                            chatIntent.putExtra("FROM", from);
                            Logger.getLogger(this.getClass().getName()+":DELIVERYACK:").log(Level.INFO, "Sending BroadCast");
                            sendBroadcast(chatIntent);

                        } else if ("SERVER_ACK".equals(extras.get("SM"))) {
                            //NOTE: duplicate db calls I know; But no option. Please do take care while making changes
                            Logger.getLogger(this.getClass().getName()+":SENTACT:").log(Level.INFO, msgID);
                            MySQLiteHelper dbHelper = new MySQLiteHelper(getApplicationContext());
                            database = dbHelper.getWritableDatabase();

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MySQLiteHelper.COLUMN_SENT_STATUS, 1);
                            database.update(MySQLiteHelper.TABLE_CHAT, contentValues, MySQLiteHelper.COLUMN_MSG_ID + " = '" + msgID + "'",
                                    null);
                            database.close();
                            dbHelper.close();
                            Intent chatIntent = new Intent("com.bitefast.chatmessage.sentack");
                            chatIntent.putExtra("FROM", from);
                            Logger.getLogger(this.getClass().getName()+":SENTACT:").log(Level.INFO, "Sending BroadCast");
                            sendBroadcast(chatIntent);
                        }
                    } catch (Error e) {

                    }
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String message, String from, String title) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SENDTO", from);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.ic_launcher,
                "New Message", System.currentTimeMillis());
        notification.sound = defaultSoundUri;
        notification.setLatestEventInfo(this, title,
                "New Message", pendingIntent);

        try {
            notificationManager.notify((int)(Long.parseLong("8886799788")/10), notification);
        } catch (NumberFormatException e) {
            notificationManager.notify(9999, notification);
        }
    }

}