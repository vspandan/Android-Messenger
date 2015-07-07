package com.spandan.bitefast.bitefast;

/**
 * Created by Vj on 7/6/2015.
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class ChatDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TO,MySQLiteHelper.COLUMN_MESSAGE,MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_LEFT };

    public ChatDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean createChat(Chat chat) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TO, chat.getTo());
        values.put(MySQLiteHelper.COLUMN_MESSAGE, chat.getMessage());
        values.put(MySQLiteHelper.COLUMN_TIMESTAMP, chat.getTimestamp());
        values.put(MySQLiteHelper.COLUMN_LEFT,chat.getLeft());
        values.put(MySQLiteHelper.COLUMN_PHN,chat.getPhn());
        long insertId = database.insert(MySQLiteHelper.TABLE_CHAT, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAT,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        chat = cursorToComment(cursor);
        cursor.close();
        return chat != null;
    }


    public List<Chat> getAllChats(String phn) {
        List<Chat> chats = new ArrayList<Chat>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAT,
                allColumns, MySQLiteHelper.COLUMN_PHN + " = \'" + phn + "\'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Chat chat = cursorToComment(cursor);
            chats.add(chat);
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
        chat.setId(cursor.getLong(0));
        chat.setTo(cursor.getString(1));
        chat.setMessage(cursor.getString(2));
        chat.setTimestamp(cursor.getLong(3));
        chat.setLeft(cursor.getInt(4));
        return chat;
    }
}
