package com.bitefast.datasource;

/**
 * Created by Vj on 7/6/2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bitefast.beans.ChatMessage;
import com.bitefast.sqlite.MySQLiteHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


public class ChatDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public String[] allColumns = {
            MySQLiteHelper.COLUMN_MSG_ID,MySQLiteHelper.COLUMN_TO,MySQLiteHelper.COLUMN_PHN, MySQLiteHelper.COLUMN_LEFT, MySQLiteHelper.COLUMN_MESSAGE,MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_SENT_STATUS, MySQLiteHelper.COLUMN_DELIVERED_STATUS };

    public ChatDataSource() {
    }

    public ChatDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    private long msgId;
    private String to;
    private String phn;
    private boolean left;
    private String message;
    private long timestamp;
    private boolean delivered = false;
    private boolean sent = false;


    public String createChat(ChatMessage chat) {
        String id = chat.getPhn()+Long.toString(Math.abs(new Random().nextLong()));
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MSG_ID,id );
        values.put(MySQLiteHelper.COLUMN_TO, chat.getTo());
        values.put(MySQLiteHelper.COLUMN_PHN, chat.getPhn());
        values.put(MySQLiteHelper.COLUMN_TIMESTAMP, chat.getTimestamp());
        if(chat.isLeft())
            values.put(MySQLiteHelper.COLUMN_LEFT, 1);
        else
            values.put(MySQLiteHelper.COLUMN_LEFT, 0);

        values.put(MySQLiteHelper.COLUMN_MESSAGE, chat.getMessage());
        if(chat.isDelivered())
            values.put(MySQLiteHelper.COLUMN_DELIVERED_STATUS, 1);
        else
            values.put(MySQLiteHelper.COLUMN_DELIVERED_STATUS, 0);
        if(chat.isSent())
            values.put(MySQLiteHelper.COLUMN_SENT_STATUS, 1);
        else
            values.put(MySQLiteHelper.COLUMN_SENT_STATUS, 0);
        long insertId = database.insert(MySQLiteHelper.TABLE_CHAT, null,
                values);
        return id;
    }

    public void deleteChat(String phn){
        database.delete(MySQLiteHelper.TABLE_CHAT, MySQLiteHelper.COLUMN_PHN + " = \'" + phn + "\'", null);
    }

    public List<ChatMessage> getAllChats(String phn) {
        List<ChatMessage> chats = new ArrayList<ChatMessage>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAT,
                allColumns, MySQLiteHelper.COLUMN_PHN + " = \'" + phn + "\'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ChatMessage chat = cursorToComment(cursor);
            chats.add(chat);
            cursor.moveToNext();
        }
        cursor.close();

        return chats;
    }

    public Set<ChatMessage> getSortedChats(String phn){
        return new TreeSet<ChatMessage>(getAllChats(phn));
    }

    public List<ChatMessage> getSortedChatMessages(String phn){
        Set<ChatMessage> chats=getSortedChats(phn);
        Iterator<ChatMessage> itr=chats.iterator();
        List<ChatMessage> chatMessages=new ArrayList<ChatMessage>();
        boolean left=false;
        while(itr.hasNext()){
            ChatMessage chat=itr.next();
            chatMessages.add(chat);
        }
        return chatMessages;
    }

    public ChatMessage cursorToComment(Cursor cursor) {
        ChatMessage chat = new ChatMessage();
        chat.setMsgId(cursor.getString(0));
        chat.setTo(cursor.getString(1));
        chat.setPhn(cursor.getString(2));
        if(cursor.getInt(3)==0)
            chat.setLeft(false);
        else
            chat.setLeft(true);
        chat.setMessage(cursor.getString(4));
        chat.setTimestamp(cursor.getLong(5));
        if(cursor.getInt(6)==0)
            chat.setSent(false);
        else
            chat.setSent(true);

        if(cursor.getInt(7)==0)
            chat.setDelivered(false);
        else
            chat.setDelivered(true);

        return chat;
    }
}
