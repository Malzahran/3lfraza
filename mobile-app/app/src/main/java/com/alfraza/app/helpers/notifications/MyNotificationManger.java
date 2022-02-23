package com.alfraza.app.helpers.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.alfraza.app.R;
import com.alfraza.app.helpers.utilities.ITUtilities;

public class MyNotificationManger {

    static final int ID_BIG_NOTIFICATION = 234;

    private Context mCtx;

    public MyNotificationManger(Context mCtx) {
        this.mCtx = mCtx;
    }

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification(String title, String message, String url, Intent intent) {
        new ImageViewNotification(mCtx, title, message, url, intent).execute();
    }

    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showSmallNotification(String title, String message, Intent intent, int code) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        code,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Push Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription(title);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mCtx, NOTIFICATION_CHANNEL_ID);
        Notification notification;
        notification = notificationBuilder
                .setSmallIcon(R.drawable.ic_notf_icon)
                .setTicker(mCtx.getResources().getString(R.string.app_name) + " | " + title)
                .setLargeIcon(ITUtilities.drawableToBitmap(ContextCompat.getDrawable(mCtx, R.mipmap.ic_launcher)))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(mCtx.getResources().getString(R.string.app_name) + " | " + title)
                .setColor(ContextCompat.getColor(mCtx, R.color.colorPrimary))
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setSound(defaultSoundUri)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        if (notificationManager != null) {
            notificationManager.cancel(code);
            notificationManager.notify(code, notification);
        }
    }
}