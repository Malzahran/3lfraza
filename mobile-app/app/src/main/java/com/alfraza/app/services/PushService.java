package com.alfraza.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alfraza.app.ActivityOrderHistory;
import com.alfraza.app.ActivityRecycler;
import com.alfraza.app.R;
import com.alfraza.app.activities.misc.ActivityNotifications;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.notifications.MyNotificationManger;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.NotificationData;

import retrofit2.Call;
import retrofit2.Callback;

public class PushService extends Service {

    private Session session;
    private Handler hPush;
    private Runnable rPush;
    private MyNotificationManger mNotificationManager;
    private Intent ntfIntent, OrdersIntent;
    private Integer ntfInterval = 5;
    private boolean gettingNtf = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NotificationCreator.getNotificationId(),
                    NotificationCreator.getNotification(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null && intent.getAction().contains("start")) {
            session = new Session(getApplicationContext());
            //creating MyNotificationManager object
            mNotificationManager = new MyNotificationManger(getApplicationContext());
            //creating an intent for the notification
            if (session.pref().GetUsertype().equals(Constants.USER)) {
                OrdersIntent = new Intent(getApplicationContext(), ActivityOrderHistory.class);
            } else OrdersIntent = new Intent(getApplicationContext(), ActivityRecycler.class);
            Bundle intentBundle = new Bundle();
            IntentData ordersIntent = new IntentData(15, 0, 0, 0, 0, "orders", "find_orders", null, null);
            intentBundle.putParcelable("intentData", ordersIntent);
            OrdersIntent.putExtras(intentBundle);
            OrdersIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            ntfIntent = new Intent(getApplicationContext(), ActivityNotifications.class);
            Bundle bundle = new Bundle();
            bundle.putString("pushnotf", "yes");
            ntfIntent.putExtras(bundle);
            ntfIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ntfInterval = session.pref().Pushinterval();
            hPush = new Handler();
            rPush = new Runnable() {
                @Override
                public void run() {
                    if (!gettingNtf)
                        fn_getnotifications();
                    hPush.postDelayed(this, ntfInterval * 1000);
                }
            };
            hPush.postDelayed(rPush, ntfInterval * 1000);
        } else {
            if (hPush != null) hPush.removeCallbacks(rPush);
            stopSelf();
        }
        return START_STICKY;
    }


    private void fn_getnotifications() {
        gettingNtf = true;
        MiscData misc = new MiscData();
        misc.setType(Constants.ALL_TYPE);
        misc.setLastid(session.pref().LastFCMID());
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.NOTIFICATIONS);
        request.setSubType("pushservice");
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        Call<ServerResponse> response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                gettingNtf = false;
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    if (resp.getMessage() != null && !resp.getMessage().isEmpty()) {
                                        mNotificationManager.showSmallNotification(getApplicationContext().getString(R.string.new_notification_alert),resp.getMessage(), OrdersIntent, 1299);
                                    }
                                    NotificationData notf = resp.getNotification();
                                    if (notf != null) {
                                        if (notf.getCount() != session.pref().GetNotfCount()) {
                                            session.pref().SetNotfcount(notf.getCount());
                                            if (session.pref().PushNotifications())
                                                if (notf.getCount() > 0)
                                                    if (notf.getAction() != null)
                                                        mNotificationManager.showSmallNotification(notf.getTitle(), notf.getAction(), ntfIntent, 1245);


                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                session.pref().clearNotfPref();
                                session.pref().clearUserPref();
                                session.pref().clearGpsPref();
                                session.pref().clearSettingsPref();
                                stopSelf();
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                gettingNtf = false;
            }
        });
    }
}