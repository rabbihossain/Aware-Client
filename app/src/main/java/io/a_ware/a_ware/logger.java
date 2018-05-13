package io.a_ware.a_ware;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

import static android.R.attr.value;

public class logger extends AppCompatActivity {

    final ArrayList<String> itemname =new ArrayList<String>();
    final ArrayList<String> itemdetail = new ArrayList<String>();
    final Activity activity = this;
    final String [] loggingKeywords = new String[] {"start","camera","location","gps","bluetooth","kill", "sms", "contact", "call", "wifi", "data"};
    final String [] loggingKeywordsExplained = new String[] {"App Launched", "Camera", "Geolocation", "Geolocation", "Bluetooth", "App Closed", "SMS", "Contacts", "Phone Call", "Wifi Access", "Data Service"};
    private String TAG = "logger.java";

    private class Startup extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private boolean suAvailable = false;
        private String suVersion = null;
        private String suVersionInternal = null;
        private List<String> suResult = null;



        Bundle bundle = getIntent().getExtras();
        String appPackageName = bundle.getString("pkgname");

        public Startup setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        protected void onPreExecute() {
            // We're creating a progress dialog here because we want the user to wait.
            // If in your app your user can just continue on with clicking other things,
            // don't do the dialog thing.

            dialog = new ProgressDialog(context);
            dialog.setTitle("Some title");
            dialog.setMessage("Doing something interesting ...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Let's do some SU stuff
            suAvailable = Shell.SU.available();
            if (suAvailable) {
                suVersion = Shell.SU.version(false);
                suVersionInternal = Shell.SU.version(true);
                suResult = Shell.SU.run(new String[] {
                        "appops get " + appPackageName
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            // output
            StringBuilder sb = new StringBuilder();
            if (suResult != null) {
                for (String line : suResult) {
                    sb.append(line).append((char)10);
                    sb.append(line).append(System.getProperty("line.separator"));
                    Log.d(TAG, "logcatmsg " +  line);

                    if(line.toLowerCase().contains("time")){
                        itemname.add(line.substring(0, line.indexOf(':')));

                        String timeString = line.substring(line.indexOf("+"), line.indexOf("ago"));
                        Date DateString = new Date(System.currentTimeMillis() - getMilliSecFromString(timeString.substring(0, timeString.length() - 1)));

                        itemdetail.add("Last Usage: " + String.valueOf(DateString));

                    }

                }
            }
            ListView listView = (ListView) findViewById(R.id.loggerList);

            CustomArrayListAdapterForPerm adapter=new CustomArrayListAdapterForPerm(activity, itemname, itemdetail);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);

       (new Startup()).setContext(this).execute();

    }

    private long getMilliSecFromString(String value) {

        String[] tokens = value.split("\\+|d|h|m|s|ms");
        Collections.reverse(Arrays.asList(tokens));
        TimeModel timeModel = null;
        if (tokens.length == 5) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .setHour(tokens[3])
                    .setDay(tokens[4])
                    .build();
        } else if (tokens.length == 4) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .setHour(tokens[3])
                    .build();
        } else if (tokens.length == 3) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .setMinute(tokens[2])
                    .build();
        } else if (tokens.length == 2) {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .setSecond(tokens[1])
                    .build();
        } else {
            timeModel = new TimeModel.TimeModelBuilder()
                    .setMilliSecond(tokens[0])
                    .build();
        }
        return timeModel.getTotalTimeInMillisecond();
    }
}
