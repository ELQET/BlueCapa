package eu.elqet.BlueCapa;

import android.app.Activity;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matej Baran on 3.3.2016.
 */
public class BluetoothLowEnergy {

    private static BluetoothLowEnergy instanceBluetoothLowEnergy = new BluetoothLowEnergy();
    private Activity mActivity;
    private ListActivity listActivity = null;
    private BeaconParser beaconParser;
    private boolean mScanning = false;
    private BluetoothLeScanner mBleScanner;
    private boolean initialized = false;

    private BluetoothLowEnergy() {

    }

    public boolean InitBLE(MainActivity activity) {
        if (!isInitialized()) {
            this.setActivity(activity);
            Intent i = new Intent(activity.getApplicationContext(), BluetoothInit.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(i);
            setInitialized(true);
            return true;
        } else {
            return false;
        }
    }

    public static BluetoothLowEnergy getInstance() {
        return instanceBluetoothLowEnergy;
    }

    public void stopScanLeDevice() {
        if (!isScanning()) return;
        setScanning(false);
        if (mBleScanner != null) mBleScanner.stopScan(mLeScanCallback);
    }

    public void scanLeDevice() {
        if (!isScanning()) {
            try {
                ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
                scanSettingsBuilder.setScanMode(Settings.getBleScanSettings());
                ScanSettings scanSettings = scanSettingsBuilder.build();
                List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
                mBleScanner.startScan(scanFilters, scanSettings, mLeScanCallback);
                setScanning(true);
                final Handler mHandler = new Handler();
                final boolean scanThread = mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBleScanner.stopScan(mLeScanCallback);
                    }
                }, Settings.getBleScanPeriod());
            } catch (Exception e) {
                Log.e(getClass().getName(), "scanLeDevice error: " + e.toString());
            }
        }
    }

    private ScanCallback mLeScanCallback =
            new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult scanResult) {
                    beaconParser = new BeaconParser(scanResult);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (beaconParser != null) {
                                String state = String.valueOf(beaconParser.getSignalStrength()) + mActivity.getString(R.string.stringDBM)
                                        + String.valueOf(Utils.roundVoltageDecimals(beaconParser.getBatteryStatus())) + mActivity.getString(R.string.stringVoltageComma)
                                        + String.valueOf(beaconParser.getSensor_data());
                                AppDB appDB2 = new AppDB(getActivity());
                                int count = appDB2.getCountOfBeacons();
                                DeviceData deviceData = new DeviceData(beaconParser.getMAC_address(), mActivity.getString(R.string.stringDevice) + String.valueOf(count + 1), state, (long) System.currentTimeMillis());
                                appDB2.insertBeacon(deviceData);
                                appDB2.close();
                                if (getListActivity() != null) {
                                    getListActivity().refreshList();
                                }
                            }
                        }
                    });
                }

                @Override
                public void onScanFailed(int i) {
                    Log.e(getClass().getName(), "onScanFailed");
                }
            };


    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void setScanning(boolean isScanning) {
        mScanning = isScanning;
    }

    public ListActivity getListActivity() {
        return listActivity;
    }

    public void setListActivity(ListActivity listActivity) {
        this.listActivity = listActivity;
    }

    public void setBleScanner(BluetoothLeScanner bluetoothLeScanner) {
        mBleScanner = bluetoothLeScanner;
    }

    public BluetoothLeScanner getBleScanner() {
        return mBleScanner;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
