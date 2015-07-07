package com.bitefast.models;

/**
 * Created by rubbernecker on 3/7/15.
 */
public class User {
    private String userNum=null;
    private boolean admin=false;

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "userNum='" + userNum + '\'' +
                ", admin=" + admin +
                '}';
    }
}
