package com.spandan.bitefast.bitefast;

/**
 * Created by rubbernecker on 7/7/15.
 */
public class DeviceLoginBean {
    public long id;
    public String androidId;
    public String phone;
    public String name;
    public String email;
    public String addr1;
    public String addr2;
    public String landmark2;
    public String city;
    public DeviceLoginBean(long id,String androidId, String phone, String name, String email, String addr1, String addr2, String landmark2, String city) {
        this.id=id;
        this.androidId = androidId;
        this.phone = phone;
        this.name = name;
        this.email = email;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.landmark2 = landmark2;
        this.city = city;
    }

}
