package eu.elqet.BlueCapa;

import android.animation.ArgbEvaluator;
import android.util.Log;
import android.widget.ImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Matej Baran on 8.3.2016.
 */
public final class Utils {

    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static int[] colors;
    private Calendar cal = Calendar.getInstance();
    private TimeZone tz = cal.getTimeZone();

    private Utils() {

    }

    public static void InitUtils() {
        int c1 = 0xFFCC0000; // ARGB representation of RED
        int c2 = 0xFFCCCC00; // ARGB representation of YELLOW
        int c3 = 0xFF00CC00; // ARGB representation of GREEN
        ArgbEvaluator evaluator = new ArgbEvaluator();

        colors = new int[10];
        colors[0] = (int) evaluator.evaluate(0f, c1, c2); // 0f/9f = 0f
        colors[1] = (int) evaluator.evaluate(2f / 9f, c1, c2);
        colors[2] = (int) evaluator.evaluate(4f / 9f, c1, c2);
        colors[3] = (int) evaluator.evaluate(6f / 9f, c1, c2);
        colors[4] = (int) evaluator.evaluate(8f / 9f, c1, c2);
        colors[5] = (int) evaluator.evaluate(1f / 9f, c2, c3);
        colors[6] = (int) evaluator.evaluate(3f / 9f, c2, c3);
        colors[7] = (int) evaluator.evaluate(5f / 9f, c2, c3);
        colors[8] = (int) evaluator.evaluate(7f / 9f, c2, c3);
        colors[9] = (int) evaluator.evaluate(1f, c2, c3); // 9f/9f = 1f

    }

    public static String getDateTimeFromTimeStamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        sdf.setTimeZone(tz);
        return sdf.format(new Date(timestamp));
    }

    public static String getOnlyTimeFromStamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(tz);
        return sdf.format(new Date(timestamp));
    }

    public static String getOnlyDateFromStamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setTimeZone(tz);
        return sdf.format(new Date(timestamp));
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static double roundVoltageDecimals(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public static int setColorFromVoltage(String voltage) {
        double volt = 0.0;

        String v = voltage.substring(0, voltage.length() - 1);
        try {
            volt = Double.parseDouble(v);
        } catch (NumberFormatException e) {
            Log.e("Utils", "setColorFromVoltage error: " + e.toString());
        }

        volt = (volt - 2.0) * 100.0;
        int index = (int) Math.round(volt / 13.0) - 1;
        if (index < 0) {
            index = 0;
        } else if (index > 9) {
            index = 9;
        }

        return colors[index];
    }

    public static void setImageFromVoltage(String voltage, ImageView imageView) {
        double volt = 0.0;

        String v = voltage.substring(0, voltage.length() - 1);
        try {
            volt = Double.parseDouble(v); // Make use of autoboxing.  It's also easier to read.
        } catch (NumberFormatException e) {
            Log.e("Utils", "setImageFromVoltage error: " + e.toString());
        }

        volt = (volt - 2.0) * 100.0;
        int index = (int) Math.round(volt / 13.0) - 1;
        if (index < 0) {
            index = 0;
        } else if (index > 7) {
            index = 7;
        }

        switch (index) {
            case 0:
                imageView.setImageResource(R.mipmap.ic_action_device_battery_alert);
                return;
            case 1:
                imageView.setImageResource(R.mipmap.ic_action_device_battery_20);
                return;
            case 2:
                imageView.setImageResource(R.mipmap.ic_action_device_battery_30);
                return;
            case 3:
                imageView.setImageResource(R.mipmap.ic_action_device_battery_50);
                return;
            case 4:
                imageView.setImageResource(R.mipmap.ic_action_device_battery_60);
                return;
            case 5:
                imageView.setImageResource(R.mipmap.ic_action_device_battery_80);
                return;
            case 6:
                imageView.setImageResource(R.mipmap.ic_action_device_battery_90);
                return;
            case 7:
                imageView.setImageResource(R.mipmap.ic_action_device_battery_full);
                return;
            default:
                break;
        }
    }

    public static void setImageFromIntensity(String intensity, ImageView imageView) {
        double intens = 0.0;

        try {
            intens = Double.parseDouble(intensity);
        } catch (NumberFormatException e) {
            Log.e("Utils", "setImageFromIntensity error: " + e.toString());
        }

        intens = Math.abs(intens);
        int index = (int) Math.round(intens / 10.0) - 1;
        if (index < 0) {
            index = 0;
        } else if (index > 9) {
            index = 9;
        }

        switch (index) {
            case 0:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_1);
                return;
            case 1:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_2);
                return;
            case 2:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_3);
                return;
            case 3:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_4);
                return;
            case 4:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_5);
                return;
            case 5:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_6);
                return;
            case 6:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_7);
                return;
            case 7:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_8);
                return;
            case 8:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_9);
                return;
            case 9:
                imageView.setImageResource(R.mipmap.ic_stat_image_filter_9_plus);
                return;
            default:
                break;
        }
    }

    public static int setColorFromRSSI(String rssi_str) {
        double rssi = 0.0;

        String r = rssi_str.substring(0, rssi_str.length() - 3);
        try {
            rssi = Double.parseDouble(r); // Make use of autoboxing.  It's also easier to read.
        } catch (NumberFormatException e) {
            Log.e("Utils", "setColorFromRSSI error: " + e.toString());
        }

        rssi = (rssi + 100.0);
        int index = (int) Math.round(rssi / 5.0) - 1;
        if (index < 0) {
            index = 0;
        } else if (index > 9) {
            index = 9;
        }

        return colors[index];
    }

    public static void setImageFromRSSI(String rssi_str, ImageView imageView) {
        double rssi = 0.0;

        String r = rssi_str.substring(0, rssi_str.length() - 3);
        try {
            rssi = Double.parseDouble(r); // Make use of autoboxing.  It's also easier to read.
        } catch (NumberFormatException e) {
            Log.e("Utils", "setImageFromRSSI error: " + e.toString());
        }

        rssi = (rssi + 100.0);
        int index = (int) Math.round(rssi / 10.0) - 1;
        if (index < 0) {
            index = 0;
        } else if (index > 4) {
            index = 4;
        }

        switch (index) {
            case 0:
                imageView.setImageResource(R.mipmap.ic_action_device_signal_wifi_0_bar);
                return;
            case 1:
                imageView.setImageResource(R.mipmap.ic_action_device_signal_wifi_1_bar);
                return;
            case 2:
                imageView.setImageResource(R.mipmap.ic_action_device_signal_wifi_2_bar);
                return;
            case 3:
                imageView.setImageResource(R.mipmap.ic_action_device_signal_wifi_3_bar);
                return;
            case 4:
                imageView.setImageResource(R.mipmap.ic_action_device_signal_wifi_4_bar);
                return;
            default:
                break;
        }
    }
}
