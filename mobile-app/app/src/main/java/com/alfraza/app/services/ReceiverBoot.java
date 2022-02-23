package com.alfraza.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.alfraza.app.helpers.session.Session;

public class ReceiverBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Session session = new Session(context);
            if (session.pref().IsUserlogged()) {
                if (session.pref().PushServices()) {
                    Intent serviceIntent = new Intent(context, PushService.class);
                    serviceIntent.setAction("start");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        context.startForegroundService(serviceIntent);
                    else context.startService(serviceIntent);
                }
                if (session.pref().iscurrentlyTracking()) {
                    Intent trackerIntent = new Intent(context, GpsService.class);
                    trackerIntent.setAction("start");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        context.startForegroundService(trackerIntent);
                    else context.startService(trackerIntent);
                }
            }
        }
    }
}