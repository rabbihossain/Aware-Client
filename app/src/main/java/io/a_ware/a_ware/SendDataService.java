package io.a_ware.a_ware;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by persson on 2017-09-21.
 */

/**
 * This class is responsible for running in the background and sending the data to the server.
 * IntentService is not affected by user interface lifecycle and dont affect the user interface responsiveness.
 */
public class SendDataService extends IntentService {


    private String serverName;
    private int port ;
    private String TAG = "SendDataService.java";
    private boolean mobileNetworkAllowed;
    private static SharedPreferences sharedPref;

    public  SendDataService(){
        super("SendDataService");
    }
//TODO make sure the toas for starting baground service only show when button is pushed to start it, not when the alarm clock stats it
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started by wakeup lock on sync intervall");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        port = Integer.parseInt(sharedPref.getString(this.getString(R.string.pref_port_number_key), this.getString(R.string.pref_port_number_defaultValue)));
        serverName = sharedPref.getString(this.getString(R.string.pref_ip_key), this.getString(R.string.pref_default_ip));
        mobileNetworkAllowed = sharedPref.getBoolean(this.getString(R.string.pref_mobileSync_key), false);
        return super.onStartCommand(intent,flags,startId);
    }
    /**
     * The background threadthat does the work.
     * @param workIntent Intent to start thread with backgroynd work
     */
    @Override
    protected void onHandleIntent(Intent workIntent){
        Log.d(TAG, "In onHandleIntent");
        TinyDB tinydb = new TinyDB(getApplicationContext());

        List<Applog> totalLog = Select.from(Applog.class).where(Condition.prop("synced").eq(0)).limit("100").list();


        boolean run = true;
        if(isInternetAvailable()) {

            if (totalLog.size() > 0) {
                Log.d(TAG, "The data is not synced yet, do it now");
                try {
                    Log.d(TAG, "Connecting to: " + serverName + " on port " + port);

                    for (int i = 0; i < totalLog.size(); i++) {

                        final Applog object = totalLog.get(i);


                            RequestQueue queue = Volley.newRequestQueue(this);
                            String url = "http://" + serverName + ":" + port + "/add-data" ;
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>()
                                    {
                                        @TargetApi(Build.VERSION_CODES.KITKAT)
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            if (Objects.equals(response, "success")){
                                                object.synced = 1;
                                                object.save();

                                                Log.d("VolleyResponse", "Object stored successfully on server.");
                                            }
                                        }
                                    },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.d("Error.Response", String.valueOf(error));
                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams()
                                {
                                    Map<String, String> params = new HashMap<String, String>();

                                    params.put("PhoneID", object.phoneid);
                                    params.put("Package", object.packagen);
                                    params.put("Permission", object.permission);
                                    params.put("Timestamp", String.valueOf(object.timestamp));
                                    params.put("GPS", object.gps == ""? "N/A": object.gps);

                                    return params;
                                }
                            };
                            queue.add(postRequest);

                    }
                    Log.d(TAG, "The data was succefully sent to the server");
                } catch (Exception e) {
                    Log.e(TAG, "error in try with send/recieve data in onHandleEvent");
                    Log.e(TAG, e.toString());
                }
            } else {
                Log.d(TAG, "The data is allready synced");
            }
        }
    }

    /**
     * @return true if all data is synced else false
     */
    private boolean isAllSynced(JSONArray loggedArray){
        try {
            for (int i = 0; i < loggedArray.length(); i++) {
                if (!isEntrySynced(loggedArray.getJSONObject(i))) {
                    Log.d(TAG, "All data not synced");
                    return false;
                }
            }
        } catch(JSONException e){
            Log.d(TAG, "Error in isAllDataSynced" + e);
            return true;
        }
        Log.d(TAG, "All data is synced");
        return true;
    }

    /**
     * @param obj The object containing the permission entry
     * @return true if the object is synced else false
     */
    private boolean isEntrySynced(JSONObject obj){
        try{
            //Log.d(TAG, "Test isEntrySynced " + obj.get("Synced").toString());
            int syncBit = Integer.parseInt((obj.get("Synced").toString()));
            if(syncBit == 1){
                //Log.d(TAG, "isEntrySynced:" +"The entry was synced");
                return true;
            }
        } catch(JSONException e){
            Log.e(TAG, " error in isEntrySynced" + e);
        }
        return false;
    }



    private void printAllData(JSONArray loggedArray){
        try{
            for(int i=0; i<loggedArray.length(); i++) {
                Log.d("line:", i + " " + loggedArray.get(i).toString()); //print entire db to log
            }
        }catch(JSONException e){
            Log.e(TAG, "Error in printAllData," + e);
        }
    }

    private void setSynced(JSONObject obj){
        try{
            obj.put("Synced", 1);
        } catch(JSONException e){
            Log.e(TAG, "Error in setSynced" + e);
        }
        Log.d(TAG, "Test object synced: " + obj.toString());
    }

    private boolean isInternetAvailable(){ //TODO add settings page for choosing over which network the app is allowed to sync, now its both
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            Log.d(TAG, cm.toString());
            Log.d(TAG, cm.getActiveNetworkInfo().toString());
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "Connected through WiFi " + activeNetwork.getTypeName());
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE && mobileNetworkAllowed == false) { //TODO test this if
                Log.d(TAG, "Connected through mobile network " + activeNetwork.getTypeName() + " not allowed to sync on mobile data.");
                return false;
            }
            else{
                Log.d(TAG, "Connected through mobile network" + activeNetwork.getTypeName() + " and allowed to sync.");
            }
        } else {
            Log.d(TAG, "You are not connected to a network");
            return false;
        }
        return true;
    }

    private void debugPrint(){
        Log.d(TAG, "Ip is now: " + sharedPref.getString(this.getString(R.string.pref_ip_key), this.getString(R.string.pref_default_ip)));
        Log.d(TAG, "MobileDatasync is now: " + String.valueOf(sharedPref.getBoolean(this.getString(R.string.pref_mobileSync_key), false)));
        Log.d(TAG, "Portnumber is now: " + sharedPref.getString(this.getString(R.string.pref_port_number_key), this.getString(R.string.pref_port_number_defaultValue)));
        Log.d(TAG, "sync_frequency is now: " + sharedPref.getString(this.getString(R.string.pref_sync_frequency_key), this.getString(R.string.pref_sync_frequency_defaultValue)));
    }
}
