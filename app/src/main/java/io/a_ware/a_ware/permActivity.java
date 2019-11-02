package io.a_ware.a_ware;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Objects;

public class permActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perm);

        final String[] itemname ={
                "Applications",
                "Permissions"
        };

        String[] itemdetail ={
                "View all installed applications",
                "View all system permissions"
        };


        Integer[] imgid ={
                R.drawable.application_shield,
                R.drawable.permissions_sheild,
        };

        ListView listView = (ListView) findViewById(R.id.TCListContainer);

        CustomListAdapter adapter=new CustomListAdapter(this, itemname, itemdetail, imgid);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem = itemname[+position];
                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

                if(Objects.equals(Slecteditem, "Applications")){
                    Intent intent = new Intent(permActivity.this, TCAppList.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(permActivity.this, TCPermList.class);
                    startActivity(intent);

                }
            }
        });

    }
}

//This Lines Maybe Needed For Future Reference
/*
        ListView listview1 = new ListView(this);
        ListView listview2 = new ListView(this);

        Vector<View> pages = new Vector<View>();

        pages.add(listview1);
        pages.add(listview2);

        ViewPager vp = (ViewPager) findViewById(R.id.viewPager);
        CustomPagerAdapter adapter = new CustomPagerAdapter(this,pages);
        vp.setAdapter(adapter);

        ArrayList<String> appList =new ArrayList<String>();

        listview1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,appList));
        listview2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,new String[]{"A2","B2","C2","D2"}));

        final PackageManager pm = getPackageManager();
        //get a list of installed apps.

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
        appList.add(packageInfo.packageName);
            Log.d("info", "Installed package :" + packageInfo.packageName);
            Log.d("info", "Source dir : " + packageInfo.sourceDir);
            Log.d("info", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
            Log.d("info", "");
} */


