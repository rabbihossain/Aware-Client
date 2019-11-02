package io.a_ware.a_ware;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.orm.query.Condition;
import com.orm.query.Select;


import java.util.List;


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

        List<Applog> totalLog = Select.from(Applog.class).where(Condition.prop("synced").eq(1)).list();

        Toast.makeText(this, totalLog.size() + " removable items found", Toast.LENGTH_SHORT).show();

        for (int i=0; i < totalLog.size(); i++){
            Applog singleItem = totalLog.get(i);
            singleItem.delete();
        }

        Toast.makeText(this, "All synced items deleted", Toast.LENGTH_SHORT).show();


    }
}
