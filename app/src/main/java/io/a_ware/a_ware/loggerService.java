package io.a_ware.a_ware;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

import static io.a_ware.a_ware.LocationService.getLocation;
import static java.security.AccessController.getContext;


public class loggerService extends Service {

    public boolean suAvailable = false;
    public List<String> suResult = null;
    private String TAG = "loggerService.java";
    private String PhoneId;
    private static Date LastRunDate;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        Log.e(TAG, "Setting last run date");
        LastRunDate = new Date(System.currentTimeMillis());
        getPhoneId();
    }

    @Override
    public int onStartCommand(Intent intent, int startId, int flags) {

        HandlerThread handlerThread = new HandlerThread("HandlerThreadName");
        // Starts the background thread
        handlerThread.start();
        // Create a handler attached to the HandlerThread's Looper
        Handler mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Process received messages here!
            }
        };

        // Execute the specified code on the worker thread
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                TinyDB tinydb = new TinyDB(getApplicationContext());

                final ArrayList<String> appList = tinydb.getListString("AwareAppList");

                suAvailable = Shell.SU.available();

                if (suAvailable) {
                    Log.d("Su", "Found");
                }

                for (String appName : appList) {
                    Log.d(TAG, "Now Querying APP " + appName);
                    suResult = Shell.SU.run("appops get " + appName);

                    for (String line : suResult) {
                        if (line.contains("ago") && !(appName.contentEquals("io.a_ware.a_ware") || appName.contentEquals("eu.chainfire.supersu") || appName.contentEquals("me.phh.superuser"))) {
                            String permName = line.substring(0, line.indexOf(':'));
                            String timeString = line.substring(line.indexOf("+"), line.indexOf("ago"));
                            Date DateString = new Date(System.currentTimeMillis() - getMilliSecFromString(timeString.substring(0, timeString.length() - 1)));
                            /*Log.e(TAG, "Perm " + permName + " appname: " + appName);
                            Log.e(TAG, "TimeString: " + timeString + " in milisec:" + getMilliSecFromString(timeString.substring(0, timeString.length() - 1))
                                + " current: " + System.currentTimeMillis() + " datestring: " + DateString);*/
                            int syncBit = 0;
                            String GPS = "";
                            if (getLocation() != null) {
                                GPS = getLocation().getLatitude() + " | " + getLocation().getLongitude();
                                Log.d(TAG, "The permission contained in GPS is: " + GPS);
                            }

                            List<Applog> foundItems = Select.from(Applog.class)
                                    .where(
                                            Condition.prop("phoneid").eq(PhoneId),
                                            Condition.prop("packagen").eq(appName),
                                            Condition.prop("permission").eq(permName),
                                            Condition.prop("timestamp").eq(DateString),
                                            Condition.prop("gps").eq(GPS),
                                            Condition.prop("synced").eq(syncBit)
                                    )
                                    .list();
                            if (foundItems.size() <= 0) {
                                Applog newItem = new Applog(PhoneId, appName, permName, DateString, GPS, syncBit);
                                newItem.save();
                            }
                        }
                    }
                }
                LastRunDate = new Date(System.currentTimeMillis());
                Log.d(TAG, "LastRunDate: " + LastRunDate.toString());
            }
        });
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    //TODO check permission OK and alternativ solution
    private void getPhoneId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PhoneId = telephonyManager.getDeviceId();
        if (null == PhoneId || 0 == PhoneId.length()) {
            PhoneId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        else if(PhoneId == null){
            Log.e(TAG, "Neither imei id or android id availible" + " a default id choosen");
            PhoneId = "0123456789";
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putString(getString(R.string.pref_PhoneId_key), PhoneId).apply();
        Log.d(TAG, "phoneID: " + PhoneId + " phoneID in pref: " + sharedPref.getString(this.getString(R.string.pref_PhoneId_key), ""));

    }

    private long getMilliSecFromString(String value) {
        String[] temptokens = value.split("\\+|d|h|m|s|ms");
        Collections.reverse(Arrays.asList(temptokens));
        TimeModel timeModel = null;
        String[] tokens = Arrays.copyOf(temptokens, temptokens.length-1);
        if (tokens.length == 5) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .setHour(tokens[3])
                    .setDay(tokens[4])
                    .build();
        } else if (tokens.length == 4) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .setHour(tokens[3])
                    .build();
        } else if (tokens.length == 3) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .build();
        } else if (tokens.length == 2) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .build();
        } else {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .build();
        }
        return timeModel.getTotalTimeInMillisecond();
    }
}
