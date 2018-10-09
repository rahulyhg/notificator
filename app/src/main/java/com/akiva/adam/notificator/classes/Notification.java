package com.akiva.adam.notificator.classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.akiva.adam.notificator.R;
import com.akiva.adam.notificator.broadcastReceivers.BroadcastReceiverNotification;
import com.akiva.adam.notificator.interfaces.INotification;
import com.akiva.adam.notificator.interfaces.IProcess;

import java.util.ArrayList;
import java.util.HashMap;

import static com.akiva.adam.notificator.activities.MyActivity.CHECK_TIME_THRESHOLD;
import static com.akiva.adam.notificator.activities.MyActivity.NOTIFICATION_CHANNEL_ID;
import static com.akiva.adam.notificator.activities.MyActivity.NOTIFICATION_RECEIVER;

// A class used to send the user a notification about an app data usage
public class Notification implements INotification{

    private final Context context;
    private final PendingIntent pendingIntent;  // The intent which responds to a user tap
    private final NotificationManager manager;

    private static int count = 0;  // General counter for the notification
    private int id;  // A specific id for each notification

    private static final HashMap<String, Integer> NOTIFICATIONS = new HashMap<String, Integer>();


    public static final String TAG = Notification.class.getName();

    public Notification(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiverNotification.class);
        intent.setAction(NOTIFICATION_RECEIVER);
        id = count;
        pendingIntent = PendingIntent.getBroadcast(context, 0 , intent, 0);
    }

    @Override
    public int getId() {
        return id;
    }

    // A function to create new notification for the user about a parameter given app
    // that pass the used data threshold and returns the notification
    @Override
    public NotificationCompat.Builder createNewNotification(IProcess process, int dataUsageInGivenTimeInMb) {
        // Had to use android builder class and not create my own because for some reason its a final class
        // (meaning you can not extend it)
        String text = context.getString(R.string.notificationText, process.getAppName(), dataUsageInGivenTimeInMb, CHECK_TIME_THRESHOLD);
        String title = context.getString(R.string.notificationTitle);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert_white)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        if (NOTIFICATIONS.containsKey(process.getProcessName())) {
            managerCompat.notify(NOTIFICATIONS.get(process.getProcessName()), mBuilder.build());
        } else {
            managerCompat.notify(id, mBuilder.build());
            NOTIFICATIONS.put(process.getProcessName(), id);
            count++;
        }

        return mBuilder;
    }

    // A function to remove the notification
    @Override
    public void removeNotification() {
        if (manager != null) {
            manager.cancel(id);
        } else {
            Log.d(TAG, TAG + ": manager is null");
        }
    }

    // A static function to remove all notifications
    public static void removeAllNotifications(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        } else {
            Log.d(TAG, TAG + ": manager is null");
        }
    }

    // A static function for phones with version 26+ to create a notification channel
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channelName);
            String desc = context.getString(R.string.channelDesc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(desc);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static HashMap<String, Integer> getNotifications() {
        return NOTIFICATIONS;
    }
}
