package com.akiva.adam.notificator.activities;

import android.support.v7.app.AppCompatActivity;

public class MyActivity extends AppCompatActivity {

    public static final String NOTIFICATION_RECEIVER = "notificationService";
    public static final String NOTIFICATION_CHANNEL_ID = "9991";
    public static final String GOOGLE_CHROME = "Google Chrome";
    public static final String UNDEFINED = "Undefined";
    public static final int THRESHOLD_FOR_DATA_USAGE = 2000000;  // bytes
    public static final int SLEEP_TIME_FOR_WIFI_CHECK_CONNECTION = 30000;  // milliseconds
    public static final int SLEEP_TIME_FOR_APP_RUN_CHECK = 5000;  // milliseconds
    public static final int TIMER_TASK_CHECK_RATE = 120000;  // milliseconds
    public static final int CHECK_TIME_THRESHOLD = 2;  // minutes

}
