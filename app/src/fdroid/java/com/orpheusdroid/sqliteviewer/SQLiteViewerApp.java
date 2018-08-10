package com.orpheusdroid.sqliteviewer;

import android.preference.PreferenceManager;
import android.util.Log;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.DeviceId;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class SQLiteViewerApp extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setupAnalytics(){
        Countly.sharedInstance()
                .setRequiresConsent(true)
                .setLoggingEnabled(true)
                .setHttpPostForced(true)
                .enableParameterTamperingProtection(getPackageName())
                .setViewTracking(true)
                .enableCrashReporting();

        String[] groupFeatures = new String[]{ Countly.CountlyFeatureNames.sessions
                ,Countly.CountlyFeatureNames.users, Countly.CountlyFeatureNames.events};
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
        Log.d(Const.TAG, "Countly setup");
    }
}
