package io.a_ware.a_ware;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

public class logDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_data);


        final TextView totalItems = (TextView) findViewById(R.id.totalDBItem);
        final TextView syncedItem = (TextView) findViewById(R.id.totalSyncedItem);
        final TextView nonSyncedItem = (TextView) findViewById(R.id.totalNonSyncedItem);

        long totalCount = Applog.count(Applog.class, null, null);
        long syncedCount = Applog.count(Applog.class, "synced = ?",  new String[]{"1"});
        long nonSyncedCount = Applog.count(Applog.class, "synced = ?",  new String[]{"0"});

        totalItems.setText("Total DB Items - " + totalCount);
        syncedItem.setText("Total Synced Items - " + syncedCount);
        nonSyncedItem.setText("Total Non-synced Items - " + nonSyncedCount);

        final Handler handler =new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 30000);
                long totalCount = Applog.count(Applog.class, null, null);
                long syncedCount = Applog.count(Applog.class, "synced = ?",  new String[]{"1"});
                long nonSyncedCount = Applog.count(Applog.class, "synced = ?",  new String[]{"0"});

                totalItems.setText("Total DB Items - " + totalCount);
                syncedItem.setText("Total Synced Items - " + syncedCount);
                nonSyncedItem.setText("Total Non-synced Items - " + nonSyncedCount);
            }
        };
        handler.postDelayed(r, 0000);

    }

    public void startSendDataService(View view){
        if(isMyServiceRunning(SendDataService.class)){
            Log.d("logDataActivity.class", "SendDataService Allready started.");
            Toast.makeText(this, "SendDataService is all-ready running.", Toast.LENGTH_LONG).show();

        }
        else {
            Log.d("logDataActivity.class", "SendDataService not started, do it now.");
            Toast.makeText(this, "starting SendDataService now. Please wait...", Toast.LENGTH_LONG).show();

            Intent sendIntent = new Intent(this, SendDataService.class);
            sendIntent.putExtra(this.getString(R.string.startSendDataServiceClicked_key), false);
            startService(sendIntent);
        }
    }

    public void startDeleteSyncedDataService(View view){
        if(isMyServiceRunning(deleteSyncedDataService.class)){
            Log.d("logDataActivity.class", "deleteSyncedDataService Allready started.");
            Toast.makeText(this, "deleteSyncedDataService is all-ready running.", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(this, "starting deleteSyncedDataService now. Please wait...", Toast.LENGTH_LONG).show();
            Log.d("logDataActivity.class", "Starting deleteSyncedDataService.java");
            Intent deleteIntent = new Intent(this, deleteSyncedDataService.class);
            startService(deleteIntent);
        }
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
