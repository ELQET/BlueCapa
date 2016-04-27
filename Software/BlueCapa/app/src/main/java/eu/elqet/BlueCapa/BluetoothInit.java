package eu.elqet.BlueCapa;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Matej Baran on 10.3.2016.
 */

public class BluetoothInit extends Activity {

    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_FINE_LOCATION = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ble_enable);
        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        try {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        } catch (Exception e) {
            Log.e(getClass().getName(), "bluetooth module init error: " + e.toString());
        }
        CheckBlueToothState();
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.stringPermissionGranted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.stringPermissionNotGranted, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void CheckBlueToothState() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            BluetoothLowEnergy.getInstance().setBleScanner(mBluetoothAdapter.getBluetoothLeScanner());
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), R.string.stringToastBluetoothEnabled, Toast.LENGTH_LONG).show();
                BluetoothLowEnergy.getInstance().setBleScanner(mBluetoothAdapter.getBluetoothLeScanner());
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), R.string.stringToastBluetoothCanceled, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
