package com.lihao.lisa.view;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lihao.lisa.R;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener{
    private Preference pref_vr_engine;
    private Preference pref_tts_engine;
    private String mAsrEngine;
    private String mTTSEngine;
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.setting_preference);

        pref_vr_engine=(Preference) findPreference("VR_Engine");
        pref_vr_engine.setOnPreferenceClickListener(this);
        bindPreferenceSummaryToValue(findPreference("VR_Engine"));

        pref_tts_engine=(Preference) findPreference("TTS_Engine");
        pref_tts_engine.setOnPreferenceClickListener(this);
        bindPreferenceSummaryToValue(findPreference("TTS_Engine"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return true;
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.d(TAG, "onPreferenceChange: stringValue: " + stringValue);
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

                String key = preference.getKey();
                Log.d(TAG, "onPreferenceChange: key:" + key + " stringValue: " + stringValue);

                if(key.equals("TTS_Engine")){
                    mTTSEngine = stringValue;
                }else if(key.equals("VR_Engine")){
                    mAsrEngine = stringValue;
                }else{
                    Log.e(TAG, "onPreferenceChange: No match setting");
                }

                Intent intent = getIntent();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("TTS_Engine", mTTSEngine);
                    jsonObject.put("VR_Engine", mAsrEngine);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("Setting", jsonObject.toString());
                setResult(RESULT_OK, intent);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}