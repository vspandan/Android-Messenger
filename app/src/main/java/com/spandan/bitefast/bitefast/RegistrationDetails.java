package com.spandan.bitefast.bitefast;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by spandanv on 6/26/2015.
 */
public class RegistrationDetails {
    private static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    private Context context = null;

    public RegistrationDetails(Context context){
        this.context = context;
    }

    public String getRegistrationId() {
        final SharedPreferences prefs = context.getSharedPreferences(
                "BiteFast", Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        registrationId="APA91bFd0cW2fln35cCBmMxHAiuJZ2OyiYYCgVRD5p_3A6H55qkiUMDeX87Tr75isosxa7zmj3JfjqAlFMW4Kwp25feKgXVM5GRG-Omia2h_1EOQyufWihTyZfBKmYJD7fDY-I8GKYbk";
        if (registrationId.isEmpty()) {
            return "";
        }
        return registrationId;
    }


    public void storeRegistrationId(String regId) {
        final SharedPreferences prefs = context.getSharedPreferences(
                "BiteFast", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.commit();
    }

    public void storeUserInfo(String phoneNum, String userName,
                              String email,
                              String addr,
                              String street,
                              String landMark,
                              String city){

        final SharedPreferences prefs = context.getSharedPreferences(
                "BiteFast", Context.MODE_PRIVATE);
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
    public boolean isLoggedIn() {
        final SharedPreferences prefs = context.getSharedPreferences(
                "BiteFast", Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if(registrationId.isEmpty() || prefs.getString("userName","").isEmpty() ||
            prefs.getString("email", "").isEmpty() ||
            prefs.getString("addr", "").isEmpty() ||
            prefs.getString("street", "").isEmpty()||
            prefs.getString("landMark", "").isEmpty()||
                prefs.getString("phoneNum", "").isEmpty()||
            prefs.getString("city", "").isEmpty()){
            return false;
        }
        return true;
    }
    public void storAsOtpVerified(){

        final SharedPreferences prefs = context.getSharedPreferences(
                "BiteFast", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("verified", true);
        editor.commit();
    }

    public boolean otpVerified(){
        final SharedPreferences prefs = context.getSharedPreferences(
                "BiteFast", Context.MODE_PRIVATE);
        return prefs.getBoolean("verified", false);
    }

    public String getPhoneNum() {
        final SharedPreferences prefs = context.getSharedPreferences(
                "BiteFast", Context.MODE_PRIVATE);
        return prefs.getString("phoneNum", "");
    }
}
