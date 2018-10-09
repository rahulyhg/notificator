package com.akiva.adam.notificator.threads;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.util.Log;

import com.akiva.adam.notificator.classes.Process;
import com.akiva.adam.notificator.interfaces.IProcess;

import java.util.List;
import java.util.Timer;

import static com.akiva.adam.notificator.activities.MyActivity.GOOGLE_CHROME_PROCESS_NAME;
import static com.akiva.adam.notificator.activities.MyActivity.SLEEP_TIME_FOR_APP_RUN_CHECK;
import static com.akiva.adam.notificator.activities.MyActivity.YOUTUBE_PROCESS_NAME;
import static com.akiva.adam.notificator.activities.MyActivity.isSystemApp;

// An extension for Thread that checks the a specific process is still running while
// checking for data usage threshold
public class CheckIfProcessIsRunning implements Runnable {

    private final Context context;
    private final PackageManager packageManager;
    private final List<ApplicationInfo> appsInfo;
    private final IProcess process;  // The process which is being checked if it's still running
    private final ActivityManager activityManager;
    private final Timer timer;  // Timer to check the data usage threshold

    public static final String TAG = CheckIfProcessIsRunning.class.getName();

    public CheckIfProcessIsRunning(Context context, IProcess process, Timer timer) {
        this.context = context;
        this.process = process;
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        this.timer = timer;

        packageManager = context.getPackageManager();
        appsInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    @Override
    public void run() {
        while (true) {
            try {
                boolean appFound = false;
                // iterate over the process and check if the one of the desired process was found
                for (final ApplicationInfo appInfo : appsInfo) {
                    // if it was break the loop
                    if ((appInfo.processName.equals(GOOGLE_CHROME_PROCESS_NAME) || appInfo.processName.equals(YOUTUBE_PROCESS_NAME))
                            || !isSystemApp(appInfo)) {
                        appFound = true;
                        break;
                    }
                }
                // if the process was found set the current data usage accordingly
                if (appFound) {
                    process.setCurrentDataUsage(TrafficStats.getUidRxBytes(process.getUid()));
                    Log.d(TAG, String.format("%s: %s used %d additional bytes in the last 5 seconds", TAG, process.getAppName(), process.getCurrentDataUsage() - process.getLastDataThreshold()));
                    // if it was not found save the last known current data usage in the database for further use
                    // as well as canceling the timer
                } else {
                    process.setLastDataThreshold(process.getCurrentDataUsage());
                    timer.cancel();
                    Thread.interrupted();
                }
                // Thread go to sleep for a given time and afterwards checks again
                Thread.sleep(SLEEP_TIME_FOR_APP_RUN_CHECK);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}