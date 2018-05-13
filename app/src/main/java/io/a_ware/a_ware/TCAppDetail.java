package io.a_ware.a_ware;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class TCAppDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcapp_detail);

        Bundle bundle = getIntent().getExtras();
        String appPackageName = bundle.getString("pkgname");


        final ArrayList<String> itemname =new ArrayList<String>();

        ListView listView = (ListView) findViewById(R.id.TCAppDetailContainer);

        CustomAppDetailView adapter=new CustomAppDetailView(this, itemname);
        listView.setAdapter(adapter);

        final PackageManager pm = getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        try {
            PackageInfo packageInfo = pm.getPackageInfo(appPackageName, PackageManager.GET_PERMISSIONS);

            //Get Permissions
            String[] requestedPermissions = packageInfo.requestedPermissions;

            if(requestedPermissions != null) {
                for (int i = 0; i < requestedPermissions.length; i++) {

                    Log.d("test", requestedPermissions[i]);

                    //itemname.add(requestedPermissions[i]);

                    String[] last = requestedPermissions[i].toString().split("\\.");

                    String lastOne = last[last.length - 1];

                    if (!lastOne.contains("_")) {
                        itemname.add(lastOne.toLowerCase());
                    } else {
                        String[] permissions = lastOne.split("_");
                        StringBuffer sb = new StringBuffer();
                        for (int index = 0; index < permissions.length; index++) {
                            sb.append(permissions[index].toLowerCase());
                            sb.append(" ");
                        }
                        itemname.add(sb.toString());
                    }

                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}
