package io.a_ware.a_ware;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity {
    private static String TAG = "SettingsActivity.java";
    private static SharedPreferences sharedPref;
    private static Activity activity;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.d(TAG, "In sBindPreferenceSummaryToValueListener: " + stringValue);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                /*Log.d(TAG, "sync_frequency is now: " + sharedPref.getString(activity.getString(R.string.pref_sync_frequency_key), activity.getString(R.string.pref_default_ip)));
                Log.d(TAG, "Clear db frequency is now: " + sharedPref.getString(activity.getString(R.string.pref_delete_db_frequency_key), activity.getString(R.string.pref_delete_db_frequency_defaultValue)));
                Log.d(TAG, "check_permission frequency is now: " + sharedPref.getString(activity.getString(R.string.pref_check_permission_key), activity.getString(R.string.pref_check_permission_defaultValue)));
                */
                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                /*Log.d(TAG, "Ip is now: " + sharedPref.getString(activity.getString(R.string.pref_ip_key), activity.getString(R.string.pref_default_ip)));
                Log.d(TAG, "MobileDatasync is now: " + String.valueOf(sharedPref.getBoolean(activity.getString(R.string.pref_mobileSync_key), false)));
                Log.d(TAG, "Portnumber is now: " + sharedPref.getString(activity.getString(R.string.pref_port_number_key), activity.getString(R.string.pref_port_number_defaultValue)));
                Log.d(TAG, "Clear db frequency is now: " + sharedPref.getString(activity.getString(R.string.pref_delete_db_frequency_key), activity.getString(R.string.pref_delete_db_frequency_defaultValue)));*/
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's
        // current value.
        if(preference instanceof CheckBoxPreference){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        }
        else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Settings activity started ");
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || ResetDbOptionsPreferenceFragment.class.getName().equals(fragmentName)
                || checkPermissionPreferenceFragment.class.getName().equals(fragmentName)
                || ViewPhoneIdPreferenceFragment.class.getName().equals(fragmentName);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            Log.d(TAG, "Returning to mainActivity");
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "DataSyncPreferenceFragment opened");
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);
            activity = getActivity();
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String ip = sharedPref.getString(getActivity().getString(R.string.pref_ip_key), "");
            String sync = String.valueOf(sharedPref.getBoolean(getActivity().getString(R.string.pref_mobileSync_key), false));
            String frequency = sharedPref.getString(getActivity().getString(R.string.pref_sync_frequency_key), "");
            String portNumber = sharedPref.getString(getActivity().getString(R.string.pref_port_number_key), "");
            /*Log.d(TAG, "Test ip: " + ip);
            Log.d(TAG, "Test portnumber: " + portNumber);
            Log.d(TAG, "Test sync frequency: " + frequency);
            Log.d(TAG, "Test mobilesync: " + sync);
            */
            bindPreferenceSummaryToValue(findPreference(getActivity().getString(R.string.pref_sync_frequency_key)));
            bindPreferenceSummaryToValue(findPreference(getActivity().getString(R.string.pref_ip_key)));
            bindPreferenceSummaryToValue(findPreference(getActivity().getString(R.string.pref_port_number_key)));
            bindPreferenceSummaryToValue(findPreference(getActivity().getString(R.string.pref_mobileSync_key)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            Log.d(TAG, "onOptionsItemSelected DataSyncPreferenceFragment" + " item: " + id);
            if (id == android.R.id.home) {
                Log.d(TAG, "Returning to settingsActivity");
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }






    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ResetDbOptionsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "Pref_reset_db Fragment opened");
            addPreferencesFromResource(R.xml.pref_reset_db);
            setHasOptionsMenu(true);

            activity = getActivity();
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String deleteSyncedItemsIntervall = sharedPref.getString(getActivity().getString(R.string.pref_delete_db_frequency_key), getActivity().getString(R.string.pref_delete_db_frequency_defaultValue));

            //Log.d(TAG, "choosen intervall: " + deleteSyncedItemsIntervall);
            bindPreferenceSummaryToValue(findPreference(getActivity().getString(R.string.pref_delete_db_frequency_key)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            Log.d(TAG, "onOptionsItemSelected DataSyncPreferenceFragment" + " item: " + id);
            if (id == android.R.id.home) {
                Log.d(TAG, "Returning to settingsActivity");
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }




    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class checkPermissionPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "checkPermissionPreferenceFragment opened");
            addPreferencesFromResource(R.xml.pref_perm_freq);
            setHasOptionsMenu(true);
            activity = getActivity();
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String frequency = sharedPref.getString(getActivity().getString(R.string.pref_check_permission_key), getActivity().getString(R.string.pref_check_permission_defaultValue));

            //Log.d(TAG, "Test permission frequency: " + frequency);

            bindPreferenceSummaryToValue(findPreference(getActivity().getString(R.string.pref_check_permission_key)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            Log.d(TAG, "onOptionsItemSelected checkPermissionPreferenceFragment" + " item: " + id);
            if (id == android.R.id.home) {
                Log.d(TAG, "Returning to settingsActivity");
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


 /////////////////////////////
 @TargetApi(Build.VERSION_CODES.HONEYCOMB)
 public static class ViewPhoneIdPreferenceFragment extends PreferenceFragment {
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         Log.d(TAG, "ViewPhoneIdPreferenceFragment opened");
         addPreferencesFromResource(R.xml.pref_phoneid);
         setHasOptionsMenu(true);
         activity = getActivity();
         sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
         String PhoneId = sharedPref.getString(activity.getString(R.string.pref_PhoneId_key), "Not Found");

         Preference preference = (Preference) getPreferenceScreen().getPreference(0);
         preference.setSummary(PhoneId);

     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();
         Log.d(TAG, "onOptionsItemSelected ViewPhoneIdPreferenceFragment" + " item: " + id);
         if (id == android.R.id.home) {
             Log.d(TAG, "Returning to settingsActivity");
             startActivity(new Intent(getActivity(), SettingsActivity.class));
             return true;
         }
         return super.onOptionsItemSelected(item);
     }
 }



}
