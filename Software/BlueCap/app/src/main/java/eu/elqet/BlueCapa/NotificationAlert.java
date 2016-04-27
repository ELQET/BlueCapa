package eu.elqet.BlueCapa;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by Matej Baran on 17.3.2016.
 */
public class NotificationAlert {

    private static NotificationAlert instanceNotificationAlert = new NotificationAlert();
    private NotificationManagerCompat notificationManager;
    private Context context;
    private int lastNotificationId = -1;


    private NotificationAlert() {

    }

    public void InitNotificationAlert(Context context) {
        this.context = context;
        notificationManager = NotificationManagerCompat.from(context);
        resetNotification();
    }

    public static NotificationAlert getInstance() {
        return instanceNotificationAlert;
    }

    public void notifyNow(int notificationId, String eventTitle, String eventText) {
        if (this.context == null) {
            return;
        }

        if (lastNotificationId == notificationId) {
            final Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetNotification();
                }
            }, Settings.getNotifyPeriod());
            return;
        }
        lastNotificationId = notificationId;
        Intent viewIntent = new Intent(context, MainActivity.class);
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        viewIntent.setAction(Intent.ACTION_MAIN);
        viewIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        String EXTRA_EVENT_ID = "extra_event_id";
        viewIntent.putExtra(EXTRA_EVENT_ID, String.valueOf(notificationId));
        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, 0);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(eventTitle)
                        .setContentText(eventText)
                        .setContentIntent(viewPendingIntent)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void resetNotification() {
        lastNotificationId = -1;
    }
}
