package io.a_ware.a_ware;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

public class TCService extends Service {
    public TCService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int startId, int flags) {
        // Perform your long running operations here.

        final SharedPreferences permPreferences = getSharedPreferences("PERMISSION_VALUES_STORAGE", Context.MODE_PRIVATE);
        final SharedPreferences lastActiveWindowStorage = getSharedPreferences("LAST_ACTIVE_WINDOW_STORAGE", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = lastActiveWindowStorage.edit();

        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(l.get(0));
        try {
             CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                if(!info.processName.equals(lastActiveWindowStorage.getString("current_app", ""))){

                    PackageInfo packageInfo = pm.getPackageInfo(info.processName, PackageManager.GET_PERMISSIONS);
                    String[] reqPerms = packageInfo.requestedPermissions;
                    for (int k = 0; k < reqPerms.length; k++) {
                        String[] last = reqPerms[k].toString().split("\\.");

                        String lastOne = last[last.length - 1];

                        System.out.println(lastOne);
                        if(permPreferences.getInt(lastOne, 0) > 5){
                            System.out.println(reqPerms[k]);
                            Resources r = getResources();
                            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Notification notification = new NotificationCompat.Builder(this)
                                    .setTicker("A-WARE")
                                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                                    .setContentTitle("A-WARE")
                                    .setContentText(c.toString() + " is using some flagged permissions")
                                    .setAutoCancel(true)
                                    .setSound(soundUri)
                                    .build();
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(0, notification);

                            break;
                        }
                    }
                    editor.putString("current_app", info.processName);
                    editor.apply();
                }
                //Log.w("LABEL", c.toString());
        }catch(Exception e) {
                //Name Not FOund Exception
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
