package io.a_ware.a_ware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class IncomingSms extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    LocationManager lm;
    LocationListener locationListener;


    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    final String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                    /*
                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);


                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: "+ senderNum + ", message: " + message, duration);
                    toast.show();
                    */

                    final SharedPreferences preferences = context.getSharedPreferences("RPP_STORAGE", Context.MODE_PRIVATE);
                    if (message.startsWith("AWARE RING " + preferences.getString("rppPassword",""))){
                        if(preferences.getString("ringOpt", "").equals("On")){
                            long ringDelay = 60000;
                            Uri notification = RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_RINGTONE);

                            final MediaPlayer player = MediaPlayer.create(context, notification);
                            player.setLooping(true);
                            player.start();

                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    player.stop();
                                }
                            };
                            Timer timer = new Timer();
                            timer.schedule(task, ringDelay);
                        }
                    }

                    if (message.startsWith("AWARE LOCATE " + preferences.getString("rppPassword",""))){
                        if(preferences.getString("locateOpt", "").equals("On")){

                            class MyLocationListener implements LocationListener {
                                @Override
                                public void onLocationChanged(Location loc) {
                                    if (loc != null) {
                                        //---send a SMS containing the current location---
                                        SmsManager sms = SmsManager.getDefault();
                                        sms.sendTextMessage(senderNum, null, "http://maps.google.com/maps?q=" + loc.getLatitude() + "," +
                                                loc.getLongitude(),	null, null);

                                        //---stop listening for location changes---
                                        lm.removeUpdates(locationListener);
                                    }
                                }

                                public void onProviderDisabled(String provider) {

                                }

                                public void onProviderEnabled(String provider) {

                                }

                                public void onStatusChanged(String provider, int status, Bundle extras) {

                                }
                            }

                            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                            //---request location updates---
                            locationListener = new MyLocationListener();
                            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                    60000,
                                    1000,
                                    locationListener);

                        }
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

}