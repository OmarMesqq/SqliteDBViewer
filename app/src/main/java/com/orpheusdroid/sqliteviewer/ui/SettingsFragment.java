package com.orpheusdroid.sqliteviewer.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

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
        }
    }
}
