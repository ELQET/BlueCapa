package eu.elqet.BlueCapa;

import android.provider.BaseColumns;

/**
 * Created by Matej Baran on 8.3.2016.
 */
public final class AppDBTables {

    private AppDBTables() {
    }

    public static abstract class BeaconEntry implements BaseColumns {
        public static final String TABLE_NAME = "beacons";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_MAC = "mac";
        public static final String COLUMN_NAME_IS_NOTIFICATION = "notification";
        public static final String COLUMN_NAME_IS_AUDIO = "is_audio";
        public static final String COLUMN_NAME_AUDIO = "audio";
        public static final String COLUMN_NAME_IS_FLASHLIGHT = "is_flashlight";
        public static final String COLUMN_NAME_IS_VIBRATOR = "is_vibrator";
        public static final String COLUMN_NAME_VIBRATOR = "vibrator";
        public static final String COLUMN_NAME_VIDEO_RECORDING = "video";
        public static final String COLUMN_NAME_LAST_TIME = "lastTime";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_IMAGE_WIDTH = "image_width";
        public static final String COLUMN_NAME_IMAGE_HEIGHT = "image_height";
        public static final String COLUMN_NAME_POSITION = "position";
        public static final String COLUMN_NAME_IGNORED = "ignore";
    }

    public static abstract class SettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_NAME_SERVICE_ALIVE = "serviceAlive";
        public static final String COLUMN_NAME_BLE_SCAN_FLAGS = "bleScanFlags";
        public static final String COLUMN_NAME_FLASHLIGHT_PERIOD = "flashlightPeriod";
        public static final String COLUMN_NAME_VIBRATION_PERIOD = "vibrationPeriod";
        public static final String COLUMN_NAME_AUDIO_PERIOD = "audioPeriod";
        public static final String COLUMN_NAME_VIDEO_PERIOD = "videoPeriod";
        public static final String COLUMN_NAME_IS_RECORDING = "isVideoRecording";
        public static final String COLUMN_NAME_NOTIFY = "notify";
    }

    public static abstract class TimeLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "time_log";
        public static final String COLUMN_NAME_MAC = "mac";
        public static final String COLUMN_NAME_TIME_STAMP = "timeStamp";
    }
}
