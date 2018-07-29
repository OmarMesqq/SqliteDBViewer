package com.orpheusdroid.sqliteviewer.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.orpheusdroid.sqliteviewer.BuildConfig;
import com.orpheusdroid.sqliteviewer.R;

/**
 * Todo: Add class description here
 *
 * @author Vijai Chandra Prasad .R
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences prefs;
    private EditTextPreference rowCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        prefs = getPreferenceScreen().getSharedPreferences();

        rowCount = (EditTextPreference) findPreference(getString(R.string.preference_settings_table_row_count_key));

        SwitchPreference updateNotification = (SwitchPreference) findPreference(getString(R.string.countly_update_notification_key));
        if (BuildConfig.FLAVOR == "fdroid") {
            updateNotification.setSummary("Update notification not available on fdroid builds");
            updateNotification.setChecked(false);
            updateNotification.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    //Unregister for OnSharedPreferenceChangeListener when the fragment pauses
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private String getValue(String key, String defVal) {
        return prefs.getString(key, defVal);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference pref = findPreference(s);
        if (pref == null) return;
        switch (pref.getTitleRes()) {
            case R.string.preference_settings_table_row_count_title:
                break;
            case R.string.preference_export_charset_title:
                pref.setSummary(getValue(getString(R.string.preference_export_charset_key), "UTF-8"));
                break;
            case R.string.countly_basic_crash_reporting_title:

            case R.string.countly_anonymous_usage_stats_title:
                Toast.makeText(this.getActivity(), R.string.toast_message_analytics_app_restart_required_message, Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
