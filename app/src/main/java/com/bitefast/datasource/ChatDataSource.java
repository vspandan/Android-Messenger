package com.bitefast.datasource;

/**
 * Created by Vj on 7/6/2015.
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bitefast.beans.ChatMessage;
import com.bitefast.beans.Chat;
import com.bitefast.sqlite.MySQLiteHelper;


public class ChatDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_TO,MySQLiteHelper.COLUMN_MESSAGE,MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_LEFT, MySQLiteHelper.COLUMN_SENT_STATUS,MySQLiteHelper.COLUMN_MSG_ID };

    public ChatDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public String createChat(Chat chat) {
        String id = chat.getPhn()+Long.toString(Math.abs(new Random().nextLong()));
        /*Logger.getLogger("ChatDataSource:").log(Level.INFO, chat.toString());*/
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MSG_ID,id );
        values.put(MySQLiteHelper.COLUMN_TO, chat.getTo());
        values.put(MySQLiteHelper.COLUMN_MESSAGE, chat.getMessage());
        values.put(MySQLiteHelper.COLUMN_TIMESTAMP, chat.getTimestamp());
        values.put(MySQLiteHelper.COLUMN_LEFT, chat.getLeft());
        values.put(MySQLiteHelper.COLUMN_PHN,chat.getPhn());
        values.put(MySQLiteHelper.COLUMN_SENT_STATUS, chat.isSent());
        long insertId = database.insert(MySQLiteHelper.TABLE_CHAT, null,
                values);
        return id;
    }

    public boolean updateChat(String  msgId, String readStat) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLiteHelper.COLUMN_SENT_STATUS, readStat);
        long id=database.update(MySQLiteHelper.TABLE_CHAT, contentValues, MySQLiteHelper.COLUMN_MSG_ID + " = '" + msgId+"'",
                null);
        return id > 0;
    }

    public List<Chat> getAllChats(String phn) {
        List<Chat> chats = new ArrayList<Chat>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAT,
                allColumns, MySQLiteHelper.COLUMN_PHN + " = \'" + phn + "\'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Chat chat = cursorToComment(cursor);
            chats.add(chat);
            /*Logger.getLogger("ChatDataSource: ALl chats:").log(Level.INFO, chat.toString());*/
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return chats;
    }

    public Set<Chat> getSortedChats(String phn){
        return new TreeSet<Chat>(getAllChats(phn));
    }

    public List<ChatMessage> getSortedChatMessages(String phn){
        Set<Chat> chats=getSortedChats(phn);
        Iterator<Chat> itr=chats.iterator();
        List<ChatMessage> chatMessages=new ArrayList<ChatMessage>();
        boolean left=false;
        while(itr.hasNext()){
            Chat chat=itr.next();
            if(chat.getLeft()==0)
                left=false;
            if(chat.getLeft()==1)
                left=true;

            ChatMessage chatMessage=new ChatMessage(left,chat.getMessage());
            chatMessages.add(chatMessage);
        }
        return chatMessages;
    }

    private Chat cursorToComment(Cursor cursor) {
        Chat chat = new Chat();
        chat.setTo(cursor.getString(0));
        chat.setMessage(cursor.getString(1));
        chat.setTimestamp(cursor.getLong(2));
        chat.setLeft(cursor.getInt(3));
        chat.setSent(Boolean.parseBoolean(cursor.getString(4)));
        chat.setId(cursor.getInt(5));
        return chat;
    }
}
