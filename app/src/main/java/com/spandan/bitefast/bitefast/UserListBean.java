package com.spandan.bitefast.bitefast;

/**
 * Created by rubbernecker on 7/7/15.
 */
public class UserListBean {
    public long id;
    public String name;
    public String read;

    public UserListBean(long id, String name, String read) {
        this.id=id;
        this.name = name;
        this.read = read;
    }

    public UserListBean() {

    }
}
