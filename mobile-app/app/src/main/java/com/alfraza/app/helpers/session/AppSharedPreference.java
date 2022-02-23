package com.alfraza.app.helpers.session;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.alfraza.app.helpers.utilities.Constants;

import static com.alfraza.app.helpers.utilities.Constants.FIRST_LAUNCH;
import static com.alfraza.app.helpers.utilities.Constants.USER_ROLE;

public class AppSharedPreference {
    private static AppSharedPreference sInstance;
    private SharedPreferences userpref, fcmprefs, sessionpref, gpspref, settingspref, notfpref;
    private SharedPreferences.Editor useredit, fcmedit, sessionedit, settingsedit, gpsedit, notfedit;

    static synchronized AppSharedPreference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppSharedPreference(context);
        }
        return sInstance;
    }

    @SuppressLint("CommitPrefEdits")
    private AppSharedPreference(Context context) {
        fcmprefs = context.getSharedPreferences(Constants.FCM_PREF, Context.MODE_PRIVATE);
        userpref = context.getSharedPreferences(Constants.USER_PREF, Context.MODE_PRIVATE);
        sessionpref = context.getSharedPreferences(Constants.SESS_PREF, Context.MODE_PRIVATE);
        settingspref = context.getSharedPreferences(Constants.SETTINGS_PREF, Context.MODE_PRIVATE);
        gpspref = context.getSharedPreferences(Constants.GPS_PREF, Context.MODE_PRIVATE);
        notfpref = context.getSharedPreferences(Constants.NOTF_PREF, Context.MODE_PRIVATE);
        fcmedit = fcmprefs.edit();
        useredit = userpref.edit();
        sessionedit = sessionpref.edit();
        settingsedit = settingspref.edit();
        gpsedit = gpspref.edit();
        notfedit = notfpref.edit();
    }

    /* FCM Preference */

    boolean hasUserSubscribeToNotification() {
        return fcmprefs.getBoolean(Constants.SET_NOTIFY, false);
    }

    public String GetToken() {
        return fcmprefs.getString(Constants.NTOKEN, null);
    }

    String GetOldToken() {
        return fcmprefs.getString(Constants.OTOKEN, null);
    }

    public void saveNotificationSubscription(boolean value) {
        fcmedit.putBoolean(Constants.SET_NOTIFY, value);
        fcmedit.apply();
    }

    public void saveNewToken(String value) {
        fcmedit.putString(Constants.NTOKEN, value);
        fcmedit.apply();
    }

    public void saveOldToken(String value) {
        fcmedit.putString(Constants.OTOKEN, value);
        fcmedit.apply();
    }

    /* Session Preference */
    public void setFirstLaunch(boolean flag) {
        sessionpref.edit().putBoolean(FIRST_LAUNCH, flag).apply();
    }

    public boolean isFirstLaunch() {
        return sessionpref.getBoolean(FIRST_LAUNCH, true);
    }

    public void setRole(String role) {
        sessionpref.edit().putString(USER_ROLE, role).apply();
    }

    public String getRole() {
        return sessionpref.getString(USER_ROLE, null);
    }

    public String GetLang() {
        return sessionpref.getString(Constants.LANG, Constants.DEF_LANG);
    }

    void SetLang(String value) {
        sessionedit.putString(Constants.LANG, value);
        sessionedit.apply();
    }

    public String GetPromo() {
        return sessionpref.getString(Constants.PROMO, "");
    }

    public void SetPromo(String value) {
        sessionedit.putString(Constants.PROMO, value);
        sessionedit.apply();
    }

    boolean RequiredPhone() {
        return sessionpref.getBoolean(Constants.REQ_PHONE, false);
    }

    public void setRequiredPhone(boolean value) {
        sessionedit.putBoolean(Constants.REQ_PHONE, value);
        sessionedit.apply();
    }

    public void setCityGroup(int value) {
        sessionedit.putInt(Constants.CITY_GROUP, value);
        sessionedit.apply();
    }

    public int getCityGroup() {
        return sessionpref.getInt(Constants.CITY_GROUP, 0);
    }

    public void setCity(int value) {
        sessionedit.putInt(Constants.CITY, value);
        sessionedit.apply();
    }

    public int getCity() {
        return sessionpref.getInt(Constants.CITY, 0);
    }


    public void setCityName(String value) {
        sessionedit.putString(Constants.CITY_NAME, value);
        sessionedit.apply();
    }

    public String getCityName() {
        return sessionpref.getString(Constants.CITY_NAME, "");
    }

    public void setLatitude(double value) {
        sessionedit.putFloat(Constants.LATITUDE, (float) value);
        sessionedit.apply();
    }

    public double getLatitude() {
        return (double) sessionpref.getFloat(Constants.LATITUDE, 0f);
    }

    public void setLongitude(double value) {
        sessionedit.putFloat(Constants.LONGITUDE, (float) value);
        sessionedit.apply();
    }

    public double getLongitude() {
        return (double) sessionpref.getFloat(Constants.LONGITUDE, 0f);
    }

    /* User Preference */

    public boolean IsUserlogged() {
        return userpref.getBoolean(Constants.IS_LOGGED_IN, false);
    }

    public String GetAuthMethod() {
        return userpref.getString(Constants.AUTHMETHOD, null);
    }

    public int GetUserid() {
        return userpref.getInt(Constants.USERID, 0);
    }

    public String GetUsername() {
        return userpref.getString(Constants.NAME, null);
    }

    public String GetUseruname() {
        return userpref.getString(Constants.USERNAME, null);
    }

    public String GetUserpass() {
        return userpref.getString(Constants.PASSWORD, null);
    }

    public String GetUserSname() {
        return userpref.getString(Constants.SHORTNAME, null);
    }

    public Integer ChatAllowed() {
        return userpref.getInt(Constants.CHAT, 0);
    }

    public Integer CreditAllowed() {
        return userpref.getInt(Constants.CREDIT, 0);
    }

    public String GetUserpic() {
        return userpref.getString(Constants.PROFILEPIC, null);
    }

    public String GetUserBalance() {
        return userpref.getString(Constants.BALANCE, "");
    }

    public String GetUsertype() {
        return userpref.getString(Constants.USERTYPE, null);
    }

    public String GetUserStreet() {
        return userpref.getString(Constants.STREET, null);
    }

    public String GetUserBuilding() {
        return userpref.getString(Constants.BUILDING, null);
    }

    public String GetUserFloor() {
        return userpref.getString(Constants.FLOOR, null);
    }

    public String GetUserApartment() {
        return userpref.getString(Constants.APARTMENT, null);
    }

    public String GetUserAdditional() {
        return userpref.getString(Constants.ADDITIONAL, null);
    }

    public String GetUserEmail() {
        return userpref.getString(Constants.EMAIL, null);
    }

    public String GetUserPhone() {
        return userpref.getString(Constants.USERPHONE, null);
    }

    public void SetUserlogged(boolean value) {
        useredit.putBoolean(Constants.IS_LOGGED_IN, value);
        useredit.apply();
    }

    public void SetAuthMethod(String value) {
        useredit.putString(Constants.AUTHMETHOD, value);
        useredit.apply();
    }

    public void SetUserid(int value) {
        useredit.putInt(Constants.USERID, value);
        useredit.apply();
    }

    public void AllowChat(int value) {
        useredit.putInt(Constants.CHAT, value);
        useredit.apply();
    }

    public void SetUsername(String value) {
        useredit.putString(Constants.NAME, value);
        useredit.apply();
    }

    public void SetUserSname(String value) {
        useredit.putString(Constants.SHORTNAME, value);
        useredit.apply();
    }

    public void SetUseremail(String value) {
        useredit.putString(Constants.EMAIL, value);
        useredit.apply();
    }

    public void SetUseruname(String value) {
        useredit.putString(Constants.USERNAME, value);
        useredit.apply();
    }

    public void SetUserpass(String value) {
        useredit.putString(Constants.PASSWORD, value);
        useredit.apply();
    }

    public void SetUserpic(String value) {
        useredit.putString(Constants.PROFILEPIC, value);
        useredit.apply();
    }

    public void SetUserbalance(String value) {
        useredit.putString(Constants.BALANCE, value);
        useredit.apply();
    }

    public void SetUsertype(String value) {
        useredit.putString(Constants.USERTYPE, value);
        useredit.apply();
    }

    public void SetUserStreet(String value) {
        useredit.putString(Constants.STREET, value);
        useredit.apply();
    }

    public void SetUserBuilding(String value) {
        useredit.putString(Constants.BUILDING, value);
        useredit.apply();
    }

    public void SetUserFLoor(String value) {
        useredit.putString(Constants.FLOOR, value);
        useredit.apply();
    }

    public void SetUserApartment(String value) {
        useredit.putString(Constants.APARTMENT, value);
        useredit.apply();
    }

    public void SetUserAdditional(String value) {
        useredit.putString(Constants.ADDITIONAL, value);
        useredit.apply();
    }

    public void AllowCredit(int value) {
        useredit.putInt(Constants.CREDIT, value);
        useredit.apply();
    }

    public void SetUserphone(String value) {
        useredit.putString(Constants.USERPHONE, value);
        useredit.apply();
    }

    public void clearUserPref() {
        userpref.edit().clear().apply();
    }

    /* Settings Preference */

    public int Mapsinterval() {
        return settingspref.getInt("Mapsinterval", 5);
    }

    public void setMapsinterval(int value) {
        settingsedit.putInt("Mapsinterval", value);
        settingsedit.apply();
    }

    public boolean PushNotifications() {
        return settingspref.getBoolean(Constants.PUSH_NTF, true);
    }

    public void clearSettingsPref() {
        settingspref.edit().clear().apply();
    }

    /* Gps Tracking Preference */

    public boolean iscurrentlyTracking() {
        return gpspref.getBoolean("currentlyTracking", false);
    }

    public void setcurrentlyTracking(boolean value) {
        gpsedit.putBoolean("currentlyTracking", value);
        gpsedit.apply();
    }

    public boolean isfirstTimeGettingPosition() {
        return gpspref.getBoolean("firstTimeGettingPosition", true);
    }

    public void firstTimeGettingPosition(boolean value) {
        gpsedit.putBoolean("firstTimeGettingPosition", value);
        gpsedit.apply();
    }

    public int Trackinginterval() {
        return gpspref.getInt("Trackinginterval", 10);
    }

    public void setTrackinginterval(int value) {
        gpsedit.putInt("Trackinginterval", value);
        gpsedit.apply();
    }

    public Float getTotalDistance() {
        return gpspref.getFloat("totalDistanceInMeters", 0f);
    }

    public void setTotalDistance(float value) {
        gpsedit.putFloat("totalDistanceInMeters", value);
        gpsedit.apply();
    }

    public Float getpreviousLatitude() {
        return gpspref.getFloat("previousLatitude", 0f);
    }

    public void setpreviousLatitude(float value) {
        gpsedit.putFloat("previousLatitude", value);
        gpsedit.apply();
    }

    public Float getpreviousLongitude() {
        return gpspref.getFloat("previousLongitude", 0f);
    }

    public void setpreviousLongitude(float value) {
        gpsedit.putFloat("previousLongitude", value);
        gpsedit.apply();
    }

    public void clearGpsPref() {
        gpspref.edit().clear().apply();
    }

    /* Notifications Preference */

    public int LastFCMID() {
        return notfpref.getInt("lastfcm", 0);
    }

    public boolean PushServices() {
        return notfpref.getBoolean("pushenabled", true);
    }

    public void setPushServices(boolean value) {
        notfedit.putBoolean("pushenabled", value);
        notfedit.apply();
    }

    public int Pushinterval() {
        return notfpref.getInt("Pushinterval", 5);
    }

    public void SetLastFCMID(int id) {
        notfedit.putInt("lastfcm", id);
        notfedit.apply();
    }

    public void setPushinterval(int value) {
        notfedit.putInt("Pushinterval", value);
        notfedit.apply();
    }

    public int GetNotfCount() {
        return notfpref.getInt(Constants.NOTF_COUNT, 0);
    }

    public void SetNotfcount(int value) {
        notfedit.putInt(Constants.NOTF_COUNT, value);
        notfedit.apply();
    }

    public void clearNotfPref() {
        notfpref.edit().clear().apply();
    }
}