package com.akiva.adam.notificator.interfaces;

import android.support.v4.app.NotificationCompat;

import com.akiva.adam.notificator.classes.Process;

public interface INotification {

    int getId();
    NotificationCompat.Builder createNewNotification(IProcess process, int dataUsage);
    void removeNotification();
}
