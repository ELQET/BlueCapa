package eu.elqet.BlueCapa;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matej Baran on 20.3.2016.
 */

public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private boolean recording = false;
    private String deviceName = "";
    private long recordTimer = 0;
    private boolean flashlight = false;
    private long flashlightTimer = 0;
    private Camera camera = null;
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_camera);
        deviceName = getIntent().getExtras().getString("device_name");
        recordTimer = getIntent().getExtras().getLong("recordTimer");
        flashlight = getIntent().getExtras().getBoolean("flashlight");
        flashlightTimer = getIntent().getExtras().getLong("flashlightTimer");
        camera = getCameraInstance();
        if (camera == null) {
            finish();
            return;
        }
        initRecorder();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.cameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        activity = this;
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            Log.e(getClass().getName(), "getCameraInstance error: " + e.toString());
        }
        return camera;
    }

    private void initRecorder() {
        Camera.Parameters parameters = camera.getParameters();
        if (flashlight) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(parameters);
        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    if (flashlightTimer > recordTimer) {
                        if (camera != null) {
                            camera.release();
                            camera = null;
                        }
                        endTasks();
                    }
                } catch (Exception e) {
                    Log.e(getClass().getName(), "initRecorder flashlight timer error: " + e.toString());
                }
            }
        }, flashlightTimer);
        camera.unlock();
        recorder = new MediaRecorder();
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile cpHigh = CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);
        String state = Environment.getExternalStorageState();
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyy_hh_mm_ss");
        String timeStamp = s.format(new Date());
        String fileName = deviceName + "_" + timeStamp + ".mp4";

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            recorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/" + fileName);
        } else {
            recorder.setOutputFile(getFilesDir().getAbsolutePath() + "/" + fileName);
        }
    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            Log.e(getClass().getName(), "prepareRecorder state error: " + e.toString());
            e.printStackTrace();
            endTasks();
        } catch (IOException e) {
            Log.e(getClass().getName(), "prepareRecorder IO error: " + e.toString());
            e.printStackTrace();
            endTasks();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            prepareRecorder();
            recording = true;
            recorder.start();
            final Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recorder.stop();
                    recording = false;
                    endTasks();
                }
            }, recordTimer);
        } catch (Exception e) {
            Log.e(getClass().getName(), "video surfaceCreated error: " + e.toString());
            endTasks();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
            recorder.release();
            recorder = null;
        }
        if (flashlightTimer <= recordTimer) {
            if (camera != null) {
                camera.release();
                camera = null;
            }
            endTasks();
        }
    }

    private void endTasks() {
        AppDB appDB1 = new AppDB(this);
        appDB1.updateSettingsVideoRecording(false);
        appDB1.close();
        activity.finishAndRemoveTask();
    }
}
