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

import static com.akiva.adam.notificator.activities.MyActivity.CHECK_TIME_THRESHOLD;
import static com.akiva.adam.notificator.activities.MyActivity.NOTIFICATION_CHANNEL_ID;
import static com.akiva.adam.notificator.activities.MyActivity.NOTIFICATION_RECEIVER;

public class Notification implements INotification{

    private final Context context;
    private final Intent intent;
    private final PendingIntent pendingIntent;
    private final NotificationManager manager;

    private static int count = 0;
    private int id;


    public static final String TAG = Notification.class.getName();

    public Notification(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        id = count++;
        intent = new Intent(context, BroadcastReceiverNotification.class);
        intent.setAction(NOTIFICATION_RECEIVER);
        pendingIntent = PendingIntent.getBroadcast(context, 0 , intent, 0);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public NotificationCompat.Builder createNewNotification(IProcess process, int dataUsageInGivenTimeInMb) {
        // Had to use android builder class and not create my own because for some reason its a final class
        // (meaning you can not extend it)
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert_white)
                .setContentTitle(context.getString(R.string.notificationTitle))
                .setContentText(context.getString(R.string.notificationText, process.getAppName(), dataUsageInGivenTimeInMb, CHECK_TIME_THRESHOLD))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.notificationText, process.getAppName(), dataUsageInGivenTimeInMb, CHECK_TIME_THRESHOLD)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(id, mBuilder.build());

        return mBuilder;
    }

    @Override
    public void removeNotification() {
        if (manager != null) {
            manager.cancel(id);
        } else {
            Log.d(TAG, TAG + ": manager is null");
        }
    }

    public static void removeAllNotifications(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        } else {
            Log.d(TAG, TAG + ": manager is null");
        }
    }

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
}