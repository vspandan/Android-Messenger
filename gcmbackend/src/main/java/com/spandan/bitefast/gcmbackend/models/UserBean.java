package com.spandan.bitefast.gcmbackend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by spandanv on 6/28/2015.
 */

@Entity
public class UserBean {
    @Id
    int phoneNum;

    @Index
    private boolean isAdmin;

    public UserBean(){

    }

    public UserBean(boolean isAdmin, int phoneNum) {
        this.isAdmin = isAdmin;
        this.phoneNum = phoneNum;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }
}
