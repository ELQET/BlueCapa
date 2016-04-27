package eu.elqet.BlueCapa;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.widget.Toast.*;

/**
 * Created by Matej Baran on 2.3.2016.
 */
public class MainActivity extends AppCompatActivity {

    private BluetoothLowEnergy bluetoothLowEnergy;
    private DeviceCardAdapterCard deviceCardAdapter = null;
    private RecyclerView recList;
    private MainActivity mainActivity;
    private Context context;
    private ToggleButton buttonListen;
    private ImageButton buttonDelete;
    private boolean status = false;
    private static boolean serviceRun = false;
    private AppDB appDB;
    private BroadcastReceiver receiver;
    private BroadcastReceiver receiverCardList;
    private boolean fromOptionsMenu = false;
    private String longPressMac = "";
    private int holderWidth = 0;
    private int holderHeight = 0;
    private EulaDialog eulaDialog;
    private final int VOID_CODE = 0;
    private final int SELECT_IMAGE_CODE = 1;
    private final int REFRESH_SERVICE_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eulaDialog = new EulaDialog(this);
        eulaDialog.show(false);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            makeText(this, R.string.stringBleNotSupported, LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this.getApplicationContext();
        bluetoothLowEnergy = BluetoothLowEnergy.getInstance();
        bluetoothLowEnergy.InitBLE(this);
        appDB = new AppDB(this);

        //for DEBUG purpose
        //appDB.dropTableSettings();
        //appDB.createTableSettings();
        //appDB.dropTableBeacons();
        //appDB.createTableBeacons();
        //appDB.dropTableTimeLog();
        //appDB.createTableTimeLog();

        appDB.readSettings();
        appDB.close();
        Utils.InitUtils();

        buttonDelete = (ImageButton) findViewById(R.id.buttonActionDelete);
        buttonListen = (ToggleButton) findViewById(R.id.buttonActionScan);
        buttonListen.setChecked(status);
        if (Settings.isServiceAlive()) {
            if (!isServiceRun()) {
                status = true;
                startServiceNow();
            } else {
                buttonListen.setChecked(true);
            }
        }

        buttonListen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                status = buttonListen.isChecked();
                if (!status) {
                    if (Settings.isNotify()) {
                        Toast.makeText(mainActivity, R.string.stringStopListenToBeacons, Toast.LENGTH_SHORT).show();

                    }
                    stopServiceNow(true);
                } else {
                    if (Settings.isNotify()) {
                        Toast.makeText(mainActivity, R.string.stringStartListenToBeacons, Toast.LENGTH_SHORT).show();
                    }
                    startServiceNow();
                }
            }
        });

        buttonDelete.setOnLongClickListener(new ImageButton.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
                dialogBuilder.setMessage(R.string.stringDeleteAllYesNo).setPositiveButton(R.string.stringYes, dialogClickListener)
                        .setNegativeButton(R.string.stringNo, dialogClickListener).show();
                fromOptionsMenu = false;
                return false;
            }
        });

        buttonDelete.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                appDB.updateAllBeaconsIgnored();
                refreshCards();
                if (Settings.isNotify()) {
                    Toast.makeText(mainActivity, R.string.stringDeleteSwipeIgnore, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mainActivity = this;
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AppDB appDB = new AppDB(MainActivity.this);
                deviceCardAdapter = new DeviceCardAdapterCard(appDB, appDB.getAllBeacons(), mainActivity);
                recList.setAdapter(deviceCardAdapter);
                ItemTouchHelper.Callback callback = new CardItemTouchHelperCallback(deviceCardAdapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(recList);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("BLUE_CAP_DATA_CHANGED");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mac = intent.getExtras().getString("mac");
                if (intent.getExtras().getBoolean("new")) {
                    String state = intent.getExtras().getString("state");
                    int count = appDB.getCountOfBeacons();
                    DeviceData deviceData = new DeviceData(mac, "Device " + String.valueOf(count + 1), state, (long) System.currentTimeMillis());
                    appDB.insertBeacon(deviceData);
                }
                refreshCards();
            }
        };
        registerReceiver(receiver, filter);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("CARD_LONG_PRESS_ON_IMAGE");
        receiverCardList = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                longPressMac = intent.getExtras().getString("mac");
                holderWidth = intent.getExtras().getInt("holderWidth");
                holderHeight = intent.getExtras().getInt("holderHeight");
                Intent intentForGallery = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                mainActivity.startActivityForResult(Intent.createChooser(intentForGallery, getString(R.string.stringSelectPicture)), SELECT_IMAGE_CODE);
            }
        };
        registerReceiver(receiverCardList, filter2);
    }

    private void startServiceNow() {
        context.startService(new Intent(context, ListenService.class));
        if (!buttonListen.isChecked()) buttonListen.setChecked(true);
        setServiceRun(true);
    }

    private void stopServiceNow(boolean dbWrite) {
        context.stopService(new Intent(context, ListenService.class));
        setServiceRun(false);
        if (dbWrite) {
            Settings.setServiceAlive(false);
            AppDB appDB = new AppDB(context);
            appDB.updateSettingsServiceAlive(false);
            appDB.updateSettingsVideoRecording(false);
            appDB.close();
            if (buttonListen.isChecked()) buttonListen.setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //click on settings
        if (id == R.id.action_settings) {
            Intent i = new Intent(mainActivity, SettingsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.startActivity(i);
            return true;
        }

        //click on show
        if (id == R.id.action_show) {
            Intent i = new Intent(mainActivity, ListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.putExtra("case", "show");
            mainActivity.startActivityForResult(i, VOID_CODE);
            return true;
        }

        //click on show Eula
        if (id == R.id.action_showEula) {
            eulaDialog.show(true);
            return true;
        }

        //click on scan add new
        if (id == R.id.action_scan) {
            Intent i = new Intent(mainActivity, ListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.putExtra("case", "scan");
            if (isServiceRun()) {
                stopServiceNow(false);
            }
            if (!bluetoothLowEnergy.isScanning()) {
                bluetoothLowEnergy.scanLeDevice();
            } else {
                bluetoothLowEnergy.stopScanLeDevice();
                bluetoothLowEnergy.scanLeDevice();
            }
            mainActivity.startActivityForResult(i, REFRESH_SERVICE_CODE);
            return true;
        }

        //click on clean and scan
        if (id == R.id.action_clean_scan) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.stringDeleteAllYesNo).setPositiveButton(R.string.stringYes, dialogClickListener)
                    .setNegativeButton(R.string.stringNo, dialogClickListener).show();
            fromOptionsMenu = true;
        }

        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    appDB.dropTableBeacons();
                    appDB.createTableBeacons();
                    appDB.dropTableTimeLog();
                    appDB.createTableTimeLog();
                    if (fromOptionsMenu) {
                        Intent i = new Intent(mainActivity, ListActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra("case", "scan");
                        if (isServiceRun()) {
                            stopServiceNow(false);
                        }
                        if (!bluetoothLowEnergy.isScanning()) {
                            bluetoothLowEnergy.scanLeDevice();
                        } else {
                            bluetoothLowEnergy.stopScanLeDevice();
                            bluetoothLowEnergy.scanLeDevice();
                        }
                        mainActivity.startActivityForResult(i, REFRESH_SERVICE_CODE);
                    } else {
                        buttonDelete.setBackgroundResource(R.mipmap.ic_action_action_delete_off);
                        refreshCards();
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REFRESH_SERVICE_CODE:
                if (Settings.isServiceAlive()) {
                    startServiceNow();
                }
                break;
            case SELECT_IMAGE_CODE:
                if (resultCode == mainActivity.RESULT_OK) {
                    if (data != null) {
                        try {
                            Uri selectedImage = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            try {
                                cursor.moveToFirst();
                            } catch(Exception e) {
                                Log.e(getClass().getName(), "onActivityResult cursor error: " + e.toString());
                            }
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String filePath = cursor.getString(columnIndex);
                            cursor.close();
                            appDB.updateBeaconThumbnail(longPressMac, holderWidth, holderHeight, filePath);
                        } catch (Exception e) {
                            Log.e(getClass().getName(), "onActivityResult error: " + e.toString());
                            e.printStackTrace();
                        }

                    } else if (resultCode == mainActivity.RESULT_CANCELED) {
                        Toast.makeText(mainActivity, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        refreshCards();
    }

    @Override
    public void onDestroy() {
        appDB.close();
        unregisterReceiver(receiver);
        unregisterReceiver(receiverCardList);
        super.onDestroy();
    }

    private void refreshCards() {
        if (deviceCardAdapter != null) {
            appDB = new AppDB(context);
            Cursor cursor = appDB.getAllBeacons();
            deviceCardAdapter.changeCursor(cursor);
            deviceCardAdapter.notifyDataSetChanged();
            if (cursor.getCount() > 0) {
                buttonDelete.setBackgroundResource(R.mipmap.ic_action_action_delete);
            } else {
                buttonDelete.setBackgroundResource(R.mipmap.ic_action_action_delete_off);
            }
        }
    }

    public static boolean isServiceRun() {
        return serviceRun;
    }

    public static void setServiceRun(boolean serviceState) {
        serviceRun = serviceState;
    }
}
