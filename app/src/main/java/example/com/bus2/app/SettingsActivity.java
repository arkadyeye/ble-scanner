package example.com.bus2.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import example.com.bus2.R;

/**
 * Created by g_arkady on 13/06/19.
 */


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            initPreferences();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

            Preference namePreference = findPreference("name");
            namePreference.setSummary(preferences.getString("name", "na"));
        }

        @Override
        public void onPause() {
            super.onPause();
            //unregister the preference change listener
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);

            if (preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }

            if (preference instanceof EditTextPreference){
                preference.setSummary(sharedPreferences.getString(key, ""));
            }

            if (preference instanceof SwitchPreference){
                ((SwitchPreference) preference).setChecked(sharedPreferences.getBoolean(key, false));
            }
        }


        public void initPreferences() {

            Preference button_show_privacy_policy = findPreference("show_privacy_policy");
            button_show_privacy_policy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showLicenseAgreement();

                    return true;
                }
            });
        }

        public void showLicenseAgreement() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(R.string.license_title)
                    .setMessage(R.string.license_content)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //sharedPreferences.edit().putBoolean("research_participant", true).apply();

                        }
                    });
//                    .setNegativeButton(R.string.license_reject, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            //do nothing and exit
//                            finish();
//                        }
//                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }





}
