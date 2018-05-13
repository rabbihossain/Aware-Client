package io.a_ware.a_ware;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class MockLocation extends AppCompatActivity {

    MockLocationProvider mock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_location);

        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        mock = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);

        final Switch nySwitch;
        final Switch dhakaSwitch;
        final Switch berlinSwitch;
        nySwitch = (Switch) findViewById(R.id.NYSwitch);
        berlinSwitch = (Switch) findViewById(R.id.BerlinSwitch);
        dhakaSwitch = (Switch) findViewById(R.id.DHKSwitch);

        dhakaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mock.pushLocation(23.8103, 90.4125);
                    nySwitch.setChecked(false);
                    berlinSwitch.setChecked(false);
                } else {
                    dhakaSwitch.setChecked(false);
                }
            }
        });

        nySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                mock.pushLocation(40.730610, -73.935242);
                dhakaSwitch.setChecked(false);
                berlinSwitch.setChecked(false);
                } else {
                    nySwitch.setChecked(false);
                }
            }
        });
        berlinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mock.pushLocation(52.5200, 13.4050);
                    nySwitch.setChecked(false);
                    dhakaSwitch.setChecked(false);

                } else {
                    berlinSwitch.setChecked(false);
                }

            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v("myapp", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.v("myapp", "status = %d");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.v("myapp", "provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.v("myapp", "provider disabled");
            }
        });

    }
}
