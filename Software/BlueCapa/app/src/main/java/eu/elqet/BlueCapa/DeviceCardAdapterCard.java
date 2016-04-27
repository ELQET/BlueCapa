package eu.elqet.BlueCapa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;


/**
 * Created by Matej Baran on 3.4.2016.
 */
public class DeviceCardAdapterCard extends RecyclerView.Adapter<DeviceCardAdapterCard.CardViewHolder> implements CardItemTouchHelperAdapter {

    private Cursor cursor;
    private Activity activity;
    private AppDB appDB;

    public DeviceCardAdapterCard(AppDB appDB, Cursor cursor, Activity activity) {
        this.cursor = cursor;
        this.activity = activity;
        this.appDB = appDB;
    }

    public void changeCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int position) {
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException(activity.getString(R.string.stringCardAdapterCursorException) + position);
        } else {
            cardViewHolder.deviceData = new DeviceData(this.activity, cursor);
            appDB.updateBeaconPosition(position, cardViewHolder.deviceData.getMac());

            cardViewHolder.deviceData.setUpdated(false);
            if (cardViewHolder.deviceData.isAudio()) {
                cardViewHolder.audioSwitch.setChecked(true);
            } else {
                cardViewHolder.audioSwitch.setChecked(false);
            }

            if (cardViewHolder.deviceData.isVibrator()) {
                cardViewHolder.vibrateSwitch.setChecked(true);
            } else {
                cardViewHolder.vibrateSwitch.setChecked(false);
            }

            if (cardViewHolder.deviceData.isNotification()) {
                cardViewHolder.notificationSwitch.setChecked(true);
            } else {
                cardViewHolder.notificationSwitch.setChecked(false);
            }

            if (cardViewHolder.deviceData.isFlashlight()) {
                cardViewHolder.flashlightSwitch.setChecked(true);
            } else {
                cardViewHolder.flashlightSwitch.setChecked(false);
            }

            if (cardViewHolder.deviceData.isVideoRecording()) {
                cardViewHolder.videoSwitch.setChecked(true);
            } else {
                cardViewHolder.videoSwitch.setChecked(false);
            }

            cardViewHolder.vName.setText(cardViewHolder.deviceData.getTitle());
            String[] statesArray = cardViewHolder.deviceData.getState().split(",");
            Utils.setImageFromRSSI(statesArray[0], cardViewHolder.vRssi);
            int count = appDB.getCountTimeLogsOfMac(cardViewHolder.deviceData.getMac());
            String countString = String.valueOf(count) + activity.getString(R.string.stringX);
            cardViewHolder.vCount.setText(countString);
            if (count > 0) {
                cardViewHolder.vLastTime.setText(Utils.getDateTimeFromTimeStamp(cardViewHolder.deviceData.getLasTimeStamp()));
                if (statesArray.length > 2) {
                    Utils.setImageFromIntensity(statesArray[2], cardViewHolder.vIntensity);
                }
            } else {
                cardViewHolder.vLastTime.setText(R.string.stringNoLogs);
                Utils.setImageFromIntensity(activity.getString(R.string.string0), cardViewHolder.vIntensity);
            }
            Utils.setImageFromVoltage(statesArray[1], cardViewHolder.vVoltage);
            if ((cardViewHolder.deviceData.getImage() != null) && (cardViewHolder.deviceData.getImageWidth() > 20) && (cardViewHolder.deviceData.getImageHeight() > 20)) {
                try {
                    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(cardViewHolder.deviceData.getImage().getAbsolutePath()), cardViewHolder.deviceData.getImageWidth(), cardViewHolder.deviceData.getImageHeight());
                    cardViewHolder.imageCardView.setImageBitmap(thumbImage);
                } catch (Exception e) {
                    Log.e(getClass().getName(), "ThumbnailUtils error: " + e.toString());
                    cardViewHolder.imageCardView.setImageResource(R.mipmap.ic_action_image_photo_camera);
                }
            } else {
                cardViewHolder.imageCardView.setImageResource(R.mipmap.ic_action_image_photo_camera);
            }
            cardViewHolder.deviceData.setUpdated(true);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.device_card, viewGroup, false);

        return new CardViewHolder(itemView);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        protected DeviceData deviceData;
        protected TextView vName;
        protected TextView vLastTime;
        protected TextView vCount;
        protected ImageView vRssi;
        protected ImageView vVoltage;
        protected ImageView vIntensity;
        protected ToggleButton audioSwitch;
        protected ToggleButton vibrateSwitch;
        protected ToggleButton flashlightSwitch;
        protected ToggleButton videoSwitch;
        protected ToggleButton notificationSwitch;
        protected ImageView imageCardView;
        private final int VOID_CODE = 0;

        public CardViewHolder(View view) {
            super(view);
            vName = (TextView) view.findViewById(R.id.textViewTitle);
            vLastTime = (TextView) view.findViewById(R.id.textViewLastTime);
            vCount = (TextView) view.findViewById(R.id.textViewCount);
            vIntensity = (ImageView) view.findViewById(R.id.imageViewIntensity);
            vRssi = (ImageView) view.findViewById(R.id.imageViewSignal);
            vVoltage = (ImageView) view.findViewById(R.id.imageViewBattery);
            audioSwitch = (ToggleButton) view.findViewById(R.id.toggleMusicButton);
            vibrateSwitch = (ToggleButton) view.findViewById(R.id.toggleVibrateButton);
            flashlightSwitch = (ToggleButton) view.findViewById(R.id.toggleFlashLightButton);
            videoSwitch = (ToggleButton) view.findViewById(R.id.toggleVideoButton);
            notificationSwitch = (ToggleButton) view.findViewById(R.id.toggleNotificationButton);
            imageCardView = (ImageView) view.findViewById(R.id.imageCardView);

            audioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        deviceData.setAudio(true);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringAudioEnabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        deviceData.setAudio(false);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringAudioDisabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (deviceData.isUpdated()) {
                        AppDB appDB = new AppDB(deviceData.getActivity().getApplicationContext());
                        appDB.updateBeacon(deviceData);
                        appDB.close();
                    }
                }
            });

            vName.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Settings.isNotify()) {
                        Toast.makeText(deviceData.getActivity(), R.string.stringChangeTitle, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            vName.setOnLongClickListener(new TextView.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(deviceData.getActivity());
                    alert.setTitle("Rename device");
                    final EditText input = new EditText(deviceData.getActivity());
                    input.setText(vName.getText());
                    input.setMaxLines(1);
                    input.setSingleLine();
                    alert.setView(input);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deviceData.setTitle(input.getEditableText().toString());
                            vName.setText(input.getEditableText().toString());
                            AppDB appDB = new AppDB(deviceData.getActivity().getApplicationContext());
                            appDB.updateBeacon(deviceData);
                            appDB.close();
                        }
                    });
                    alert.setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    return false;
                }
            });

            audioSwitch.setOnLongClickListener(new CompoundButton.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    OpenChooseMusicFileDialog();
                    return true;
                }
            });

            imageCardView.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Settings.isNotify()) {
                        Toast.makeText(deviceData.getActivity(), R.string.stringChooseNewImage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            imageCardView.setOnLongClickListener(new ImageView.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    FrameLayout layout = (FrameLayout) deviceData.getActivity().findViewById(R.id.imageFrameLayout);
                    int lWidth = layout.getWidth();
                    int lHeight = (int) Math.round((float) layout.getWidth() / 1.777); //16:9 ratio
                    Intent intentBroadcast = new Intent("CARD_LONG_PRESS_ON_IMAGE");
                    intentBroadcast.putExtra("mac", deviceData.getMac());
                    intentBroadcast.putExtra("holderWidth", lWidth);
                    intentBroadcast.putExtra("holderHeight", lHeight);
                    deviceData.getActivity().sendBroadcast(intentBroadcast);
                    return true;
                }
            });

            notificationSwitch.setOnLongClickListener(new CompoundButton.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent i = new Intent(deviceData.getActivity(), LogListActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra("title", deviceData.getTitle());
                    i.putExtra("mac", deviceData.getMac());
                    deviceData.getActivity().startActivityForResult(i, VOID_CODE);
                    return true;
                }
            });

            vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        deviceData.setVibrator(true);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringVibrationEnabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        deviceData.setVibrator(false);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringVibrationDisabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (deviceData.isUpdated()) {
                        AppDB appDB = new AppDB(deviceData.getActivity().getApplicationContext());
                        appDB.updateBeacon(deviceData);
                        appDB.close();
                    }
                }
            });

            notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        deviceData.setNotification(true);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringNotificationEnabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        deviceData.setNotification(false);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringNotificationDisabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (deviceData.isUpdated()) {
                        AppDB appDB = new AppDB(deviceData.getActivity().getApplicationContext());
                        appDB.updateBeacon(deviceData);
                        appDB.close();
                    }
                }
            });

            flashlightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        deviceData.setFlashlight(true);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringFlashlightEnabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        deviceData.setFlashlight(false);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringFlashlightDisabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (deviceData.isUpdated()) {
                        AppDB appDB = new AppDB(deviceData.getActivity().getApplicationContext());
                        appDB.updateBeacon(deviceData);
                        appDB.close();
                    }
                }
            });

            videoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        deviceData.setVideoRecording(true);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringRecordingEnabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        deviceData.setVideoRecording(false);
                        if (deviceData.isUpdated()) {
                            if (Settings.isNotify()) {
                                Toast.makeText(deviceData.getActivity(), deviceData.getTitle() + deviceData.getActivity().getString(R.string.stringRecordingDisabled), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (deviceData.isUpdated()) {
                        AppDB appDB = new AppDB(deviceData.getActivity().getApplicationContext());
                        appDB.updateBeacon(deviceData);
                        appDB.close();
                    }
                }
            });
        }

        private void OpenChooseMusicFileDialog() {
            try {
                File ipath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                AudioFileDialog audioFileDialog = new AudioFileDialog(deviceData.getActivity(), ipath);
                audioFileDialog.setFileEndsWith(".mp3");
                audioFileDialog.addFileListener(new AudioFileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        if (deviceData.isUpdated()) {
                            AppDB appDB = new AppDB(deviceData.getActivity().getApplicationContext());
                            deviceData.setAudioFile(file);
                            audioSwitch.setChecked(true);
                            deviceData.setAudio(true);
                            appDB.updateBeacon(deviceData);
                            appDB.close();
                        }
                    }
                });
                audioFileDialog.showDialog();
            } catch (Exception e) {
                Log.e(getClass().getName(), "OpenChooseMusicFileDialog error: " + e.toString());
            }
        }
    }

    @Override
    public void onItemDismiss(int position) {
        appDB.updateBeaconIgnored(position, true);
        changeCursor(appDB.getAllBeacons());
        notifyItemRemoved(position);
        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 450); //run after 450ms - animation
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition != toPosition) {
            DeviceData fromDeviceData = appDB.readBeaconFromPosition(fromPosition);
            fromDeviceData.setPosition(toPosition);
            DeviceData toDeviceData = appDB.readBeaconFromPosition(toPosition);
            toDeviceData.setPosition(fromPosition);
            appDB.updateBeaconMacWhereID("1", toDeviceData.getId());
            appDB.updateBeaconMacWhereID("2", fromDeviceData.getId());
            appDB.updateBeaconWhereID(fromDeviceData, toDeviceData.getId());
            appDB.updateBeaconWhereID(toDeviceData, fromDeviceData.getId());
            notifyItemMoved(fromPosition, toPosition);
        }
    }
}
