package me.jiho.butterfly.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import me.jiho.butterfly.MainActivity;
import me.jiho.butterfly.R;

/**
 * Created by jiho on 1/25/15.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    public static final int VIBRATE_DURATION = 500;

    private static final String KEY_GCM_NOTIFICATION_ID = "gcm_notification_id";
    private static final String KEY_MESSAGE = "message";
    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = Integer.parseInt(intent.getStringExtra(KEY_GCM_NOTIFICATION_ID));
        String message = intent.getStringExtra(KEY_MESSAGE);
        if (notificationId == 0 || message == null) {
            return;
        }

        // notification with vibrate
        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(VIBRATE_DURATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setColor(Color.WHITE)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_received_24)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(notificationId, mBuilder.build());
    }
}
