package com.spandan.bitefast.gcmbackend.models;

/**
 * Created by rubbernecker on 5/7/15.
 */
public class UserDetails {
    private String androidID;
    private String regId;
    private String phoneNum;
    private String name;
    private String email;
    private String addr;
    private String street;
    private String landmark;
    private String city;

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "androidID='" + androidID + '\'' +
                ", regId='" + regId + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", addr='" + addr + '\'' +
                ", street='" + street + '\'' +
                ", landmark='" + landmark + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
