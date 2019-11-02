package io.a_ware.a_ware;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TCPermDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcperm_detail);

        Bundle bundle = getIntent().getExtras();
        final String permCodename = bundle.getString("permIds");
        String permName = bundle.getString("permName");

        TextView textview1 = (TextView) findViewById (R.id.permDetailName);
        TextView textview2 = (TextView) findViewById (R.id.permShortName);
        final TextView seekBarValue = (TextView) findViewById (R.id.seekBarValue);

        textview1.setText(permName);
        textview2.setText(permCodename);

        final SharedPreferences preferences = getSharedPreferences("PERMISSION_VALUES_STORAGE", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        seekBarValue.setText("" + preferences.getInt(permCodename, 0));
        final SeekBar sk=(SeekBar) findViewById(R.id.seekBar);

        sk.setProgress(preferences.getInt(permCodename, 0));

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText("" + progress);
                editor.putInt(permCodename, progress);
                editor.apply();
            }
        });


    }
}
