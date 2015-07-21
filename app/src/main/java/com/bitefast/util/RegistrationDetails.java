package com.bitefast.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

    public void setPhoneNum(Context context, String phoneNum){
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneNum", phoneNum);
        editor.commit();
    }


    private SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(RegistrationDetails.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    public String getPhoneNum(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getString("phoneNum", "");
    }

    public void onLogout(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public boolean isAdmin(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getBoolean("admin", false);
    }

    public void setAdmin(Context context,boolean isadmin){
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("admin", isadmin);
        editor.commit();
    }

    public String getEmailId(Context applicationContext) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        return prefs.getString("email", "");
    }

    public String getUserName(Context applicationContext) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        return prefs.getString("name", "");
    }
    public void setUserName(Context applicationContext, String name) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.commit();
    }

    public void setEmailId(Context applicationContext, String emailVal) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", emailVal);
        editor.commit();
    }
}
