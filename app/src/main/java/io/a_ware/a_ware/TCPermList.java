package io.a_ware.a_ware;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCPermList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcperm_list);

        final ArrayList<String> itemname =new ArrayList<String>();
        final ArrayList<String> itemdetail = new ArrayList<String>();

        String [] permNamesDetail = new String[] {"CAMERA", "CALL_PHONE", "ACCESS_COARSE_LOCATION","ACCESS_FINE_LOCATION", "ACCESS_NETWORK_STATE","ACCESS_WIFI_STATE","READ_SMS","SEND_SMS","READ_CONTACTS","INSTALL_PACKAGES"};
        String [] permNamesReadable = new String[] {"Camera", "Call Phone", "Access Coarse Location","Access Precise Location","Get Network Info", "Get Wifi Info", "Read SMS", "Send SMS", "Read Contacts", "Install Applications"};

        itemname.addAll(Arrays.asList(permNamesReadable));
        itemdetail.addAll(Arrays.asList(permNamesDetail));

        ListView listView = (ListView) findViewById(R.id.TCPermListContainer);

        CustomArrayListAdapterForPerm adapter=new CustomArrayListAdapterForPerm(this, itemname, itemdetail);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String Selecteditem1 = itemdetail.get(+position);
                String Selecteditem2 = itemname.get(+position);
                Intent intent = new Intent(TCPermList.this, TCPermDetail.class);
                intent.putExtra("permIds", Selecteditem1);
                intent.putExtra("permName", Selecteditem2);
                startActivity(intent);

            }
        });

    }
}
