/*
 * Copyright (c) 2016-2018. Vijai Chandra Prasad R.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

package com.orpheusdroid.sqliteviewer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.solovyev.android.checkout.Billing;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.DeviceId;
import ly.count.android.sdk.messaging.CountlyPush;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class ScreenCamApp extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                // Create the NotificationChannel
                NotificationChannel channel = new NotificationChannel(CountlyPush.CHANNEL_ID, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(getString(R.string.notification_channel_description));
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.setShowBadge(true);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void setupAnalytics() {
        Countly.sharedInstance()
                .setRequiresConsent(true)
                .setPushIntentAddMetadata(true)
                .setLoggingEnabled(true)
                .setHttpPostForced(true)
                .enableParameterTamperingProtection(getPackageName())
                .setViewTracking(true)
                .enableCrashReporting();

        String[] groupFeatures = new String[]{Countly.CountlyFeatureNames.sessions
                , Countly.CountlyFeatureNames.users, Countly.CountlyFeatureNames.events
                , Countly.CountlyFeatureNames.starRating};
        Countly.sharedInstance().CreateFeatureGroup(Const.COUNTLY_USAGE_STATS_GROUP_NAME, groupFeatures);

        boolean isUsageStatsEnabled = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.countly_anonymous_usage_stats_key), false);
        Countly.sharedInstance().SetConsentFeatureGroup(Const.COUNTLY_USAGE_STATS_GROUP_NAME, isUsageStatsEnabled);

        boolean isCrashesEnabled = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.countly_anonymous_usage_stats_key), false);
        Countly.sharedInstance().setConsent(new String[]{Countly.CountlyFeatureNames.crashes}, isCrashesEnabled);

        Countly.sharedInstance().init(this, "https://analytics.orpheusdroid.com", "6d1957315cc874c5ca865fe8cebd403ec5a4065b", null, DeviceId.Type.OPEN_UDID);

        boolean isPushNotificationEnabled = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.countly_update_notification_key), true);
        Countly.sharedInstance().setConsent(new String[]{Countly.CountlyFeatureNames.push}, isPushNotificationEnabled);

        CountlyPush.init(this, Countly.CountlyMessagingMode.PRODUCTION);
        Log.d(Const.TAG, "Countly setup");
    }

    private static ScreenCamApp sInstance;

    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
        @Override
        public String getPublicKey() {
            return BuildConfig.APP_PUB_KEY;
        }
    });

    public ScreenCamApp() {
        sInstance = this;
    }

    public static ScreenCamApp get() {
        return sInstance;
    }

    public Billing getBilling() {
        return mBilling;
    }
}
