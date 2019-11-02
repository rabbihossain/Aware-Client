package io.a_ware.a_ware;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * Created by Fredrik Persson on 2017-10-04.
 */

public class LocationService extends Service {

    private String TAG = "LocationService.java";
    private static Location currentBestLocation;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /*public LocationService() {
        super("LocationService");
    }*/

    //TODO check that gps is activated on phone, if not ask user to activate

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "LocationService starting", Toast.LENGTH_SHORT).show(); //This happens!
        return super.onStartCommand(intent,flags,startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"LocationService started");
        getCoarseLocation ();
        getFineLocation();
    }

    private void  getCoarseLocation (){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG,"In coarseLocation");
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d(TAG,"In CoarseLocation on locationChanged");
                Log.d(TAG, " CoarseLocation: "+ "Altitude: " + location.getAltitude() + " Latitude: "
                        + location.getLatitude() + " Longitude: " + location.getLongitude());
                if(isBetterLocation(location, currentBestLocation)){
                    currentBestLocation = location;
                    Log.d(TAG, "New best current location found");
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException e){
            Log.e(TAG, "Error in getCoareLocation permisssion revoked" + e);
        }catch(Exception e){
            Log.e(TAG, "Error in getCoareLocation" + e);
        }
    }

    private void  getFineLocation (){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG,"In FineLocation");
        // Define a listener that responds to location updates
        LocationListener locationListenerGps = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d(TAG,"In Location on location changed");
                Log.d(TAG, "FineLocation " + "Altitude: " + location.getAltitude() + " Latitude: "
                        + location.getLatitude() + " Longitude: " + location.getLongitude());
                if(isBetterLocation(location, currentBestLocation)){
                    currentBestLocation = location;
                    Log.d(TAG, "New best current location found");
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {Log.d(TAG, "onStatusChanged " + provider);}

            public void onProviderEnabled(String provider) {Log.d(TAG, "onProviderEnabled");}

            public void onProviderDisabled(String provider) {Log.d(TAG, "onProviderDisabled");}
        };

        // Register the listener with the Location Manager to receive location updates
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        }catch(SecurityException e){
            Log.e(TAG, "Error in fineLocation permisssion revoked" + e);
        }catch(Exception e){
            Log.e(TAG, "Error in getFineLocation" + e);
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            Log.d(TAG, "No current location, so the new one is better");
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            Log.d(TAG, "The newely found location is newer, so save that one instead");
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            Log.d(TAG, "The new location is a GPS fix thats older than the current one, dont save it");
            return false;
        }
        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            Log.d(TAG, "The new gps fix is more accurate");
            return true;
        } else if (isNewer && isMoreAccurate) {
            Log.d(TAG, "The new GPS fix is newer and more accurate");
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate) {
            Log.d(TAG, "The GPS fix is newer and not significantly worse");
            return true;
        }
        return false;
    }

    public static Location getLocation(){
        if(currentBestLocation != null) {
            Log.d("LocationService.java", "in getLocation, return: " + currentBestLocation.toString());
        }
        return currentBestLocation;
    }

}
