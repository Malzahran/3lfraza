package com.alfraza.app.helpers.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.alfraza.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class ImageViewNotification extends AsyncTask<String, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private Context mCtx;
    private String title, message, imageUrl;
    private Intent intent;

    ImageViewNotification(Context context, String title, String message, String imageUrl, Intent intent) {
        super();
        this.mCtx = context;
        this.intent = intent;
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        InputStream in;
        try {
            URL url = new URL(this.imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            in = connection.getInputStream();
            return BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 100, intent, PendingIntent.FLAG_ONE_SHOT);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_02";
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription(title);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) notificationManager.createNotificationChannel(notificationChannel);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mCtx, NOTIFICATION_CHANNEL_ID);
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                .bigPicture(result)
                .setSummaryText(title);
        Notification notification;
        notification = notificationBuilder
                .setContentIntent(pendingIntent)
                .setContentTitle(mCtx.getResources().getString(R.string.app_name) + " | " + title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notf_icon)
                .setTicker(mCtx.getResources().getString(R.string.app_name) + " - " + title)
                .setSound(defaultSoundUri)
                .setLargeIcon(result)
                .setStyle(style)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (notificationManager != null) {
            notificationManager.cancel(MyNotificationManger.ID_BIG_NOTIFICATION);
            notificationManager.notify(MyNotificationManger.ID_BIG_NOTIFICATION, notification);
        }
    }
}