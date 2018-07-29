package com.orpheusdroid.sqliteviewer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orpheusdroid.sqliteviewer.BuildConfig;
import com.orpheusdroid.sqliteviewer.R;
import com.orpheusdroid.sqliteviewer.ui.MainActivity;

import ly.count.android.sdk.messaging.CountlyPush;

import static ly.count.android.sdk.messaging.CountlyPush.CHANNEL_ID;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class CountlyFirebaseInstanceIdService extends FirebaseMessagingService {

    private static final String TAG = "DemoMessagingService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("DemoInstanceIdService", "got new token: " + token);

        CountlyPush.onTokenRefresh(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("DemoFirebaseService", "got new message: " + remoteMessage.getData());

        // decode message data and extract meaningful information from it: title, body, badge, etc.
        CountlyPush.Message message = CountlyPush.decodeMessage(remoteMessage.getData());

        if (message.has("appid")) {
            int appId = Integer.parseInt(message.data("appid"));
            if (appId > BuildConfig.VERSION_CODE)
                showAppUpdateNotification(message);
            return;
        }

        if (message != null && message.has("typ")) {
            // custom handling only for messages with specific "typ" keys
            message.recordAction(getApplicationContext());
            return;
        }

        Intent notificationIntent = null;
        if (message.has("another")) {
            notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        }

        Boolean result = CountlyPush.displayMessage(getApplicationContext(), message, R.drawable.ic_notification_icon, notificationIntent);
        if (result == null) {
            Log.i(TAG, "Message wasn't sent from Countly server, so it cannot be handled by Countly SDK");
        } else if (result) {
            Log.i(TAG, "Message was handled by Countly SDK");
        } else {
            Log.i(TAG, "Message wasn't handled by Countly SDK because API level is too low for Notification support or because currentActivity is null (not enough lifecycle method calls)");
        }
    }

    private void showAppUpdateNotification(CountlyPush.Message message) {

        Intent playIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.orpheusdroid.sqliteviewer"));
        PendingIntent playPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, playIntent, 0);
        Notification.Builder notification = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                new Notification.Builder(getApplicationContext(), CHANNEL_ID) :
                new Notification.Builder(getApplicationContext().getApplicationContext()))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setTicker(message.message())
                .setContentTitle(message.title())
                .setContentText(message.message())
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(playPendingIntent)
                .setStyle(new Notification.BigTextStyle().bigText(message.message()).setBigContentTitle(message.title()));
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(message.hashCode(), notification.build());

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
