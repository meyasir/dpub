package com.darewro.Utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.darewro.Activities.AppBaseActivity;
import com.darewro.Fragments.HomeFragment;
import com.darewro.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Jaffar on 11/15/2017.
 */

/*
* edited by yasir on 19-may-18
*
* When message is recieved.
*
* */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    MySharedPreferences mySharedPreferences;

    private static final String TAG = "MyFirebaseMsgService";
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        int status;
        String title, message;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            status = Integer.parseInt(remoteMessage.getData().get("notification_type").toString());
            title = remoteMessage.getData().get("title").toString();
            message = remoteMessage.getData().get("message").toString();
            mySharedPreferences = new MySharedPreferences();
            boolean isRegister = mySharedPreferences.isRegister(getApplicationContext());
            if(isRegister)
                    sendNotificationOrder(title,message);
            }


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param title FCM message body received.
     * @param message FCM message body received.
     */
    private void sendNotificationOrder(String title,String message) {
        Intent i = new Intent(this, AppBaseActivity.class);
        i.putExtra("Uniqid","From_Notification");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, i,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_noti_colored : R.drawable.ic_noti;
    }
}
