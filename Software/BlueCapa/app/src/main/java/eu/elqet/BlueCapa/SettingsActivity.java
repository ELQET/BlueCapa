package eu.elqet.BlueCapa;

import android.app.Activity;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Matej Baran on 19.3.2016.
 */
public class SettingsActivity extends Activity {

    private final int REFRESH_SETTINGS_CODE = 3;
    private int m;
    private Context context;
    private TextView textViewValueScanMode;
    private SeekBar seekBarScanMode;
    private TextView textViewValueAudioPeriod;
    private SeekBar seekBarAudioPeriod;
    private TextView textViewValueVibrationPeriod;
    private SeekBar seekBarVibrationPeriod;
    private TextView textViewValueFlashlightPeriod;
    private SeekBar seekBarFlashlightPeriod;
    private TextView textViewValueVideoPeriod;
    private SeekBar seekBarVideoPeriod;
    private Switch switchNotifications;

    String[] modesNames;
    int[] modesValues = new int[]{ScanSettings.SCAN_MODE_LOW_POWER, ScanSettings.SCAN_MODE_BALANCED, ScanSettings.SCAN_MODE_LOW_LATENCY};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this.getApplicationContext();
        modesNames = new String[]{getString(R.string.stringLowPower), getString(R.string.stringBalanced), getString(R.string.stringLowLatency)};
        seekBarScanMode = (SeekBar) findViewById(R.id.seekBarScanMode);
        textViewValueScanMode = (TextView) findViewById(R.id.textViewValueScanMode);
        seekBarScanMode.setMax(2);
        m = updateScanMode();
        seekBarScanMode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewValueScanMode.setText(modesNames[progress]);
                Settings.setBleScanSettings(modesValues[progress]);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        textViewValueScanMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m++;
                if (m > 2) {
                    m = 0;
                }
                Settings.setBleScanSettings(modesValues[m]);
                updateScanMode();
            }
        });

        seekBarAudioPeriod = (SeekBar) findViewById(R.id.seekBarAudioPeriod);
        textViewValueAudioPeriod = (TextView) findViewById(R.id.textViewValueAudioPeriod);
        seekBarAudioPeriod.setMax(240); //4min
        String audioTimerValue = String.valueOf(Settings.getAudioPeriod() / 1000) + getString(R.string.stringS);
        textViewValueAudioPeriod.setText(audioTimerValue);
        seekBarAudioPeriod.setProgress((int) (Settings.getAudioPeriod() / 1000));
        seekBarAudioPeriod.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String audioTimerValue = String.valueOf(progress) + getString(R.string.stringS);
                textViewValueAudioPeriod.setText(audioTimerValue);
                Settings.setAudioPeriod((long) (seekBarAudioPeriod.getProgress() * 1000));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        textViewValueAudioPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarAudioPeriod.getProgress() >= seekBarAudioPeriod.getMax()) {
                    seekBarAudioPeriod.setProgress(0);
                } else {
                    seekBarAudioPeriod.setProgress(seekBarAudioPeriod.getProgress() + 1);
                }
                Settings.setAudioPeriod(seekBarAudioPeriod.getProgress() * 1000);
            }
        });

        seekBarVibrationPeriod = (SeekBar) findViewById(R.id.seekBarVibrationPeriod);
        textViewValueVibrationPeriod = (TextView) findViewById(R.id.textViewValueVibrationPeriod);
        seekBarVibrationPeriod.setMax(30); //0.5min
        String vibrationTimerValue = String.valueOf(Settings.getVibrationPeriod() / 1000) + getString(R.string.stringS);
        textViewValueVibrationPeriod.setText(vibrationTimerValue);
        seekBarVibrationPeriod.setProgress((int) (Settings.getVibrationPeriod() / 1000));
        seekBarVibrationPeriod.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String vibrationTimerValue = String.valueOf(progress) + getString(R.string.stringS);
                textViewValueVibrationPeriod.setText(vibrationTimerValue);
                Settings.setVibrationPeriod((long) (seekBarVibrationPeriod.getProgress() * 1000));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        textViewValueVibrationPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarVibrationPeriod.getProgress() >= seekBarVibrationPeriod.getMax()) {
                    seekBarVibrationPeriod.setProgress(0);
                } else {
                    seekBarVibrationPeriod.setProgress(seekBarVibrationPeriod.getProgress() + 1);
                }
                Settings.setVibrationPeriod(seekBarVibrationPeriod.getProgress() * 1000);
            }
        });

        seekBarFlashlightPeriod = (SeekBar) findViewById(R.id.seekBarFlashlightPeriod);
        textViewValueFlashlightPeriod = (TextView) findViewById(R.id.textViewValueFlashlightPeriod);
        seekBarFlashlightPeriod.setMax(60); //1min
        String flashlightTimerValue = String.valueOf(Settings.getFlashlightPeriod() / 1000) + getString(R.string.stringS);
        textViewValueFlashlightPeriod.setText(flashlightTimerValue);
        seekBarFlashlightPeriod.setProgress((int) (Settings.getFlashlightPeriod() / 1000));
        seekBarFlashlightPeriod.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String flashlightTimerValue = String.valueOf(progress) + getString(R.string.stringS);
                textViewValueFlashlightPeriod.setText(flashlightTimerValue);
                Settings.setFlashlightPeriod((long) (seekBarFlashlightPeriod.getProgress() * 1000));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        textViewValueFlashlightPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarFlashlightPeriod.getProgress() >= seekBarFlashlightPeriod.getMax()) {
                    seekBarFlashlightPeriod.setProgress(0);
                } else {
                    seekBarFlashlightPeriod.setProgress(seekBarFlashlightPeriod.getProgress() + 1);
                }
                Settings.setFlashlightPeriod(seekBarFlashlightPeriod.getProgress() * 1000);
            }
        });

        seekBarVideoPeriod = (SeekBar) findViewById(R.id.seekBarVideoPeriod);
        textViewValueVideoPeriod = (TextView) findViewById(R.id.textViewValueVideoPeriod);
        seekBarVideoPeriod.setMax(300); //5min
        String videoTimerValue = String.valueOf(Settings.getVideoPeriod() / 1000) + getString(R.string.stringS);
        textViewValueVideoPeriod.setText(videoTimerValue);
        seekBarVideoPeriod.setProgress((int) (Settings.getVideoPeriod() / 1000));
        seekBarVideoPeriod.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String videoTimerValue = String.valueOf(progress) + getString(R.string.stringS);
                textViewValueVideoPeriod.setText(videoTimerValue);
                Settings.setVideoPeriod((long) (seekBarVideoPeriod.getProgress() * 1000));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        textViewValueVideoPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarVideoPeriod.getProgress() >= seekBarVideoPeriod.getMax()) {
                    seekBarVideoPeriod.setProgress(0);
                } else {
                    seekBarVideoPeriod.setProgress(seekBarVideoPeriod.getProgress() + 1);
                }
                Settings.setVideoPeriod(seekBarVideoPeriod.getProgress() * 1000);
            }
        });

        switchNotifications = (Switch) findViewById(R.id.switchNotifications);
        if (Settings.isNotify()) {
            switchNotifications.setChecked(true);
            switchNotifications.setText(R.string.stringSettingsNotificationEnabled);
        } else {
            switchNotifications.setChecked(false);
            switchNotifications.setText(R.string.stringSettingsNotificationDisabled);
        }
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Settings.setNotify(true);
                    switchNotifications.setText(R.string.stringSettingsNotificationEnabled);
                } else {
                    Settings.setNotify(false);
                    switchNotifications.setText(R.string.stringSettingsNotificationDisabled);
                }
            }
        });

        Button backButton = (Button) findViewById(R.id.buttonSettingsGoBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonSettingsGoBack: {
                        WriteToDB();
                        setResult(REFRESH_SETTINGS_CODE);
                        finish();
                        break;
                    }
                    default:
                        break;
                }
            }
        });

    }

    private int updateScanMode() {
        switch (Settings.getBleScanSettings()) {
            case ScanSettings.SCAN_MODE_LOW_POWER:
                seekBarScanMode.setProgress(0);
                textViewValueScanMode.setText(modesNames[0]);
                return 0;
            case ScanSettings.SCAN_MODE_BALANCED:
                seekBarScanMode.setProgress(1);
                textViewValueScanMode.setText(modesNames[1]);
                return 1;
            case ScanSettings.SCAN_MODE_LOW_LATENCY:
                seekBarScanMode.setProgress(2);
                textViewValueScanMode.setText(modesNames[2]);
                return 2;
            default:
                return -1;
        }
    }

    private void WriteToDB() {
        AppDB appDB = new AppDB(context);
        appDB.updateSettings();
        appDB.close();
    }

    @Override
    public void onBackPressed() {
        WriteToDB();
        setResult(REFRESH_SETTINGS_CODE);
        finish();
    }
}
