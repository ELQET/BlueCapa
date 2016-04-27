package eu.elqet.BlueCapa;

import android.bluetooth.le.ScanSettings;

/**
 * Created by Matej Baran on 8.3.2016.
 */
public final class Settings {

    private static boolean serviceAlive = false;
    private static int bleScanSettings = ScanSettings.SCAN_MODE_LOW_LATENCY;
    private static long flashlightPeriod = 10000;
    private static long vibrationPeriod = 6000;
    private static long audioPeriod = 20000;
    private static long notifyPeriod = 5000;
    private static long videoPeriod = 10000;
    private static boolean notify = true;
    private static boolean videoRecording = false;
    private static long bleScanPeriod = 1000000;  // Stops scanning after 1000 seconds = 16,66 minutes

    public static long getBleScanPeriod() {
        return bleScanPeriod;
    }

    public static void setBleScanPeriod(long scanPeriod) {
        Settings.bleScanPeriod = scanPeriod;
    }

    public static long getFlashlightPeriod() {
        return flashlightPeriod;
    }

    public static void setFlashlightPeriod(long flashlightPeriod) {
        Settings.flashlightPeriod = flashlightPeriod;
    }

    public static long getVibrationPeriod() {
        return vibrationPeriod;
    }

    public static void setVibrationPeriod(long vibrationPeriod) {
        Settings.vibrationPeriod = vibrationPeriod;
    }

    public static long getAudioPeriod() {
        return audioPeriod;
    }

    public static void setAudioPeriod(long audioPeriod) {
        Settings.audioPeriod = audioPeriod;
    }

    public static long getNotifyPeriod() {
        return notifyPeriod;
    }

    public static void setNotifyPeriod(long notifyPeriod) {
        Settings.notifyPeriod = notifyPeriod;
    }

    public static boolean isServiceAlive() {
        return serviceAlive;
    }

    public static void setServiceAlive(boolean serviceAlive) {
        Settings.serviceAlive = serviceAlive;
    }

    private Settings() {

    }

    public static int getBleScanSettings() {
        return bleScanSettings;
    }

    public static void setBleScanSettings(int scanSettings) {
        Settings.bleScanSettings = scanSettings;
    }

    public static boolean isNotify() {
        return notify;
    }

    public static void setNotify(boolean notify) {
        Settings.notify = notify;
    }

    public static long getVideoPeriod() {
        return videoPeriod;
    }

    public static void setVideoPeriod(long videoPeriod) {
        Settings.videoPeriod = videoPeriod;
    }

    public static boolean isVideoRecording() {
        return videoRecording;
    }

    public static void setVideoRecording(boolean videoRecording) {
        Settings.videoRecording = videoRecording;
    }
}
