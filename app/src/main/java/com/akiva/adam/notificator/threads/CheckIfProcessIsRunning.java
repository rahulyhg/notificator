package com.akiva.adam.notificator.threads;

import android.app.ActivityManager;
import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;
import com.akiva.adam.notificator.classes.Process;
import com.akiva.adam.notificator.interfaces.IProcess;

import java.util.List;
import java.util.Timer;

import static com.akiva.adam.notificator.activities.MyActivity.SLEEP_TIME_FOR_APP_RUN_CHECK;

// An extension for Thread that checks the a specific process is still running while
// checking for data usage threshold
public class CheckIfProcessIsRunning extends Thread {

    private final Context context;
    private final IProcess process;  // The process which is being checked if it's still running
    private final ActivityManager activityManager;
    private final Timer timer;  // Timer to check the data usage threshold

    public static final String TAG = CheckIfProcessIsRunning.class.getName();

    public CheckIfProcessIsRunning(Context context, IProcess process, Timer timer) {
        this.context = context;
        this.process = process;
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        this.timer = timer;
    }

    @Override
    public void run() {
        while (true) {
            List<ActivityManager.RunningAppProcessInfo> runningApps = activityManager.getRunningAppProcesses();  // Get the running process at the current time
            boolean appFound = false;
            // iterate over the process and check if the one of the desired process was found
            for (final ActivityManager.RunningAppProcessInfo appInfo : runningApps) {
                // if it was break the loop
                if (appInfo.processName.contains("chrome")) {
                    appFound = true;
                    break;
                }
            }
            // if the process was found set the current data usage accordingly
            if (appFound) {
                process.setCurrentDataUsage(TrafficStats.getUidRxBytes(process.getUid()));
                Log.d(TAG, String.format(TAG + ": App used %d additional bytes in the last 5 seconds", process.getCurrentDataUsage() - process.getLastDataThreshold()));
                // if it was not found save the last known current data usage in the database for further use
                // as well as canceling the timer
            } else {
                process.setLastDataThreshold(process.getCurrentDataUsage());
                timer.cancel();
                break;
            }
            // Thread go to sleep for a given time and afterwards checks again
            try {
                Thread.sleep(SLEEP_TIME_FOR_APP_RUN_CHECK);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
