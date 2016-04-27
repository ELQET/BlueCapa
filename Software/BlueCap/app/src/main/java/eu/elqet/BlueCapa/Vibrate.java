package eu.elqet.BlueCapa;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by Matej Baran on 12.3.2016.
 */
public class Vibrate {

    private static Vibrate instanceVibrate = new Vibrate();
    private Vibrator vibrator;
    private Context context;
    private boolean vibratorOK = false;
    private boolean vibrating = false;

    private Vibrate() {

    }

    public boolean InitVibrate(Context context) {
        try {
            instanceVibrate.context = context.getApplicationContext();
            vibrator = (Vibrator) instanceVibrate.context.getSystemService(Context.VIBRATOR_SERVICE);
            vibratorOK = true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "scanLeDevice error: " + e.toString());
            vibratorOK = false;
            return false;
        }
        return true;
    }

    public static Vibrate getInstance() {
        return instanceVibrate;
    }

    public boolean startVibrate(int repeat) {
        if (!vibratorOK) return false;
        if (isVibrating()) return false;
        try {
            long[] pattern = {10, 50, 100, 200, 400};
            vibrator.vibrate(pattern, repeat);
            setVibrating(true);
            return true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "startVibrate error: " + e.toString());
            return false;
        }
    }

    public boolean stopVibrate() {
        if (!vibratorOK) return false;
        if (!isVibrating()) return false;
        try {
            vibrator.cancel();
            setVibrating(false);
            return true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "stopVibrate error: " + e.toString());
            return false;
        }
    }

    public boolean canVibrate() {
        return vibrator.hasVibrator();
    }

    public boolean isVibrating() {
        return vibrating;
    }

    public void setVibrating(boolean vibrating) {
        this.vibrating = vibrating;
    }
}
