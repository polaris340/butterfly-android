package co.bttrfly.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import co.bttrfly.MainActivity;
import co.bttrfly.R;
import co.bttrfly.db.Picture;
import co.bttrfly.picture.PictureDataManager;
import co.bttrfly.picture.PictureDataObservable;
import co.bttrfly.picture.SinglePictureViewActivity;
import co.bttrfly.statics.Constants;

/**
 * Created by jiho on 1/25/15.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {

    private static final String KEY_GCM_NOTIFICATION_ID = "gcm_notification_id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_PICTURE_DATA = "picture_data";

    public static final int GCM_NOTIFICATION_ID_RECEIVED = 12114;
    public static final int GCM_NOTIFICATION_ID_NOTICE = 152;
    public static final int GCM_NOTIFICATION_ID_SENT = 4124;
    public static final int GCM_NOTIFICATION_ID_LIKE = 7642;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean doNotification = preferences.getBoolean(
                context.getString(R.string.key_pref_notification),
                true
        );

        if (!doNotification) return;

        int notificationId = Integer.parseInt(intent.getStringExtra(KEY_GCM_NOTIFICATION_ID));
        String message = intent.getStringExtra(KEY_MESSAGE);
        if (notificationId == 0 || message == null) {
            return;
        }

        int smallIcon = R.drawable.ic_received_24;

        switch (notificationId) {
            case GCM_NOTIFICATION_ID_SENT:
                smallIcon = R.drawable.ic_sent_24;
                break;
            case GCM_NOTIFICATION_ID_LIKE:
                smallIcon = R.drawable.heart_active_18;
                break;
        }


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setColor(Color.WHITE)
                        .setAutoCancel(true)
                        .setSmallIcon(smallIcon)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app


        Intent resultIntent;
        if (intent.hasExtra(Constants.Keys.PICTURE_ID)) {
            resultIntent = SinglePictureViewActivity.getIntent(context, Long.parseLong(intent.getStringExtra(Constants.Keys.PICTURE_ID)));
        } else {
            resultIntent = MainActivity.getIntent(context);
        }

        switch (notificationId) {
            case GCM_NOTIFICATION_ID_LIKE:
            case GCM_NOTIFICATION_ID_SENT:
                resultIntent.putExtra(MainActivity.KEY_FRAGMENT_TYPE, PictureDataManager.Type.SENT.name());
                break;
            case GCM_NOTIFICATION_ID_NOTICE:
                resultIntent.putExtra(Constants.Keys.NOTICE, message);
                break;
        }

        boolean vibrate = preferences.getBoolean(
                context.getString(R.string.key_pref_notification_vibrate),
                true
        );
        boolean sound = preferences.getBoolean(
                context.getString(R.string.key_pref_notification_sound),
                true
        );

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


        Notification notification = mBuilder.build();
        //notification.defaults |= Notification.DEFAULT_ALL;

        if (vibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (sound) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        }


        // mId allows you to update the notification later on.
        mNotificationManager.notify(notificationId, notification);
    }




    private String parseSentNotificationData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String message = jsonObject.getString(KEY_MESSAGE);

            if (jsonObject.has(KEY_PICTURE_DATA)) {
                Picture picture = Picture.fromJson(jsonObject.getString(KEY_PICTURE_DATA));
                PictureDataManager manager = PictureDataManager.getInstance();
                manager.add(PictureDataObservable.Type.SENT, 0, picture);
                manager.addItems(PictureDataObservable.Type.SENT, 0, 1);
            }
            return message;
        } catch (JSONException e) {
            return null;
        }
    }
}
