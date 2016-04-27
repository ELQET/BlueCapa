package eu.elqet.BlueCapa;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matej Baran on 18.3.2016.
 */
public class ListenService extends Service {

    private FlashLight flashLight;
    private Vibrate vibrate;
    private Audio audio;
    private Service service;
    private BeaconParser beaconParser;
    private Handler handler;
    private BluetoothLeScanner bleScanner;
    private boolean isScanning = false;
    private Intent intentBroadcast;
    private DeviceData deviceDataCurrent = null;
    private static boolean isCameraActivity = false;

    public ListenService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        try {
            FlashLight.getInstance().InitFlashLight(this);
            Vibrate.getInstance().InitVibrate(this);
            Audio.getInstance().InitAudio(this);
            NotificationAlert.getInstance().InitNotificationAlert(this);
            intentBroadcast = new Intent("BLUE_CAP_DATA_CHANGED");
        } catch (Exception e) {
            Log.e(getClass().getName(), "onCreate error: " + e.toString());
        }
        AppDB appDB = new AppDB(this);
        appDB.readSettings();
        appDB.close();
        isCameraActivity = Settings.isVideoRecording();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker(getString(R.string.stringBlueCapServiceRunning));
        builder.setContentTitle(getString(R.string.stringBlueCapService));
        builder.setContentText(getString(R.string.stringListenToBeacons));
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        builder.setOnlyAlertOnce(true);
        Notification notification = builder.build();
        int NOTIFICATION_ID = 32768;
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        service = this;
        try {
            BluetoothManager bluetoothManager = (BluetoothManager) this.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (bleScanner == null) {
                stopSelf();
            } else {

                ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
                scanSettingsBuilder.setScanMode(Settings.getBleScanSettings());
                ScanSettings scanSettings = scanSettingsBuilder.build();
                List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
                bleScanner.startScan(scanFilters, scanSettings, mLeScanCallback);
                isScanning = true;
                Settings.setServiceAlive(true);
                AppDB appDB = new AppDB(this);
                appDB.updateSettingsServiceAlive(true);
                appDB.close();
            }
        } catch (Error e) {
            Log.e(getClass().getName(), "onStartCommand error: " + e.toString());
            stopSelf();
        }
        return START_STICKY;
    }

    private ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult scanResult) {
                    beaconParser = new BeaconParser(scanResult);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            LEServiceTasks();
                        }
                    });
                }
            };

    private void LEServiceTasks() {
        if (beaconParser != null) {
            if (beaconParser.isStatus_BEACON_DATA()) {
                return;
            }
            long lastTimeStamp = 0;
            AppDB appDB1 = new AppDB(service);
            deviceDataCurrent = appDB1.readBeacon(beaconParser.getMAC_address());
            lastTimeStamp = appDB1.getLastTimeStampOfMac(beaconParser.getMAC_address());
            appDB1.close();
            if (deviceDataCurrent != null) {
                if (deviceDataCurrent.isIgnored()) {
                    return;
                }
            }
            long timeStampDiff = System.currentTimeMillis() - lastTimeStamp;
            if (timeStampDiff > 2000) {
                String state = String.valueOf(beaconParser.getSignalStrength()) + service.getString(R.string.stringDBM)
                        + String.valueOf(Utils.roundVoltageDecimals(beaconParser.getBatteryStatus())) + service.getString(R.string.stringVoltageComma)
                        + String.valueOf(beaconParser.getSensor_data());
                AppDB appDB = new AppDB(service);
                appDB.insertTimeLog(beaconParser.getMAC_address());
                appDB.updateBeaconState(beaconParser.getMAC_address(), state);
                appDB.close();
                try {
                    if (deviceDataCurrent == null) {
                        intentBroadcast.putExtra("new", true);
                    } else {
                        intentBroadcast.putExtra("new", false);
                    }
                    intentBroadcast.putExtra("mac", beaconParser.getMAC_address());
                    intentBroadcast.putExtra("state", state);
                    sendBroadcast(intentBroadcast);
                } catch (Exception e) {
                    Log.e(getClass().getName(), "onScanResult sendBroadcast error: " + e.toString());
                }
            } else {
                return;
            }
            if (deviceDataCurrent == null) {
                return;
            }

            if (deviceDataCurrent.isNotification()) {
                NotificationAlert.getInstance().notifyNow(deviceDataCurrent.getId(), deviceDataCurrent.getTitle(), service.getString(R.string.string_BlueCapEventDetected));
            }

            if (deviceDataCurrent.isAudio()) {
                audio = Audio.getInstance();
                if (!audio.isPlaying()) {
                    audio.playMp3(deviceDataCurrent.getAudioFile());
                    final Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            audio.stop();
                        }
                    }, Settings.getAudioPeriod() + 500);
                }
            }

            if ((deviceDataCurrent.isFlashlight()) && (!deviceDataCurrent.isVideoRecording())) {
                flashLight = FlashLight.getInstance();
                if (!flashLight.isLightOn()) {
                    flashLight.switchLightOn();
                    final Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            flashLight.switchLightOff();
                        }
                    }, Settings.getFlashlightPeriod() + 500);
                }
            }

            if (deviceDataCurrent.isVibrator()) {
                vibrate = Vibrate.getInstance();
                if (!vibrate.isVibrating()) {
                    vibrate.startVibrate(2);
                    final Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            vibrate.stopVibrate();
                        }
                    }, Settings.getVibrationPeriod() + 500);
                }
            }

            if (deviceDataCurrent.isVideoRecording()) {
                TakeVideo(deviceDataCurrent.getTitle(), deviceDataCurrent.isFlashlight(), Settings.getFlashlightPeriod() + 500);
            }
        }
    }

    private void TakeVideo(String name, boolean isFlashlight, long flashlightTimer) {
        try {
            if (!isCameraActivity) {
                isCameraActivity = true;
                AppDB appDB1 = new AppDB(service);
                appDB1.updateSettingsVideoRecording(true);
                appDB1.close();
                Intent i = new Intent(service, CameraActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("device_name", (String) name);
                i.putExtra("recordTimer", (long) (Settings.getVideoPeriod() + 1000));
                i.putExtra("flashlight", isFlashlight);
                i.putExtra("flashlightTimer", flashlightTimer);
                service.startActivity(i);
                final Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Settings.setVideoRecording(false);
                        AppDB appDB2 = new AppDB(service);
                        appDB2.updateSettingsVideoRecording(false);
                        appDB2.close();
                        isCameraActivity = false;
                    }
                }, Settings.getVideoPeriod() + 1500);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "TakeVideo error: " + e.toString());
        }
    }

    @Override
    public void onDestroy() {
        if (isScanning) {
            if (bleScanner != null) bleScanner.stopScan(mLeScanCallback);
            isScanning = false;
        }
        FlashLight.getInstance().switchLightOff();
        Vibrate.getInstance().stopVibrate();
        Audio.getInstance().stop();
        stopForeground(true);
    }
}
