package me.jiho.butterfly.settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import me.jiho.butterfly.R;

/**
 * Created by jiho on 2/13/15.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(
                getActivity(),
                R.xml.pref_notification,
                false
        );
        addPreferencesFromResource(R.xml.pref_notification);
    }
}
