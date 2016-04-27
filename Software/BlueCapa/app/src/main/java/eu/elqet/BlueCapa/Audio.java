package eu.elqet.BlueCapa;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by Matej Baran on 13.3.2016.
 */
public class Audio {

    private static Audio instanceAudio = new Audio();
    private Context context;
    private AudioManager audioManager;
    private MediaPlayer mPlayer;
    private Boolean audioOK = false;
    private boolean playing = false;

    private Audio() {

    }

    public boolean InitAudio(Context context) {
        try {
            instanceAudio.context = context.getApplicationContext();
            audioManager = (AudioManager) instanceAudio.context.getSystemService(Context.AUDIO_SERVICE);
            audioOK = true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "InitAudio error: " + e.toString());
            audioOK = false;
            return false;
        }
        return true;
    }

    public static Audio getInstance() {
        return instanceAudio;
    }

    public boolean playMp3(File filename) {
        if (!audioOK) return false;
        if (isPlaying()) return false;
        try {
            if ((filename == null) || (filename.length() < 1)) return false;
            mPlayer = MediaPlayer.create(instanceAudio.context, Uri.fromFile(filename));
            mPlayer.start();
            setPlaying(true);
            return true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "playMp3 error: " + e.toString());
            return false;
        }
    }

    public boolean stop() {
        if (!audioOK) return false;
        if (!isPlaying()) return false;
        try {
            mPlayer.stop();
            setPlaying(false);
            return true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "stop error: " + e.toString());
            return false;
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
