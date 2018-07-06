package com.orpheusdroid.sqliteviewer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class SharedPreferenceManager {
    private static SharedPreferenceManager prefs;
    private static Context mContext;

    public static SharedPreferenceManager getInstance(Context context) {
        if (prefs == null) {
            prefs = new SharedPreferenceManager();
            mContext = context;
        }
        return prefs;
    }

    private SharedPreferences getSharedPreferences(String preference) {
        return mContext.getSharedPreferences(preference, Context.MODE_PRIVATE);
    }

    public String getString(String preference, String key, String defaultValue) {
        return getSharedPreferences(preference).getString(key, defaultValue);
    }

    public void setString(String preference, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(preference).edit();
        editor.putString(key, value);
        editor.apply();
    }

}
