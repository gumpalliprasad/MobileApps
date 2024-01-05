/*
 * *
 *  * Created by SriRamaMurthy A on 22/9/19 4:25 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 22/9/19 4:24 PM
 *
 */

package myschoolapp.com.gsnedutech.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.SplashActivity;


public class FireMsgService extends FirebaseMessagingService {

    private static final String TAG = "SriRam -" + FireMsgService.class.getName();

    NotificationManager notificationManager;

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.v("TOKEN", mToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.v(TAG, "Message data payload: " + remoteMessage.getData());
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1001");
            Intent ii = new Intent(this, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            mBuilder.setContentTitle(remoteMessage.getData().get("title"));
            mBuilder.setContentText(remoteMessage.getData().get("body"));
            mBuilder.setPriority(Notification.PRIORITY_MAX);

            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "1001";
                NotificationChannel channel = new NotificationChannel(channelId, "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }

            notificationManager.notify(0, mBuilder.build());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}