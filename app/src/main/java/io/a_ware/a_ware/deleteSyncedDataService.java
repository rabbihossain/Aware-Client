package io.a_ware.a_ware;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Fredrik Persson on 2017-10-18.
 */

public class deleteSyncedDataService extends IntentService {
    private static String TAG = "deleteSyncedDataService";
    private Context mContext;

    public deleteSyncedDataService(String name) {
        super(name);
    }

    public deleteSyncedDataService(){ super("deleteSyncedDataService");}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "In create deteSyncedDataService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "In background thread");
        TinyDB tinydb = new TinyDB(getApplicationContext());
        String totalLog = tinydb.getString("TotalLog");
        Log.d(TAG, "Entire db: " + totalLog);
        JSONArray loggedArray = null;
        int counter = 0;
        try{
            loggedArray = new JSONArray(totalLog);
            //Log.d(TAG, "db lengt: " + loggedArray.length());
            if (Build.VERSION.SDK_INT >= 19){
                Log.d(TAG, "Build version > 18 support for built in remove function");
                for(int i = 0; i < loggedArray.length(); i++){
                    if(isEntrySynced(loggedArray.getJSONObject(i))){
                        loggedArray.remove(i);
                        counter++;
                        i = 0;
                    }
                }
                //Toast.makeText(mContext, "Number of synced entries removed: " + counter, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Number of synced entries removed: " + counter);
            }
            else{
                Log.d(TAG, "build version < 19 remove not supported");
                for(int i=0; i < loggedArray.length(); i++){
                    if(isEntrySynced(loggedArray.getJSONObject(i))){
                        loggedArray.put(i, new JSONObject()); //TODO Test this no ide if it works
                        counter++;
                        i = 0;
                    }
                }
                //Toast.makeText(mContext, counter + " synced entries was deleted.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Number of synced entries removed: " + counter);
            }
            tinydb.putString("TotalLog", loggedArray.toString()); //save the updated loggedArray to db
            /*Log.d(TAG, tinydb.toString());
            Log.d(TAG, "db lengt after: " + loggedArray.length());
            Log.d(TAG, "Entire db after: " + tinydb.getString("TotalLog"));*/
        }catch(Exception e){
            Log.e(TAG, "Error in deleting synced db: " + e);
        }

    }


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
}
