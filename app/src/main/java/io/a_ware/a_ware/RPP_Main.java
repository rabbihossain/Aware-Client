package io.a_ware.a_ware;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class RPP_Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpp__main);

        final SharedPreferences preferences = getSharedPreferences("RPP_STORAGE", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        final Switch ringOpt = (Switch) findViewById(R.id.ringOpt);
        final Switch locateOpt = (Switch) findViewById(R.id.locateOpt);

        String checkpass = preferences.getString("rppHasPassword", "");
        String checkring = preferences.getString("ringOpt", "");
        String checklocate = preferences.getString("locateOpt", "");

        if(checkring.equals("On")){
            ringOpt.setChecked(true);
        } else {
            ringOpt.setChecked(false);
        }

        if(checklocate.equals("On")){
            locateOpt.setChecked(true);
        } else {
            locateOpt.setChecked(false);
        }

        //Toast.makeText(this, checkpass, Toast.LENGTH_LONG).show();
        if(!checkpass.equals("true")){
            Intent rppIntent = new Intent(getApplicationContext(), RPPSetPass.class);
            startActivity(rppIntent);
        }

        ringOpt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    editor.putString("ringOpt", "On");
                    editor.apply();
                } else {
                    editor.putString("ringOpt","Off");
                    editor.apply();
                }
            }
        });

        locateOpt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    editor.putString("locateOpt", "On");
                    editor.apply();
                } else {
                    editor.putString("locateOpt", "Off");
                    editor.apply();
                }
            }
        });

    }
}
