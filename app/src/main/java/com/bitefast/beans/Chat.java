package com.bitefast.beans;

import java.util.Date;

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
    private boolean sent;

    //Future Use

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    private boolean delivered;

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Chat() {
    }

    public Chat(String to, String message, int left, String phn, boolean sent) {
        this.to = to;
        this.left=left;
        this.message = message;
        this.timestamp= new Date().getTime();
        this.phn=phn;
        this.sent=sent;
    }

    public Chat(String to, String message, int left, String phn, boolean sent, String timeStamp) {
        this.to = to;
        this.left=left;
        this.message = message;
        this.timestamp= Long.parseLong(timeStamp);
        this.phn=phn;
        this.sent=sent;
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

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                ", left=" + left +
                ", timestamp=" + timestamp +
                ", phn='" + phn + '\'' +
                ", sent=" + sent +
                ", delivered=" + delivered +
                '}';
    }
}
