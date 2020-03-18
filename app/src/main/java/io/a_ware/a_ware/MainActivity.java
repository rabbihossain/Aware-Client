package io.a_ware.a_ware;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int repeatTimeCeckPermission;
    private int repeatTimeSendData;
    private int repeatTimeClearData;
    private String TAG = "MainActivity.java";
    private final int PermissionRequestStorageANDPhoneState = 1;
    private Boolean FineLocationPermsission = false;
    private final int FineLocationPermissionRequest = 3;
    private final int CoarseLocationPermissionRequest = 2;
    private final int AllPermissionGranted = 4;
    private Boolean CoarseLocationPermsission = false;
    private SharedPreferences sharedPref;
    private String serverName;
    private int port ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        repeatTimeSendData = Integer.parseInt(sharedPref.getString(this.getString(R.string.pref_sync_frequency_key), this.getString(R.string.pref_sync_frequency_defaultValue)));
        repeatTimeClearData = Integer.parseInt(sharedPref.getString((this.getString(R.string.pref_delete_db_frequency_key)), this.getString(R.string.pref_delete_db_frequency_defaultValue)));
        repeatTimeCeckPermission = Integer.parseInt(sharedPref.getString((this.getString(R.string.pref_check_permission_key)), this.getString(R.string.pref_check_permission_defaultValue)));
        final SharedPreferences preferences = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        TinyDB tinydb = new TinyDB(getApplicationContext());
        JSONArray loggedArray = new JSONArray();

        if(preferences.getString("firstRun", "true") == "true"){
            editor.putString("firstRun", "false");
            editor.apply();
            JSONObject loggedObj = new JSONObject();

            try {
                loggedObj.put(getResources().getString(R.string.PhoneId), "init");
                loggedObj.put(getResources().getString(R.string.Package), "init");
                loggedObj.put(getResources().getString(R.string.Permission), "init");
                loggedObj.put(getResources().getString(R.string.Timestamp), "init");
                loggedObj.put(getResources().getString(R.string.GPS), "init");
                loggedObj.put(getResources().getString(R.string.Synced), "1");
                loggedArray.put(loggedObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tinydb.putString("TotalLog", loggedArray.toString());
        }

        final ArrayList<String> appNames = new ArrayList<String>();

        final PackageManager pm = getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null &&
                    !pm.getLaunchIntentForPackage(packageInfo.packageName).equals("")) {
                appNames.add(packageInfo.packageName);
            }
        }

        tinydb.putListString("AwareAppList", appNames);
        Log.d("AppList", tinydb.getListString("AwareAppList").toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Aware";
            String description = "Aware default notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("aware", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        //TODO add delay between starts so toas wont overlapp and to reduce computing power needed
        requestPermissions();
        startLocationService();
        startLoggerService();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "In on RequestsPermissionsResult");
        // If request is cancelled, the result arrays are empty.
        Log.d(TAG, "RequestCode: " + requestCode + " permission: " + permissions.toString() + " grantresult: " + grantResults.toString());

        if(grantResults.length > 0 && requestCode == AllPermissionGranted
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "All permission granted start services");
            startLocationService();
            startLoggerService();
            startDeleteSyncedDataService();
        }
        else{
            Log.e(TAG, "The user denied write external storage usage or request cancelled");
            Toast.makeText(this, "The app wont work properly since you denied write external storage permisssion", Toast.LENGTH_LONG).show();
        }
    }


    public void imClicked(View view) {
        Intent intent = new Intent(this, permActivity.class);
        startActivity(intent);
    }

    private void startLocationService(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (isMyServiceRunning(LocationService.class)) {
                Log.d(TAG, "LocationService Allready started.");
            }
            else if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "LocationService not started, do it now.");
                Log.d(TAG, "Permissions granted for both coarse and fine location" + ", start the service to gather gps fix");
                Intent intentToSend = new Intent(this, LocationService.class);
                this.startService(intentToSend);
            }
            else{
                Log.d(TAG, "Dont have permissions to start locationService");
            }
        }
        else{
            Log.d(TAG, "Permission grante on install time start locationservice");
            Intent intentToSend = new Intent(this, LocationService.class);
            this.startService(intentToSend);
        }
    }

    private void startSendDataService(){
        if(isMyServiceRunning(SendDataService.class) && checkIfAlarmIsSet("SendDataService.class")){
            Log.d(TAG, "SendDataService Allready started.");
        }
        else{
            Log.d(TAG, "SendDataService not started, do it now.");
            Intent logintent = new Intent(this, SendDataService.class);
            logintent.putExtra(this.getString(R.string.startSendDataServiceClicked_key), false);
            startService(logintent);
            PendingIntent pintent2 = PendingIntent.getService(this, 0, logintent, 0);
            AlarmManager alarm2 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarm2.cancel(pintent2);
            alarm2.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), repeatTimeSendData, pintent2);
        }
    }

    private boolean checkIfAlarmIsSet(String startedClass){
        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                new Intent(startedClass),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Log.d(TAG, "Alarm is already active for " + startedClass);
            return true;
        }
        else{
            Log.d(TAG, "Alarm not set for " + startedClass);
            return false;
        }
    }


    private void startDeleteSyncedDataService(){
        if(isMyServiceRunning(deleteSyncedDataService.class) && checkIfAlarmIsSet("deleteSyncedDataService.class")){
            Log.d(TAG, "deleteSyncedDataService Allready started.");
        }
        else {
            Log.d(TAG, "startDeleteSyncedDataService, repeatTimeClearData " + repeatTimeClearData);
            Intent logintent = new Intent(this, deleteSyncedDataService.class);
            Log.d(TAG, "Starting deleteSyncedDataService.java");
            startService(logintent);
            PendingIntent pintent2 = PendingIntent.getService(this, 0, logintent, 0);
            AlarmManager alarm2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm2.cancel(pintent2);
            alarm2.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), repeatTimeClearData, pintent2);
        }
    }

    private void requestPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v("reqperm","Permission is granted write external storage, read phone state and both coarse and fine location");
            }
            else {
                Log.v("reqperm","Permission is revoked for all");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION}, AllPermissionGranted);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("reqperm","Permission is already granted");
            startLocationService();
            startLoggerService();
        }
    }

    private void startLoggerService(){
        if(isMyServiceRunning(loggerService.class) && checkIfAlarmIsSet("loggerService.class")){
            Log.d(TAG, "LoggerService Allready started.");
        }
        else {
            Intent logintent = new Intent(this, loggerService.class);
            Log.d(TAG, "Starting loggerService");
            startService(logintent);
            PendingIntent pintent2 = PendingIntent.getService(this, 0, logintent, 0);
            AlarmManager alarm2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm2.cancel(pintent2);
            alarm2.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), repeatTimeCeckPermission, pintent2);
        }
    }

    public void mockClicked(View view) {
        Intent mocklocintent = new Intent(this, MockLocation.class);
        startActivity(mocklocintent);
    }

    public void rppClicked(View view) {
        Intent rppIntent = new Intent(this, RPP_Main.class);
        startActivity(rppIntent);
    }

    public void SettingsClicked(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        Log.d(TAG, "starting settings activity");
        startActivity(settingsIntent );
    }

    public void ShowStatisticsClicked(View view) {
        Log.d("MainActivity", "In ShowStatistics clicked");
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        port = Integer.parseInt(sharedPref.getString(this.getString(R.string.pref_port_number_key), this.getString(R.string.pref_port_number_defaultValue)));
        serverName = sharedPref.getString(this.getString(R.string.pref_ip_key), this.getString(R.string.pref_default_ip));

        String url = "http://" + serverName + ":" + port + "/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    public void logClicked(View view) throws IOException, JSONException {
        Intent logOpenIntent = new Intent(this, logDataActivity.class);
        Log.d(TAG, "starting log activity");
        startActivity(logOpenIntent);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
