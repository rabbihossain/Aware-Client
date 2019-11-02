package io.a_ware.a_ware;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RPPSetPass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rppset_pass);

        final SharedPreferences preferences = getSharedPreferences("RPP_STORAGE", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        final EditText password1 = (EditText) findViewById(R.id.RPPPassInput);
        final EditText password2 = (EditText) findViewById(R.id.RPPPassInputAgain);
        Button saveButton = (Button) findViewById(R.id.RPPPassSave);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!password1.getText().toString().equals("")){

                    if(!password2.getText().toString().equals("")){

                        if(password1.getText().toString().equals(password2.getText().toString())){

                            editor.putString("rppPassword", password1.getText().toString());
                            editor.putString("rppHasPassword", "true");
                            editor.apply();

                            Intent rppIntent = new Intent(getApplicationContext(), RPP_Main.class);
                            startActivity(rppIntent);

                        } else {
                            Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "2nd password field is blank", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "1st password field is blank", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
