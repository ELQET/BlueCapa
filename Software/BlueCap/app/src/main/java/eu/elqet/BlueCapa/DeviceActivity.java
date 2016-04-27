package eu.elqet.BlueCapa;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Widlak on 15.3.2016.
 */
public class DeviceActivity extends Activity {

    private EditText nameEditText;
    private TextView audioFileText;
    private Button backButton;
    private Button chooseButton;
    private Switch notificationSwitch;
    private Switch audioSwitch;
    private Switch vibrateSwitch;
    private Switch flashlightSwitch;
    private Switch videoSwitch;
    private File mp3File;
    private DeviceData deviceData;
    private Context context;
    private final int REFRESH_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        String idStr = getIntent().getExtras().getString("id");
        context = this.getApplicationContext();

        nameEditText = (EditText) this.findViewById(R.id.nameEditText);
        audioFileText = (TextView) findViewById(R.id.audioFileTextView);
        backButton = (Button)findViewById(R.id.buttonBack);
        chooseButton = (Button)findViewById(R.id.buttonChoose);
        notificationSwitch = (Switch)findViewById(R.id.switchActive);
        audioSwitch = (Switch)findViewById(R.id.switchAudio);
        vibrateSwitch = (Switch)findViewById(R.id.switchVibrator);
        flashlightSwitch = (Switch)findViewById(R.id.switchFlashLight);
        videoSwitch = (Switch)findViewById(R.id.switchVideo);
        AppDB appDB = new AppDB(this);
        Cursor cursor = appDB.getBeaconFromId(idStr);
        if(cursor.moveToFirst()) {
            deviceData = new DeviceData(cursor);
            nameEditText.setText(deviceData.getTitle());

            boolean isAudio = deviceData.isAudio();
            if(isAudio) {
                chooseButton.setEnabled(true);
                audioSwitch.setText(R.string.stringAudioEnabledSwitch);
            }
            else {
                chooseButton.setEnabled(false);
                audioSwitch.setText(R.string.stringAudioDisabledSwitch);
            }
            if ((deviceData.getAudioFileString() != null) && (deviceData.getAudioFileString().length() > 0)) {
                audioFileText.setText(deviceData.getAudioFile().getName());
            } else {
                audioFileText.setText(R.string.stringAudioFile);
            }
            audioSwitch.setChecked(isAudio);

            if(deviceData.isNotification()) {
                notificationSwitch.setChecked(true);
                notificationSwitch.setText(R.string.stringNotificationEnabledSwitch);
            } else {
                notificationSwitch.setChecked(false);
                notificationSwitch.setText(R.string.stringNotificationDisabledSwitch);
            }

            if(deviceData.isVibrator()) {
                vibrateSwitch.setChecked(true);
                vibrateSwitch.setText(R.string.stringVibrateEnabledSwitch);
            } else {
                vibrateSwitch.setChecked(false);
                vibrateSwitch.setText(R.string.stringVibrateDisabledSwitch);
            }

            if(deviceData.isFlashlight()) {
                flashlightSwitch.setChecked(true);
                flashlightSwitch.setText(R.string.stringFlashLightEnabledSwitch);
            } else {
                flashlightSwitch.setChecked(false);
                flashlightSwitch.setText(R.string.stringFlashLightDisabledSwitch);
            }

            if(deviceData.isVideoRecording()) {
                videoSwitch.setChecked(true);
                videoSwitch.setText(R.string.stringVideoRecordingEnabled);
            } else {
                videoSwitch.setChecked(false);
                videoSwitch.setText(R.string.stringVideoRecordingDisabled);
            }
        }

        appDB.close();

        nameEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                            return false;
                        } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || event == null
                                || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (nameEditText.getText().length() > 0) {
                                deviceData.setTitle(nameEditText.getText().toString());
                            }
                        }
                        return false;
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonBack: {
                        WriteToDB();
                        setResult(REFRESH_REQUEST_CODE);
                        finish();
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonChoose: {
                        OpenChooseFileDialog();
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationSwitch.setText(R.string.stringNotificationEnabledSwitch);
                    deviceData.setNotification(true);
                } else {
                    notificationSwitch.setText(R.string.stringNotificationDisabledSwitch);
                    deviceData.setNotification(false);
                }
            }
        });

        audioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    audioSwitch.setText(R.string.stringAudioEnabledSwitch);
                    chooseButton.setEnabled(true);
                    deviceData.setAudio(true);
                } else {
                    audioSwitch.setText(R.string.stringAudioDisabledSwitch);
                    chooseButton.setEnabled(false);
                    deviceData.setAudio(false);
                }
            }
        });

        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vibrateSwitch.setText(R.string.stringVibrateEnabledSwitch);
                    deviceData.setVibrator(true);
                } else {
                    vibrateSwitch.setText(R.string.stringVibrateDisabledSwitch);
                    deviceData.setVibrator(false);
                }
            }
        });

        flashlightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flashlightSwitch.setText(R.string.stringFlashLightEnabledSwitch);
                    deviceData.setFlashlight(true);
                } else {
                    flashlightSwitch.setText(R.string.stringFlashLightDisabledSwitch);
                    deviceData.setFlashlight(false);
                }
            }
        });

        videoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    videoSwitch.setText(R.string.stringVideoRecordingEnabled);
                    deviceData.setVideoRecording(true);
                } else {
                    videoSwitch.setText(R.string.stringVideoRecordingDisabled);
                    deviceData.setVideoRecording(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        WriteToDB();
        setResult(REFRESH_REQUEST_CODE);
        finish();
    }

    private void WriteToDB() {
        AppDB appDB = new AppDB(context);
        appDB.updateBeacon(deviceData);
        appDB.close();
    }

    private void OpenChooseFileDialog() {
        try {
            File ipath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            AudioFileDialog audioFileDialog = new AudioFileDialog(this, ipath);
            audioFileDialog.setFileEndsWith(".mp3");
            audioFileDialog.addFileListener(new AudioFileDialog.FileSelectedListener() {
                public void fileSelected(File file) {
                    Log.d(getClass().getName(), "selected file " + file.toString());
                    mp3File = file;
                    audioFileText.setText(mp3File.getName());
                    deviceData.setAudioFile(mp3File);
                }
            });
            audioFileDialog.showDialog();
        }
        catch(Exception e) {
            Log.e(getClass().getName(), "OpenChooseFileDialog error: " + e.toString());
        }
    }
}
