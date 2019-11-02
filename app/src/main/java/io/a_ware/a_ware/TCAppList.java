package io.a_ware.a_ware;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TCAppList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcapp_list);

        final ArrayList<String> itemname =new ArrayList<String>();
        final ArrayList<String> itemdetail = new ArrayList<String>();
        ArrayList<Drawable> imgid =new ArrayList<Drawable>();

        ListView listView = (ListView) findViewById(R.id.TCAppListContainer);

        CustomArrayListAdapter adapter=new CustomArrayListAdapter(this, itemname, itemdetail, imgid);
        listView.setAdapter(adapter);

        final PackageManager pm = getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null &&

                    !pm.getLaunchIntentForPackage(packageInfo.packageName).equals(""))


            {
                try {
                    ApplicationInfo app = this.getPackageManager().getApplicationInfo(packageInfo.packageName, 0);
                    Drawable icon = pm.getApplicationIcon(app);
                    String name = (String) pm.getApplicationLabel(app);
                    itemname.add(name);
                    imgid.add(icon);

                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("fuck", "no name found for this package");
                }

                itemdetail.add(packageInfo.packageName);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String Selecteditem = itemdetail.get(+position);

                Intent intent = new Intent(TCAppList.this, logger.class);
                intent.putExtra("pkgname", Selecteditem);
                startActivity(intent);
            }
        });
    }
}
