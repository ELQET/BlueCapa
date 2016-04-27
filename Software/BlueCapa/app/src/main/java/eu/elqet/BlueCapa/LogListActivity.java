package eu.elqet.BlueCapa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Matej Baran on 9.4.2016.
 */
public class LogListActivity extends Activity {

    private Context context;
    private ListView listView;
    private AppDB appDB;
    private LogListAdapter logListAdapter = null;
    private Button deleteButton;
    private String mac;
    private BroadcastReceiver receiver;
    private final int VOID_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loglist);
        listView = (ListView) findViewById(R.id.listLogView);
        mac = getIntent().getExtras().getString("mac");
        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText(getIntent().getExtras().getString("title"));
        context = this;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                appDB = new AppDB(LogListActivity.this);
                logListAdapter = new LogListAdapter(LogListActivity.this, appDB.getAllTimeLogsOfMac(mac));
                listView.setAdapter(logListAdapter);
                if (logListAdapter.getCount() == 0) {
                    deleteButton.setEnabled(false);
                } else {
                    deleteButton.setEnabled(true);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // on item click
            }
        });
        Button backButton = (Button) findViewById(R.id.buttonBack);
        deleteButton = (Button) findViewById(R.id.buttonDeleteLogs);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(VOID_CODE);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.stringLogsDeleteAllYesNo).setPositiveButton(R.string.stringYes, dialogClickListener)
                        .setNegativeButton(R.string.stringNo, dialogClickListener).show();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("BLUE_CAP_DATA_CHANGED");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshList();
            }
        };
        registerReceiver(receiver, filter);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    appDB.deleteLogsOfMac(mac);
                    refreshList();
                    deleteButton.setEnabled(false);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        setResult(VOID_CODE);
        finish();
    }

    public void refreshList() {
        if (logListAdapter != null) {
            logListAdapter.changeCursor(appDB.getAllTimeLogsOfMac(mac));
            logListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        appDB.close();
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
