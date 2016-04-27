package eu.elqet.BlueCapa;

import android.app.Activity;
import android.database.Cursor;
import java.io.File;

/**
 * Created by Matej Baran on 13.3.2016.
 */
public class DeviceData {

    private Activity activity;
    private String mac = "";
    private String title = "";
    private String state = "";
    private File image = null;
    private int imageWidth = 0;
    private int imageHeight = 0;
    private boolean notification = true;
    private boolean audio = false;
    private File audioFile = null;
    private boolean vibrator = false;
    private int vibratorRepeat = 0;
    private boolean flashlight = false;
    private boolean videoRecording = false;
    private long lasTimeStamp = 0;
    private int id = -1;
    private int position = -1;
    private boolean ignored = false;
    private boolean updated = false;

    public DeviceData(String mac, String title, String state, long lasTimeStamp) {
        setMac(mac);
        setTitle(title);
        setState(state);
        setLasTimeStamp(lasTimeStamp);
    }

    public DeviceData(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry._ID));
        int position = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION));
        int ignored = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED));
        String mac = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_MAC));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_TITLE));
        String state = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_STATE));
        int notification = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION));
        int audio = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO));
        String audioFile = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO));
        int vibrator = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR));
        int vibratorRepeat = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR));
        int flashlight = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT));
        int videorecording = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING));
        long lasTimeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME));
        String image = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE));
        int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH));
        int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT));
        this.id = id;
        setMac(mac);
        setTitle(title);
        setState(state);
        if (notification == 0) setNotification(false);
        else setNotification(true);
        if (audio == 0) setAudio(false);
        else setAudio(true);
        File file = new File(audioFile);
        setAudioFile(file);
        if (vibrator == 0) setVibrator(false);
        else setVibrator(true);
        setVibratorRepeat(vibratorRepeat);
        if (flashlight == 0) setFlashlight(false);
        else setFlashlight(true);
        if (videorecording == 0) setVideoRecording(false);
        else setVideoRecording(true);
        setLasTimeStamp(lasTimeStamp);
        File iFile = new File(image);
        setImage(iFile);
        setImageWidth(imageWidth);
        setImageHeight(imageHeight);
        setPosition(position);
        if (ignored == 0) setIgnored(false);
        else setIgnored(true);
    }

    public DeviceData(Activity activity, Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry._ID));
        int position = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_POSITION));
        int ignored = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IGNORED));
        String mac = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_MAC));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_TITLE));
        String state = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_STATE));
        int notification = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_NOTIFICATION));
        int audio = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_AUDIO));
        String audioFile = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_AUDIO));
        int vibrator = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_VIBRATOR));
        int vibratorRepeat = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_VIBRATOR));
        int flashlight = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IS_FLASHLIGHT));
        int videorecording = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_VIDEO_RECORDING));
        long lasTimeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_LAST_TIME));
        String image = cursor.getString(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE));
        int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_WIDTH));
        int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTables.BeaconEntry.COLUMN_NAME_IMAGE_HEIGHT));
        setActivity(activity);
        this.id = id;
        setMac(mac);
        setTitle(title);
        setState(state);
        if (notification == 0) setNotification(false);
        else setNotification(true);
        if (audio == 0) setAudio(false);
        else setAudio(true);
        File file = new File(audioFile);
        setAudioFile(file);
        if (vibrator == 0) setVibrator(false);
        else setVibrator(true);
        setVibratorRepeat(vibratorRepeat);
        if (flashlight == 0) setFlashlight(false);
        else setFlashlight(true);
        if (videorecording == 0) setVideoRecording(false);
        else setVideoRecording(true);
        setLasTimeStamp(lasTimeStamp);
        File iFile = new File(image);
        setImage(iFile);
        setImageWidth(imageWidth);
        setImageHeight(imageHeight);
        setPosition(position);
        if (ignored == 0) setIgnored(false);
        else setIgnored(true);
    }

    public DeviceData(int id, int position, boolean ignored, String mac, String title, String state, boolean notification, boolean audio, File audioFile, boolean vibrator, int vibratorRepeat, boolean flashlight, boolean videorecording, long lasTimeStamp, File image, int imageWidth, int imageHeight) {
        this.id = id;
        setMac(mac);
        setTitle(title);
        setState(state);
        setNotification(notification);
        setAudio(audio);
        setAudioFile(audioFile);
        setVibrator(vibrator);
        setVibratorRepeat(vibratorRepeat);
        setFlashlight(flashlight);
        setVideoRecording(videorecording);
        setLasTimeStamp(lasTimeStamp);
        setImage(image);
        setImageWidth(imageWidth);
        setImageHeight(imageHeight);
        setPosition(position);
        setIgnored(ignored);
    }

    public DeviceData(int id, int position, int ignored, String mac, String title, String state, int notification, int audio, String audioFile, int vibrator, int vibratorRepeat, int flashlight, int videorecording, long lasTimeStamp, String image, int imageWidth, int imageHeight) {
        this.id = id;
        setMac(mac);
        setTitle(title);
        setState(state);
        if (notification == 0) setNotification(false);
        else setNotification(true);
        if (audio == 0) setAudio(false);
        else setAudio(true);
        File file = new File(audioFile);
        setAudioFile(file);
        if (vibrator == 0) setVibrator(false);
        else setVibrator(true);
        setVibratorRepeat(vibratorRepeat);
        if (flashlight == 0) setFlashlight(false);
        else setFlashlight(true);
        if (videorecording == 0) setVideoRecording(false);
        else setVideoRecording(true);
        setLasTimeStamp(lasTimeStamp);
        File iFile = new File(image);
        setImage(iFile);
        setImageWidth(imageWidth);
        setImageHeight(imageHeight);
        setPosition(position);
        if (ignored == 0) setIgnored(false);
        else setIgnored(true);
    }

    public DeviceData(Activity activity, int id, int position, int ignored, String mac, String title, String state, int notification, int audio, String audioFile, int vibrator, int vibratorRepeat, int flashlight, int videorecording, long lasTimeStamp, String image, int imageWidth, int imageHeight) {
        this.id = id;
        setMac(mac);
        setTitle(title);
        setState(state);
        if (notification == 0) setNotification(false);
        else setNotification(true);
        if (audio == 0) setAudio(false);
        else setAudio(true);
        File file = new File(audioFile);
        setAudioFile(file);
        if (vibrator == 0) setVibrator(false);
        else setVibrator(true);
        setVibratorRepeat(vibratorRepeat);
        if (flashlight == 0) setFlashlight(false);
        else setFlashlight(true);
        if (videorecording == 0) setVideoRecording(false);
        else setVideoRecording(true);
        setLasTimeStamp(lasTimeStamp);
        setActivity(activity);
        File iFile = new File(image);
        setImage(iFile);
        setImageWidth(imageWidth);
        setImageHeight(imageHeight);
        setPosition(position);
        if (ignored == 0) setIgnored(false);
        else setIgnored(true);
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if ((state != null) && (state.length() > 0)) {
            this.state = state;
        } else {
            this.state = "unknown state";
        }
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public boolean isVibrator() {
        return vibrator;
    }

    public void setVibrator(boolean vibrator) {
        this.vibrator = vibrator;
    }

    public int getVibratorRepeat() {
        return vibratorRepeat;
    }

    public String getVibratorRepeatString() {
        return String.valueOf(vibratorRepeat);
    }

    public void setVibratorRepeat(int vibratorRepeat) {
        this.vibratorRepeat = vibratorRepeat;
    }

    public boolean isFlashlight() {
        return flashlight;
    }

    public void setFlashlight(boolean flashlight) {
        this.flashlight = flashlight;
    }

    public int getNotification() {
        if (notification) return 1;
        return 0;
    }

    public String getNotificationString() {
        if (notification) return "1";
        return "0";
    }

    public int getAudio() {
        if (audio) return 1;
        return 0;
    }

    public String getAudioString() {
        if (audio) return "1";
        return "0";
    }

    public String getAudioFileString() {
        if (audioFile == null) return "";
        else return audioFile.toString();
    }

    public int getVibrator() {
        if (vibrator) return 1;
        return 0;
    }

    public String getVibratorString() {
        if (vibrator) return "1";
        return "0";
    }

    public int getFlashLight() {
        if (flashlight) return 1;
        return 0;
    }

    public String getFlashLightString() {
        if (flashlight) return "1";
        return "0";
    }

    public int getId() {
        return id;
    }

    public boolean isVideoRecording() {
        return videoRecording;
    }

    public int getVideoRecording() {
        if (videoRecording) return 1;
        return 0;
    }

    public void setVideoRecording(boolean videoRecording) {
        this.videoRecording = videoRecording;
    }

    public long getLasTimeStamp() {
        return lasTimeStamp;
    }

    public void setLasTimeStamp(long lasTimeStamp) {
        this.lasTimeStamp = lasTimeStamp;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public String getImageFileString() {
        if (image == null) return "";
        else return image.toString();
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public int getIgnored() {
        if (isIgnored()) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}
