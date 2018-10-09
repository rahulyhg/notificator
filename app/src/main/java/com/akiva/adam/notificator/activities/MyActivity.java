package com.akiva.adam.notificator.activities;

import android.content.pm.ApplicationInfo;
import android.support.v7.app.AppCompatActivity;

// Base activity used for functions that all activities can use as well as defined strings for the entire program
public class MyActivity extends AppCompatActivity {

    public static final String NOTIFICATION_RECEIVER = "notificationService";
    public static final String NOTIFICATION_CHANNEL_ID = "9991";
    public static final int THRESHOLD_FOR_DATA_USAGE = 2000000;  // bytes
    public static final int SLEEP_TIME_FOR_APP_DATA_USAGE_CHECK = 15000;
    public static final int SLEEP_TIME_FOR_WIFI_CHECK_CONNECTION = 30000;  // milliseconds
    public static final int SLEEP_TIME_FOR_APP_RUN_CHECK = 5000;  // milliseconds
    public static final int TIMER_TASK_CHECK_RATE = 120000;  // milliseconds
    public static final int CHECK_TIME_THRESHOLD = 1;  // minutes

    public static final int ONE_MEGA_BYTE_IN_BYTES = 1000000;

    public static final String GOOGLE_CHROME_PROCESS_NAME = "com.android.chrome";
    public static final String YOUTUBE_PROCESS_NAME = "com.android.youtube";

    public static boolean isAppStopped(ApplicationInfo appInfo) {
        return ((appInfo.flags & ApplicationInfo.FLAG_STOPPED) != 0);
    }

    public static boolean isSystemApp(ApplicationInfo appInfo) {
        return ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
}