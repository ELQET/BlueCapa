package eu.elqet.BlueCapa;

import android.bluetooth.le.ScanSettings;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Matej Baran on 8.3.2016.
 */
public class AppDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1; // If you change the database schema, you must increment the database version.
    public static final String DATABASE_NAME = "BlueCapApp.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String TEXT_NOT_NULL_UNIQUE_TYPE = " TEXT NOT NULL UNIQUE";
    private static final String TEXT_NOT_NULL_TYPE = " TEXT NOT NULL";
    private static final String INT_TYPE = " INT";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES_BEACONS =
            "CREATE TABLE " + AppDBTables.BeaconEntry.TABLE_NAME + " (" +
                    AppDBTables.BeaconEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AppDBTables.BeaconEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_MAC + TEXT_NOT_NULL_UNIQUE_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO + TEXT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_POSITION + INT_TYPE + COMMA_SEP +
                    AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED + INT_TYPE +
                    " );";

    private static final String SQL_CREATE_ENTRIES_SETTINGS =
            "CREATE TABLE " + AppDBTables.SettingsEntry.TABLE_NAME + " (" +
                    AppDBTables.SettingsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AppDBTables.SettingsEntry.COLUMN_NAME_BLE_SCAN_FLAGS + INT_TYPE + COMMA_SEP +
                    AppDBTables.SettingsEntry.COLUMN_NAME_NOTIFY + INT_TYPE + COMMA_SEP +
                    AppDBTables.SettingsEntry.COLUMN_NAME_SERVICE_ALIVE + INT_TYPE + COMMA_SEP +
                    AppDBTables.SettingsEntry.COLUMN_NAME_AUDIO_PERIOD + INT_TYPE + COMMA_SEP +
                    AppDBTables.SettingsEntry.COLUMN_NAME_FLASHLIGHT_PERIOD + INT_TYPE + COMMA_SEP +
                    AppDBTables.SettingsEntry.COLUMN_NAME_VIBRATION_PERIOD + INT_TYPE + COMMA_SEP +
                    AppDBTables.SettingsEntry.COLUMN_NAME_VIDEO_PERIOD + INT_TYPE + COMMA_SEP +
                    AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING + INT_TYPE +
                    " );";

    private static final String SQL_CREATE_ENTRIES_TIMELOG =
            "CREATE TABLE " + AppDBTables.TimeLogEntry.TABLE_NAME + " (" +
                    AppDBTables.TimeLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AppDBTables.TimeLogEntry.COLUMN_NAME_MAC + TEXT_NOT_NULL_TYPE + COMMA_SEP +
                    AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP + INT_TYPE +
                    " );";

    public AppDB(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        createTableBeacons_local(db);
        createTableSettings_local(db);
        createTableTimeLog_local(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
    }

    private void createTableBeacons_local(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES_BEACONS);
        } catch (Exception e) {
            Log.e(getClass().getName(), "createTableBeacons_local SQL error: " + e.toString());
        }
    }

    public void createTableBeacons() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(SQL_CREATE_ENTRIES_BEACONS);
        } catch (Exception e) {
            Log.e(getClass().getName(), "createTableBeacons SQL error: " + e.toString());
        }
    }

    private void createTableSettings_local(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES_SETTINGS);
        } catch (Exception e) {
            Log.e(getClass().getName(), "createTableSettings_local SQL error: " + e.toString());
        }
    }

    private void createTableTimeLog_local(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES_TIMELOG);
        } catch (Exception e) {
            Log.e(getClass().getName(), "createTableTimeLog_local SQL error: " + e.toString());
        }
    }

    public void createTableTimeLog() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(SQL_CREATE_ENTRIES_TIMELOG);
        } catch (Exception e) {
            Log.e(getClass().getName(), "createTableTimeLog SQL error: " + e.toString());
        }
    }

    public void dropTableBeacons() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + AppDBTables.BeaconEntry.TABLE_NAME;
            db.execSQL(SQL_DELETE_ENTRIES);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "dropTableBeacons SQL error: " + e.toString());
        }
    }

    public void dropTableSettings() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + AppDBTables.SettingsEntry.TABLE_NAME;
            db.execSQL(SQL_DELETE_ENTRIES);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "dropTableSettings SQL error: " + e.toString());
        }
    }

    public void dropTableTimeLog() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + AppDBTables.TimeLogEntry.TABLE_NAME;
            db.execSQL(SQL_DELETE_ENTRIES);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "dropTableTimeLog SQL error: " + e.toString());
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long getLastTimeStampOfMac(String mac) {
        long timeStamp = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {
                    AppDBTables.TimeLogEntry._ID,
                    AppDBTables.TimeLogEntry.COLUMN_NAME_MAC,
                    AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP
            };
            String selection = AppDBTables.TimeLogEntry.COLUMN_NAME_MAC + "=?";
            String[] selectionArgs = {
                    mac
            };
            String sortOrder = AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP + " DESC";
            Cursor cursor = db.query(
                    AppDBTables.TimeLogEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "getLastTimeStampOfMac SQL error: " + e.toString());
        }
        return timeStamp;
    }

    public Cursor getAllTimeLogsOfMac(String mac) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {
                    AppDBTables.TimeLogEntry._ID,
                    AppDBTables.TimeLogEntry.COLUMN_NAME_MAC,
                    AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP
            };
            String selection = AppDBTables.TimeLogEntry.COLUMN_NAME_MAC + "=?";
            String[] selectionArgs = {
                    mac
            };
            String sortOrder = AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP + " DESC";
            Cursor cursor = db.query(
                    AppDBTables.TimeLogEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            return cursor;
        } catch (Exception e) {
            Log.e(getClass().getName(), "getAllTimeLogsOfMac SQL error: " + e.toString());
        }
        return null;
    }

    public int getCountTimeLogsOfMac(String mac) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {
                    AppDBTables.TimeLogEntry._ID,
                    AppDBTables.TimeLogEntry.COLUMN_NAME_MAC
            };
            String selection = AppDBTables.TimeLogEntry.COLUMN_NAME_MAC + "=?";
            String[] selectionArgs = {
                    mac
            };
            String sortOrder = AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP + " DESC";
            Cursor cursor = db.query(
                    AppDBTables.TimeLogEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null
            );
            int count = cursor.getCount();
            cursor.close();
            db.close();
            return count;
        } catch (Exception e) {
            Log.e(getClass().getName(), "getCountTimeLogsOfMac SQL error: " + e.toString());
        }
        return -1;
    }

    public boolean insertTimeLog(String mac) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.TimeLogEntry.COLUMN_NAME_MAC, mac);
            values.put(AppDBTables.TimeLogEntry.COLUMN_NAME_TIME_STAMP, (long) System.currentTimeMillis());
            long newRowId = db.insert(
                    AppDBTables.TimeLogEntry.TABLE_NAME,
                    null,
                    values);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "insertTimeLog SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean deleteLogsOfMac(String mac) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String selection = AppDBTables.TimeLogEntry.COLUMN_NAME_MAC + " LIKE ?";
            String[] selectionArgs = {mac};
            db.delete(AppDBTables.TimeLogEntry.TABLE_NAME, selection, selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "deleteLogsOfMac SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean readSettings() {
        if (!getSettings()) {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_BLE_SCAN_FLAGS, ScanSettings.SCAN_MODE_LOW_LATENCY);
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_NOTIFY, 1);
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_SERVICE_ALIVE, 0);
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_AUDIO_PERIOD, 20000);
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_VIBRATION_PERIOD, 6000);
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_FLASHLIGHT_PERIOD, 10000);
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_VIDEO_PERIOD, 10000);
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING, 0);
                long newRowId = db.insert(
                        AppDBTables.SettingsEntry.TABLE_NAME,
                        null,
                        values);
                db.close();
                return getSettings();
            } catch (Exception e) {
                Log.e(getClass().getName(), "readSettings SQL error: " + e.toString());
                return false;
            }
        }
        return true;
    }

    private boolean getSettings() {
        try {
            SQLiteDatabase db1 = this.getReadableDatabase();
            String buildSQL = "SELECT * FROM " + AppDBTables.SettingsEntry.TABLE_NAME + " WHERE " + AppDBTables.SettingsEntry._ID + " = 1";
            Cursor cursor = db1.rawQuery(buildSQL, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Settings.setAudioPeriod(cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_AUDIO_PERIOD)));
                Settings.setFlashlightPeriod(cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_FLASHLIGHT_PERIOD)));
                Settings.setVibrationPeriod(cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_VIBRATION_PERIOD)));
                Settings.setBleScanSettings(cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_BLE_SCAN_FLAGS)));
                Settings.setVideoPeriod(cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_VIDEO_PERIOD)));
                if (cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_SERVICE_ALIVE)) > 0) {
                    Settings.setServiceAlive(true);
                } else {
                    Settings.setServiceAlive(false);
                }
                if (cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_NOTIFY)) > 0) {
                    Settings.setNotify(true);
                } else {
                    Settings.setNotify(false);
                }
                if (cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING)) > 0) {
                    Settings.setVideoRecording(true);
                } else {
                    Settings.setVideoRecording(false);
                }
                cursor.close();
                db1.close();
                return true;
            } else {
                cursor.close();
                db1.close();
                return false;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "getSettings SQL error: " + e.toString());
            return false;
        }
    }

    public boolean updateSettings() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.SettingsEntry.COLUMN_NAME_FLASHLIGHT_PERIOD, Settings.getFlashlightPeriod());
            values.put(AppDBTables.SettingsEntry.COLUMN_NAME_VIBRATION_PERIOD, Settings.getVibrationPeriod());
            values.put(AppDBTables.SettingsEntry.COLUMN_NAME_AUDIO_PERIOD, Settings.getAudioPeriod());
            values.put(AppDBTables.SettingsEntry.COLUMN_NAME_VIDEO_PERIOD, Settings.getVideoPeriod());
            values.put(AppDBTables.SettingsEntry.COLUMN_NAME_BLE_SCAN_FLAGS, Settings.getBleScanSettings());
            if (Settings.isNotify()) {
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_NOTIFY, 1);
            } else {
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_NOTIFY, 0);
            }
            if (Settings.isVideoRecording()) {
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING, 1);
            } else {
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING, 0);
            }
            String selection = AppDBTables.SettingsEntry._ID + " LIKE ?";
            String[] selectionArgs = {
                    "1"
            };
            int count = db.update(
                    AppDBTables.SettingsEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateSettings SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean readIsRecording() {
        try {
            SQLiteDatabase db1 = this.getReadableDatabase();
            String buildSQL = "SELECT " + AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING + " FROM " + AppDBTables.SettingsEntry.TABLE_NAME + " WHERE " + AppDBTables.SettingsEntry._ID + " = 1";
            Cursor cursor = db1.rawQuery(buildSQL, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING)) > 0) {
                    Settings.setVideoRecording(true);
                } else {
                    Settings.setVideoRecording(false);
                }
                cursor.close();
                db1.close();
                return true;
            } else {
                cursor.close();
                db1.close();
                return false;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "readIsRecording SQL error: " + e.toString());
            return false;
        }
    }

    public boolean updateSettingsVideoRecording(boolean videoRecording) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            Settings.setVideoRecording(videoRecording);
            if (videoRecording) {
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING, 1);
            } else {
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_IS_RECORDING, 0);
            }
            String selection = AppDBTables.SettingsEntry._ID + " LIKE ?";
            String[] selectionArgs = {
                    "1"
            };
            int count = db.update(
                    AppDBTables.SettingsEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateSettingsVideoRecording SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean updateSettingsServiceAlive(boolean serviceAlive) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            if (serviceAlive) {
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_SERVICE_ALIVE, 1);
            } else {
                values.put(AppDBTables.SettingsEntry.COLUMN_NAME_SERVICE_ALIVE, 0);
            }
            String selection = AppDBTables.SettingsEntry._ID + " LIKE ?";
            String[] selectionArgs = {
                    "1"
            };
            int count = db.update(
                    AppDBTables.SettingsEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateSettingsServiceAlive SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean insertBeacon(DeviceData deviceData) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION, deviceData.getPosition());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED, deviceData.getIgnored());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_MAC, deviceData.getMac());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION, deviceData.getNotification());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_TITLE, deviceData.getTitle());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_STATE, deviceData.getState());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO, deviceData.getAudio());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO, deviceData.getAudioFileString());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR, deviceData.getVibrator());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR, deviceData.getVibratorRepeat());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT, deviceData.getFlashLight());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING, deviceData.getVideoRecording());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME, (long) System.currentTimeMillis());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE, deviceData.getImageFileString());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH, deviceData.getImageWidth());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT, deviceData.getImageHeight());
            long newRowId = db.insert(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    null,
                    values);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "insertBeacon SQL error " + e.toString());
            return false;
        }
        return true;
    }

    public Cursor getAllBeacons() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String buildSQL = "SELECT * FROM " + AppDBTables.BeaconEntry.TABLE_NAME + " WHERE " + AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED + " = 0";
            return db.rawQuery(buildSQL, null);
        } catch (Exception e) {
            Log.e(getClass().getName(), "getAllBeacons SQL error: " + e.toString());
        }
        return null;
    }

    public boolean isThereBeacon(String mac) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String buildSQL = "SELECT * FROM " + AppDBTables.BeaconEntry.TABLE_NAME + " WHERE " + AppDBTables.BeaconEntry.COLUMN_NAME_MAC + " = " + mac;
            int count = db.rawQuery(buildSQL, null).getCount();
            db.close();
            if (count > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(getClass().getName(), "isThereBeacon SQL error: " + e.toString());
        }
        return false;
    }

    public int getCountOfBeacons() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String buildSQL = "SELECT * FROM " + AppDBTables.BeaconEntry.TABLE_NAME;
            int count = db.rawQuery(buildSQL, null).getCount();
            db.close();
            return count;
        } catch (Exception e) {
            Log.e(getClass().getName(), "getCountOfBeacons SQL error: " + e.toString());
        }
        return 0;
    }

    public Cursor getBeaconFromId(String id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String buildSQL = "SELECT * FROM " + AppDBTables.BeaconEntry.TABLE_NAME + " WHERE " + AppDBTables.BeaconEntry._ID + " = " + id;
            Cursor cursor = db.rawQuery(buildSQL, null);
            return cursor;
        } catch (Exception e) {
            Log.e(getClass().getName(), "getBeaconFromId SQL error: " + e.toString());
        }
        return null;
    }

    public DeviceData readBeacon(String mac) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                AppDBTables.BeaconEntry._ID,
                AppDBTables.BeaconEntry.COLUMN_NAME_POSITION,
                AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED,
                AppDBTables.BeaconEntry.COLUMN_NAME_MAC,
                AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION,
                AppDBTables.BeaconEntry.COLUMN_NAME_TITLE,
                AppDBTables.BeaconEntry.COLUMN_NAME_STATE,
                AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO,
                AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO,
                AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR,
                AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR,
                AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT,
                AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING,
                AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME,
                AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE,
                AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH,
                AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT
        };
        String selection = AppDBTables.BeaconEntry.COLUMN_NAME_MAC + "=?";
        String[] selectionArgs = {
                mac
        };
        String sortOrder = AppDBTables.BeaconEntry.COLUMN_NAME_TITLE + " DESC";
        Cursor cursor = db.query(
                AppDBTables.BeaconEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        DeviceData deviceData = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            deviceData = new DeviceData(
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry._ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_MAC)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_STATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT))
            );
        }
        cursor.close();
        db.close();
        return deviceData;
    }

    public DeviceData readBeaconFromPosition(int position) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                AppDBTables.BeaconEntry._ID,
                AppDBTables.BeaconEntry.COLUMN_NAME_POSITION,
                AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED,
                AppDBTables.BeaconEntry.COLUMN_NAME_MAC,
                AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION,
                AppDBTables.BeaconEntry.COLUMN_NAME_TITLE,
                AppDBTables.BeaconEntry.COLUMN_NAME_STATE,
                AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO,
                AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO,
                AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR,
                AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR,
                AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT,
                AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING,
                AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME,
                AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE,
                AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH,
                AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT
        };
        String selection = AppDBTables.BeaconEntry.COLUMN_NAME_POSITION + "=?";
        String[] selectionArgs = {
                String.valueOf(position)
        };
        String sortOrder = AppDBTables.BeaconEntry.COLUMN_NAME_TITLE + " DESC";
        Cursor cursor = db.query(
                AppDBTables.BeaconEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        DeviceData deviceData = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            deviceData = new DeviceData(
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry._ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_MAC)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_STATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT))
            );
        }
        cursor.close();
        db.close();
        return deviceData;
    }

    public boolean updateBeaconWhereID(DeviceData deviceData, long id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_MAC, deviceData.getMac());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION, deviceData.getPosition());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED, deviceData.getIgnored());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION, deviceData.getNotification());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_TITLE, deviceData.getTitle());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_STATE, deviceData.getState());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO, deviceData.getAudio());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO, deviceData.getAudioFileString());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR, deviceData.getVibrator());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR, deviceData.getVibratorRepeat());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT, deviceData.getFlashLight());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING, deviceData.getVideoRecording());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME, (long) System.currentTimeMillis());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE, deviceData.getImageFileString());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH, deviceData.getImageWidth());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT, deviceData.getImageHeight());
            String selection = AppDBTables.BeaconEntry._ID + " LIKE ?";
            String[] selectionArgs = {
                    String.valueOf(id)
            };
            int count = db.update(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateBeaconWhereID SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean updateBeacon(DeviceData deviceData) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_MAC, deviceData.getMac());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION, deviceData.getPosition());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED, deviceData.getIgnored());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION, deviceData.getNotification());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_TITLE, deviceData.getTitle());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_STATE, deviceData.getState());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO, deviceData.getAudio());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO, deviceData.getAudioFileString());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR, deviceData.getVibrator());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR, deviceData.getVibratorRepeat());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT, deviceData.getFlashLight());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING, deviceData.getVideoRecording());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME, (long) System.currentTimeMillis());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE, deviceData.getImageFileString());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH, deviceData.getImageWidth());
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT, deviceData.getImageHeight());
            String selection = AppDBTables.BeaconEntry.COLUMN_NAME_MAC + " LIKE ?";
            String[] selectionArgs = {
                    deviceData.getMac()
            };
            int count = db.update(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateBeacon SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean updateBeaconState(String mac, String state) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_STATE, state);
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME, (long) System.currentTimeMillis());
            String selection = AppDBTables.BeaconEntry.COLUMN_NAME_MAC + " LIKE ?";
            String[] selectionArgs = {
                    mac
            };
            int count = db.update(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateBeaconState SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean updateBeaconPosition(long position, String mac) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION, position);
            String selection = AppDBTables.BeaconEntry.COLUMN_NAME_MAC + " LIKE ?";
            String[] selectionArgs = {
                    mac
            };
            int count = db.update(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateBeaconPosition SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean updateBeaconMacWhereID(String mac, long id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_MAC, mac);
            String selection = AppDBTables.BeaconEntry._ID + " LIKE ?";
            String[] selectionArgs = {
                    String.valueOf(id)
            };
            int count = db.update(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateBeaconMacWhereID SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean updateBeaconThumbnail(String mac, int width, int height, String path) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH, width);
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT, height);
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE, path);
            String selection = AppDBTables.BeaconEntry.COLUMN_NAME_MAC + " LIKE ?";
            String[] selectionArgs = {
                    mac
            };
            int count = db.update(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateBeaconThumbnail SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean updateBeaconIgnored(int position, boolean ignored) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION, -1);
            if (ignored) {
                values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED, 1);
            } else {
                values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED, 0);
            }
            String selection = AppDBTables.BeaconEntry.COLUMN_NAME_POSITION + " LIKE ?";
            String[] selectionArgs = {
                    String.valueOf(position)
            };
            int count = db.update(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateBeaconIgnored SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean updateAllBeaconsIgnored() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION, -1);
            values.put(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED, 0);
            String selection = AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED + " LIKE ?";
            String[] selectionArgs = {
                    String.valueOf(1)
            };
            int count = db.update(
                    AppDBTables.BeaconEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "updateAllBeaconsIgnored SQL error: " + e.toString());
            return false;
        }
        return true;
    }

    public boolean deleteBeacon(DeviceData deviceData) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String selection = AppDBTables.BeaconEntry.COLUMN_NAME_MAC + " LIKE ?";
            String[] selectionArgs = {deviceData.getMac()};
            db.delete(AppDBTables.BeaconEntry.TABLE_NAME, selection, selectionArgs);
            db.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "deleteBeacon SQL error: " + e.toString());
            return false;
        }
        return true;
    }
}
