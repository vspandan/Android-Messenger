package com.spandan.bitefast.bitefast;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by spandanv on 6/26/2015.
 */
public class RegistrationDetails extends Application{
    private static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";


    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(REG_ID, "");
        return registrationId;
    }


    public void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.commit();
    }

    public void storeUserInfo(Context context,String phoneNum, String userName,
                              String email,
                              String addr,
                              String street,
                              String landMark,
                              String city){

        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneNum", phoneNum);
        editor.putString("userName", userName);
        editor.putString("email", email);
        editor.putString("addr", addr);
        editor.putString("street", street);
        editor.putString("landMark", landMark);
        editor.putString("city", city);
        editor.commit();
    }


    public boolean isLoggedIn(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(REG_ID, "");
        if(registrationId.isEmpty() || prefs.getString("userName","").isEmpty() ||
            prefs.getString("email", "").isEmpty() ||
            prefs.getString("addr", "").isEmpty() ||
            prefs.getString("phoneNum", "").isEmpty()||
            prefs.getString("city", "").isEmpty()){
            return false;
        }
        return true;
    }
    public void storeAsOtpVerified(Context context){
        //TODO forfuture
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("verified", true);
        editor.commit();
    }

    public boolean otpVerified(Context context){
        //TODO forfuture
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getBoolean("verified", false);
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(RegistrationDetails.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    public String getPhoneNum(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getString("phoneNum", "");
    }
    public boolean isAdmin(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getBoolean("admin", false);
    }
    public void setAdmin(Context context){
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("admin", true);
        editor.commit();
    }

}
