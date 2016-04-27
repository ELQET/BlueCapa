package eu.elqet.BlueCapa;

import android.bluetooth.le.ScanResult;
import android.util.Log;

/**
 * Created by Matej Baran on 2.3.2016.
 */
public class BeaconParser {

    private String MAC_address = "";
    private double batteryStatus = 0.0;
    private double signal_strength = 0.0;
    private int sensor_data = 0;
    private boolean active = false;
    private boolean button_RF_POWER = false;
    private boolean button_BEACON = false;
    private boolean button_LOCK = false;
    private boolean button_SENSOR_AREA = false;
    private boolean status_BUTTON_LOCKED = false;
    private boolean status_BEACON_DATA = false;
    private boolean status_BEACON_ON = false;
    private int offset = 5;

    public BeaconParser(ScanResult scanResult) {
        try {
            if (UpdateBlueBeacon(scanResult.getScanRecord().getBytes(), scanResult.getRssi())) {
                this.MAC_address = scanResult.getDevice().getAddress();
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "BeaconParser error: " + e.toString());
        }
    }

    public boolean UpdateBlueBeacon(byte[] data, double rssi) {
        if (!Character.toString((char) data[0 + offset]).equals("S")) return false;
        if (!Character.toString((char) data[5 + offset]).equals("E")) return false;
        this.signal_strength = rssi;
        batteryStatus = GetBatteryStatusFromPacket(data);
        sensor_data = GetSensorDataFromPacket(data);
        GetButtonsStatusFromPacket(data);
        GetBeaconStatusFromPacket(data);
        this.setActive(true);
        return true;
    }

    private void GetBeaconStatusFromPacket(byte[] data) {
        if ((data[1 + offset] & 0x10) == 0x10) status_BUTTON_LOCKED = true;
        else status_BUTTON_LOCKED = false;
        if ((data[1 + offset] & 0x40) == 0x40) status_BEACON_DATA = true;
        else status_BEACON_DATA = false;
        if ((data[1 + offset] & 0x80) == 0x80) status_BEACON_ON = true;
        status_BEACON_ON = false;
    }

    private void GetButtonsStatusFromPacket(byte[] data) {
        if ((data[1 + offset] & 0x01) == 0x01) button_RF_POWER = true;
        else button_RF_POWER = false;
        if ((data[1 + offset] & 0x02) == 0x02) button_BEACON = true;
        else button_BEACON = false;
        if ((data[1 + offset] & 0x04) == 0x04) button_LOCK = true;
        else button_LOCK = false;
        if ((data[1 + offset] & 0x08) == 0x08) button_SENSOR_AREA = true;
        else button_SENSOR_AREA = false;
    }

    private int GetSensorDataFromPacket(byte[] data) {
        return (int) data[2 + offset];
    }

    private double GetBatteryStatusFromPacket(byte[] data) {
        int ADvalue = ((data[3 + offset] & 0xff) << 8) | (data[4 + offset] & 0xff);
        double result = 1048.576 / (double) ADvalue;
        return result;
    }

    public double getBatteryStatus() {
        return batteryStatus;
    }

    public double getSignalStrength() {
        return signal_strength;
    }

    public int getSensor_data() {
        return sensor_data;
    }

    public boolean isButton_RF_POWER() {
        return button_RF_POWER;
    }

    public boolean isButton_BEACON() {
        return button_BEACON;
    }

    public boolean isButton_LOCK() {
        return button_LOCK;
    }

    public boolean isButton_SENSOR_AREA() {
        return button_SENSOR_AREA;
    }

    public boolean isStatus_BUTTON_LOCKED() {
        return status_BUTTON_LOCKED;
    }

    public boolean isStatus_BEACON_DATA() {
        return status_BEACON_DATA;
    }

    public boolean isStatus_BEACON_ON() {
        return status_BEACON_ON;
    }

    public String getMAC_address() {
        return MAC_address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
