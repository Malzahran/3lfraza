package com.alfraza.app.helpers.fcm;

import android.content.Intent;

import com.alfraza.app.ActivityStore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.alfraza.app.helpers.notifications.MyNotificationManger;
import com.alfraza.app.helpers.session.Session;

import org.json.JSONObject;

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        sendTheRegisteredTokenToWebServer(s);
    }

    private void sendTheRegisteredTokenToWebServer(String token) {
        Session session = new Session(getApplicationContext());
        String oldToken = session.pref().GetToken();
        if (oldToken != null) session.pref().saveOldToken(oldToken);
        session.pref().saveNewToken(token);
        session.pref().saveNotificationSubscription(true);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");
            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");
            String ntype = data.getString("ntype");
            String naction = data.getString("naction");
            int ntfID = data.getInt("nid");
            Session session = new Session(getApplicationContext());
            //creating MyNotificationManager object
            MyNotificationManger mNotificationManager = new MyNotificationManger(getApplicationContext());
            session.pref().SetLastFCMID(ntfID);
            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(), ActivityStore.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (!ntype.isEmpty()) {
                intent.putExtra("NotiClick", true);
                intent.putExtra("ntype", ntype);
                intent.putExtra("naction", naction);
                intent.putExtra("nmessage", message);
            }
            //if there is no image
            if (!imageUrl.isEmpty()) {
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            } else {
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent, 1025);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}