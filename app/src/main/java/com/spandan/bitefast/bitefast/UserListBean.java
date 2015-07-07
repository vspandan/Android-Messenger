package com.spandan.bitefast.bitefast;

import java.util.Date;

/**
 * Created by rubbernecker on 7/7/15.
 */
public class UserListBean  implements Comparable<UserListBean> {
    public long id;
    public String name;
    public String read;
    public long timestamp;


    public UserListBean(String name, String read) {
        this.name = name;
        this.read = read;
        this.timestamp= new Date().getTime();
    }

    public UserListBean(long id, String name, String read,long timestamp) {
        this.id=id;
        this.name = name;
        this.read = read;
        this.timestamp= timestamp;
    }

    public UserListBean() {

    }


    public int compareTo(UserListBean userListBean) {
        return (int)(this.timestamp-userListBean.timestamp);
    }

}
