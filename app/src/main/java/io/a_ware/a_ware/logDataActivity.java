package io.a_ware.a_ware;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.w3c.dom.Text;

import java.util.List;

public class logDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_data);

        TextView totalItems = (TextView) findViewById(R.id.totalDBItem);
        TextView syncedItem = (TextView) findViewById(R.id.totalSyncedItem);
        TextView nonSyncedItem = (TextView) findViewById(R.id.totalNonSyncedItem);

        long totalCount = Applog.count(Applog.class, null, null);
        long syncedCount = Applog.count(Applog.class, "synced = ?",  new String[]{String.valueOf(1)});
        long nonSyncedCount = Applog.count(Applog.class, "synced = ?",  new String[]{String.valueOf(0)});

        totalItems.setText("Total DB Items - " + totalCount);
        syncedItem.setText("Total Synced Items - " + syncedCount);
        nonSyncedItem.setText("Total Non-synced Items - " + nonSyncedCount);

    }
}
