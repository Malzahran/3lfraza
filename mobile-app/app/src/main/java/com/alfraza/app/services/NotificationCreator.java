package com.alfraza.app.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.alfraza.app.ActivitySplash;
import com.alfraza.app.R;
import com.alfraza.app.helpers.utilities.ITUtilities;

import static com.alfraza.app.ThisApplication.NOTIFICATION_CHANNEL_ID;

class NotificationCreator {

    private static final int NOTIFICATION_ID = 1094;

    private static Notification notification;

    static Notification getNotification(Context context) {

        if (notification == null) {
            Intent intent = new Intent(context, ActivitySplash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.app_still_running))
                    .setSubText(context.getString(R.string.app_still_running_desc))
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setColorized(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(ITUtilities.drawableToBitmap(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)))
                    .setContentIntent(pendingIntent)
                    .setShowWhen(false)
                    .setOngoing(true)
                    .build();

        }
        return notification;
    }

    static int getNotificationId() {
        return NOTIFICATION_ID;
    }
}