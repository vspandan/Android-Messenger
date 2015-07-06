package com.spandan.bitefast.bitefast;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Date;
import java.sql.Time;

/**
 * Created by Vj on 7/5/2015.
 */
public class Chat implements Comparable<Chat>{
    private long id;
    private String to;
    private String message;
    private int left;
    private long timestamp;
    private String phn;

    public Chat() {
    }

    public Chat(String to, String message, int left, String phn) {
        this.to = to;
        this.left=left;
        this.message = message;
        this.timestamp= new Date().getTime();
        this.phn=phn;
    }

    public Chat(long id, String to, String message, long timestamp,int left, String phn) {
        this.id = id;
        this.to = to;
        this.left = left;
        this.message = message;
        this.timestamp = timestamp;
        this.phn=phn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int compareTo(Chat chat) {


        //ascending order
        //return this.quantity - compareQuantity;

        //descending order
        return (int)(this.getTimestamp()-chat.getTimestamp());

    }

    public String getPhn() {
        return phn;
    }

    public void setPhn(String phn) {
        this.phn = phn;
    }
}
