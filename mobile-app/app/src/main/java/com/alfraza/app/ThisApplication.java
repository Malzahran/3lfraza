package com.alfraza.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ThisApplication extends Application {

    public static final String NOTIFICATION_CHANNEL_ID = "NotificationChannel";
    private static ThisApplication mInstance;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        mInstance = this;
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    // override the base context of application to update default locale for the application
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Connection Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription(getString(R.string.app_name) + " is kept running in background to ensure you receive newly push notifications.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static synchronized ThisApplication getInstance() {
        return mInstance;
    }

    public void saveLogEvent(long id, String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void saveCustomLogEvent(String event, String key, String value) {
        Bundle params = new Bundle();
        params.putString(key, value);
        mFirebaseAnalytics.logEvent(event, params);
    }
}
