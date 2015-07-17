package com.bitefast.beans;

import java.util.Date;

public class ChatMessage implements Comparable<ChatMessage> {

    //DB elements
    private String msgId;
    private String to;
    private String phn;
    private boolean left;
    private String message;
    private long timestamp;
    private boolean delivered = false;
    private boolean sent = false;

    public ChatMessage() {

    }

    public ChatMessage(String to, String message, boolean left, String phn, boolean sent, boolean delivered) {
        this.to = to;
        this.left = left;
        this.message = message;
        this.timestamp = new Date().getTime();
        this.phn = phn;
        this.sent = sent;
        this.delivered = delivered;
    }

    public ChatMessage(String id, String to, String message, boolean left, String phn, boolean sent, boolean delivered) {
        this.msgId = id;
        this.to = to;
        this.left = left;
        this.message = message;
        this.timestamp = new Date().getTime();
        this.phn = phn;
        this.sent = sent;
        this.delivered = delivered;
    }


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhn() {
        return phn;
    }

    public void setPhn(String phn) {
        this.phn = phn;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String gettS() {
        d.setTime(timestamp);
        return d.getHours() + ":" + d.getMinutes();
    }

    public void settS(String tS) {
        this.tS = tS;
    }

    Date d = new Date();


    public String tS = d.getHours() + ":" + d.getMinutes();

    @Override
    public int compareTo(ChatMessage another) {
        return (int) (this.getTimestamp() - another.getTimestamp());
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "msgId='" + msgId + '\'' +
                ", to='" + to + '\'' +
                ", phn='" + phn + '\'' +
                ", left=" + left +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", delivered=" + delivered +
                ", sent=" + sent +
                ", d=" + d +
                ", tS='" + tS + '\'' +
                '}';
    }
}