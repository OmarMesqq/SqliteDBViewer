package com.orpheusdroid.sqliteviewer.service;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orpheusdroid.sqliteviewer.R;

import ly.count.android.sdk.messaging.CountlyPush;

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

        if (message != null && message.has("typ")) {
            // custom handling only for messages with specific "typ" keys
            message.recordAction(getApplicationContext());
            return;
        }

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com"));

        Boolean result = CountlyPush.displayMessage(getApplicationContext(), message, R.drawable.ic_notification_icon, notificationIntent);
        if (result == null) {
            Log.i(TAG, "Message wasn't sent from Countly server, so it cannot be handled by Countly SDK");
        } else if (result) {
            Log.i(TAG, "Message was handled by Countly SDK");
        } else {
            Log.i(TAG, "Message wasn't handled by Countly SDK because API level is too low for Notification support or because currentActivity is null (not enough lifecycle method calls)");
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
