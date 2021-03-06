package io.a_ware.a_ware;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import eu.chainfire.libsuperuser.Shell;


public class logger extends AppCompatActivity {

    final ArrayList<String> itemname =new ArrayList<String>();
    final ArrayList<String> itemdetail = new ArrayList<String>();
    final ArrayList<Integer> itemcount = new ArrayList<Integer>();
    final ArrayList<Integer> itemcountsystem = new ArrayList<Integer>();

    final Activity activity = this;
    final String [] loggingKeywords = new String[] {"start","camera","location","gps","bluetooth","kill", "sms", "contact", "call", "wifi", "data"};
    final String [] loggingKeywordsExplained = new String[] {"App Launched", "Camera", "Geolocation", "Geolocation", "Bluetooth", "App Closed", "SMS", "Contacts", "Phone Call", "Wifi Access", "Data Service"};
    private String TAG = "logger.java";

    DatePickerDialog dateSelector;
    TextView dateText;
    ImageButton dateSelectButton;
    CustomArrayListAdapterForPermGraph adapter;

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

        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;

        private void listModifier(String appPackageName, String permName, String date){
            long permcount = Select.from(Applog.class)
                    .where(
                            Condition.prop("packagen").eq(appPackageName),
                            Condition.prop("permission").eq(permName),
                            Condition.prop("dateval").eq(date)
                    )
                    .count();
            long permcountsystem = Select.from(Applog.class)
                    .where(
                            Condition.prop("permission").eq(permName),
                            Condition.prop("dateval").eq(date)
                    )
                    .count();

            itemcount.add((int) permcount);
            itemcountsystem.add((int) permcountsystem);
        }

        @Override
        protected void onPreExecute() {
            // We're creating a progress dialog here because we want the user to wait.
            // If in your app your user can just continue on with clicking other things,
            // don't do the dialog thing.

            dialog = new ProgressDialog(context);
            dialog.setTitle("Please Wait");
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

            dateText = findViewById(R.id.dateSelect);
            dateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    dateSelector = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    dateText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                    itemcount.clear();
                                    itemcountsystem.clear();
                                    for(int i=0; i < itemname.size(); i++){
                                        String permName = itemname.get(i);
                                        listModifier(appPackageName, permName, dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    }
                                    adapter.setData(itemname, itemdetail, itemcount, itemcountsystem);
                                    adapter.notifyDataSetChanged();
                                }
                            }, year, month, day);
                    dateSelector.show();
                }
            });

            dateSelectButton = findViewById(R.id.dateSelectButton);
            dateSelectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    dateSelector = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    dateText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    itemcount.clear();
                                    itemcountsystem.clear();
                                    for(int i=0; i < itemname.size(); i++){
                                        String permName = itemname.get(i);
                                        listModifier(appPackageName, permName, dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    }
                                    adapter.setData(itemname, itemdetail, itemcount, itemcountsystem);
                                    adapter.notifyDataSetChanged();
                                }
                            }, year, month, day);
                    dateSelector.show();


                }
            });

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

                        final Calendar cldr = Calendar.getInstance();
                        int day = cldr.get(Calendar.DAY_OF_MONTH);
                        int month = cldr.get(Calendar.MONTH);
                        int year = cldr.get(Calendar.YEAR);

                        long permcount = Select.from(Applog.class)
                                .where(
                                        Condition.prop("packagen").eq(appPackageName),
                                        Condition.prop("permission").eq(line.substring(0, line.indexOf(':'))),
                                        Condition.prop("dateval").eq(day+"/"+(month + 1)+"/"+year)
                                )
                                .count();
                        long permcountsystem = Select.from(Applog.class)
                                .where(
                                        Condition.prop("permission").eq(line.substring(0, line.indexOf(':'))),
                                        Condition.prop("dateval").eq(day+"/"+(month + 1)+"/"+year)
                                )
                                .count();

                        itemcount.add((int) permcount);
                        itemcountsystem.add((int) permcountsystem);
                    }

                }
            }
            ListView listView = (ListView) findViewById(R.id.loggerList);
            adapter = new CustomArrayListAdapterForPermGraph(activity, itemname, itemdetail, itemcount, itemcountsystem);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    final String Selecteditem = itemname.get(+position);

                    try {
                        ai = pm.getApplicationInfo( appPackageName, 0);
                    } catch (final PackageManager.NameNotFoundException e) {
                        ai = null;
                    }
                    final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");


                    // setup the alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Enable / Disable Permissions");

                    // add a radio button list
                    final String[] choices = {"allow", "maybe", "deny"};
                    final int checkedItem = 1;
                    final int[] checkedItemFinal = {1};
                    builder.setSingleChoiceItems(choices, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // user checked an item
                            checkedItemFinal[0] = which;
                        }
                    });
                    // add OK and Cancel buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("logger.class", String.valueOf(checkedItemFinal[0]));
                            final String command = "appops set "+ appPackageName + " " + Selecteditem + " ";
                            if(checkedItemFinal[0] == 0 || checkedItemFinal[0] == 2) {
                                Shell.SU.run(new String[] {
                                        command + choices[checkedItemFinal[0]]
                                });
                            } else {
                                Shell.SU.run(new String[] {
                                        command + "allow"
                                });
                                String commandString = command + "deny";
                                Data d = new Data.Builder()
                                        .putString("command", commandString)
                                        .build();

                                Data notifyContent = new Data.Builder()
                                        .putString("notifycontent", Selecteditem+" permission will set to be denied for "+applicationName+" soon")
                                        .build();

                                OneTimeWorkRequest runCommand =
                                        new OneTimeWorkRequest.Builder(PermissionMaybe.class)
                                                .setInputData(d)
                                                .setInitialDelay(1440, TimeUnit.MINUTES)
                                                .build();

                                WorkManager.getInstance(context).enqueue(runCommand);

                                OneTimeWorkRequest runNotification =
                                        new OneTimeWorkRequest.Builder(NotifyMaybe.class)
                                                .setInputData(notifyContent)
                                                .setInitialDelay(1410, TimeUnit.MINUTES)
                                                .build();

                                WorkManager.getInstance(context).enqueue(runNotification);
                            }

                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
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
