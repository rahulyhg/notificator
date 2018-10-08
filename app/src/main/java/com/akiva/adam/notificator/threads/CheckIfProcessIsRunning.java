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

public class CheckIfProcessIsRunning extends Thread {

    private final Context context;
    private final IProcess process;
    private final ActivityManager activityManager;
    private final Timer timer;

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
            List<ActivityManager.RunningAppProcessInfo> runningApps = activityManager.getRunningAppProcesses();
            boolean appFound = false;
            for (final ActivityManager.RunningAppProcessInfo appInfo : runningApps) {
                if (appInfo.processName.contains("chrome")) {
                    appFound = true;
                    break;
                }
            }
            if (appFound) {
                process.setCurrentDataUsage(TrafficStats.getUidRxBytes(process.getUid()));
                Log.d(TAG, String.format(TAG + ": App used %d additional bytes in the last 5 seconds", process.getCurrentDataUsage() - process.getLastDataThreshold()));
            } else {
                process.setLastDataThreshold(process.getCurrentDataUsage());
                timer.cancel();
                break;
            }
            try {
                Thread.sleep(SLEEP_TIME_FOR_APP_RUN_CHECK);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
