package com.bitefast.util;

import org.json.simple.JSONValue;

import java.util.HashMap;

/**
 * Created by spandanv on 6/27/2015.
 */
public class Test {
    public static void main(String[] args) {
        HashMap<String,String> dataBundle = new HashMap<String,String>();
        dataBundle.put("ACTION", "USERLIST");
        System.out.print(JSONValue.toJSONString(dataBundle));
    }
}
