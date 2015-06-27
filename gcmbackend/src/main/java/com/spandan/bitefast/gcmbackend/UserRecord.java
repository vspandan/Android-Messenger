package com.spandan.bitefast.gcmbackend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by spandanv on 6/27/2015.
 */
@Entity
public class UserRecord {
    @Id
    Long id;

    @Index
    private String name;
    private String email;
    private String addressLine1;
    private String street;
    private String city;

    public UserRecord(String country, Long id, String name, String addressLine1, String street, String city, String phoneNum, boolean verified, String opt, String password) {
        this.country = country;
        this.id = id;
        this.name = name;
        this.addressLine1 = addressLine1;
        this.street = street;
        this.city = city;
        this.phoneNum = phoneNum;
        this.verified = verified;
        this.opt = opt;
        this.password = password;
    }

    private String phoneNum;
    private String country;
    private boolean verified;
    private String opt;
    private String password;

    public UserRecord() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public UserRecord(String name, String addressLine1, String street, String city, String phoneNum, String country, boolean verified, String opt, String password) {
        this.id = id;
        this.name = name;
        this.addressLine1 = addressLine1;
        this.street = street;
        this.city = city;
        this.phoneNum = phoneNum;
        this.country = country;
        this.verified = verified;
        this.opt = opt;
        this.password = password;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
