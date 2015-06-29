package com.spandan.bitefast.gcmbackend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class RegistrationRecord {

    @Id
    private Long phoneNum;

    @Index
    private String regId;
    // you can add more fields...

    public RegistrationRecord() {
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public long getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(long phoneNum) {
        this.phoneNum = phoneNum;
    }

}