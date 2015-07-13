package com.bitefast.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.bitefast.util.RegistrationDetails;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.bitefast.R;
import com.bitefast.activities.MainActivity;
import com.bitefast.beans.Chat;
import com.bitefast.datasource.ChatDataSource;
import com.bitefast.receiver.GcmBroadcastReceiver;

import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GCMNotificationIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCMNotificationIntentService";
    private String androidId =null;

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String messageType = gcm.getMessageType(intent);

        if (extras != null && messageType !=null) {
            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
                    sendNotification("Send error","Error");
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
                    sendNotification("Deleted messages on server","Expired Message on Server");
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                        .equals(messageType)) {

                    if("CHAT".equals(extras.get("SM"))){
                        String from=extras.get("FROM").toString();
                        String receivedMsg=extras.get("CHATMESSAGE").toString();
                        String msgID=extras.get("ID").toString();
                        String msgTS=extras.get("MSGTIMESTAMP").toString();

                        Intent chatIntent = new Intent("com.bitefast.chatmessage");
                        chatIntent.putExtra("CHATMESSAGE",receivedMsg);
                        chatIntent.putExtra("FROM", from);
                        sendNotification(receivedMsg,from);
                        ChatDataSource chatDataSource = new ChatDataSource(getApplicationContext());
                        chatDataSource.open();
                        Chat chat=new Chat(from,receivedMsg,1,from,true,msgTS);
                        chatDataSource.createChat(chat);
                        chatDataSource.close();
                        /*Logger.getLogger("NotificationService:").log(Level.INFO, extras.toString());*/
                        sendBroadcast(chatIntent);

                        //TODO ques: do we need to store id of recieved msg along with timestamp
                        //TODO ACK the server saying message is delivered

                        HashMap<String,String> dataBundle = new HashMap<String,String>();
                        dataBundle.put("DEVICEID", androidId);
                        dataBundle.put("ACTION", "ACK");
                        dataBundle.put("FROM", new RegistrationDetails().getPhoneNum(getApplicationContext()));
                        dataBundle.put("SENDTO", from);
                        dataBundle.put("ID", "" + msgID);
                        dataBundle.put("MSGTIMESTAMP", "" + msgTS);

                        new GcmDataSavingAsyncTask().sendMessage(JSONValue.toJSONString(dataBundle));
                        /*Logger.getLogger("NotificationService:").log(Level.INFO, JSONValue.toJSONString(dataBundle));*/

                    }
                    else if("ACK".equals(extras.get("SM"))){
                        /*Logger.getLogger("NotificationService: ACK:").log(Level.INFO, extras.toString());*/
                        String from=extras.get("FROM").toString();
                        String msgID=extras.get("ID").toString();
                        String msgTS=extras.get("MSGTIMESTAMP").toString();

                        ChatDataSource chatDataSource = new ChatDataSource(getApplicationContext());
                        chatDataSource.open();
                        chatDataSource.updateChat(msgID, "" + true);
                        chatDataSource.close();

                    }

                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String message,String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.ic_launcher,
                "New Message", System.currentTimeMillis());
        notification.sound=defaultSoundUri;
        notification.setLatestEventInfo(this, title,
                "New Message", pendingIntent);



        /*notificationManager.notify(9999 *//* ID of notification *//*, notification);*/
    }
}

