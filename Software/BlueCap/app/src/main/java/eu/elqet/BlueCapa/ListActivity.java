package eu.elqet.BlueCapa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Matej Baran on 14.3.2016.
 */
public class ListActivity extends Activity {

    private ListView listView;
    private AppDB appDB;
    private DeviceListAdapter deviceListAdapter = null;
    private Button stopBackButton;
    private Context context;
    private boolean showOnly = false;
    private final int VOID_CODE = 0;
    private final int REFRESH_REQUEST_CODE = 1;
    private final int REFRESH_SERVICE_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.listViewDevices);
        BluetoothLowEnergy.getInstance().setListActivity(this);
        context = this;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                appDB = new AppDB(ListActivity.this);
                deviceListAdapter = new DeviceListAdapter(ListActivity.this, appDB.getAllBeacons());
                listView.setAdapter(deviceListAdapter);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, DeviceActivity.class);
                intent.putExtra("id", String.valueOf(id));
                startActivityForResult(intent, REFRESH_REQUEST_CODE);

            }
        });
        stopBackButton = (Button) findViewById(R.id.buttonStopBack);
        stopBackButton.setTag(1);
        showOnly = false;
        if (getIntent().getExtras().getString("case").equals("show")) {
            stopBackButton.setText(R.string.stringButtonGoBack);
            stopBackButton.setTag(0);
            showOnly = true;
        }
        stopBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonStopBack: {
                        final int status = (Integer) v.getTag();
                        if (status == 1) {
                            BluetoothLowEnergy.getInstance().stopScanLeDevice();
                            stopBackButton.setText(R.string.stringButtonGoBack);
                            v.setTag(0);
                        } else {
                            if (!showOnly) {
                                setResult(REFRESH_SERVICE_CODE);
                            } else {
                                setResult(VOID_CODE);
                            }
                            finish();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REFRESH_REQUEST_CODE) {
            refreshList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (showOnly) {
            setResult(VOID_CODE);
            finish();
        } else if (BluetoothLowEnergy.getInstance().isScanning()) {
            BluetoothLowEnergy.getInstance().stopScanLeDevice();
        }
        setResult(REFRESH_SERVICE_CODE);
        finish();
    }

    public void refreshList() {
        if (deviceListAdapter != null) {
            deviceListAdapter.changeCursor(appDB.getAllBeacons());
            deviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        appDB.close();
        super.onDestroy();
    }
}
