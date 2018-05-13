package io.a_ware.a_ware;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TCServiceReceiver extends BroadcastReceiver {
    public TCServiceReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        context.startService(new Intent(context, loggerService.class));

        Intent ishintent = new Intent(context, loggerService.class);
        PendingIntent pintent = PendingIntent.getService(context, 0, ishintent, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 300000, pintent);
    }
}
