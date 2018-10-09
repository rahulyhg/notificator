package com.akiva.adam.notificator.classes;

import android.content.Context;
import android.util.Log;

import com.akiva.adam.notificator.interfaces.IProcess;

import java.util.TimerTask;

import static com.akiva.adam.notificator.activities.MyActivity.CHECK_TIME_THRESHOLD;
import static com.akiva.adam.notificator.activities.MyActivity.ONE_MEGA_BYTE_IN_BYTES;
import static com.akiva.adam.notificator.activities.MyActivity.THRESHOLD_FOR_DATA_USAGE;

// Extension of timer task that checks every given time if the data usage threshold has been reached and if it was send user notification
public class CheckDataUsageThreshold extends TimerTask {

    private final Context context;
    private final IProcess process;  // the process which we want to check the data usage threshold

    public static final String TAG = CheckDataUsageThreshold.class.getName();

    public CheckDataUsageThreshold(Context context, IProcess processToCheck) {
        this.context = context;
        process = processToCheck;
    }

    @Override
    public void run() {
//        if (process.getCurrentDataUsage() - process.getLastDataThreshold() >= THRESHOLD_FOR_DATA_USAGE) {
//            int dataUsageInGivenTimeInMb = (int) ((process.getCurrentDataUsage() - process.getLastDataThreshold()) / 1000000);  // bytes to mb
//            Notification notification = new Notification(context);
//            notification.createNewNotification(process, dataUsageInGivenTimeInMb);
//            Log.d(TAG, String.format(TAG + ": App used more than %d mb in the last %d minutes", (THRESHOLD_FOR_DATA_USAGE / 100000), CHECK_TIME_THRESHOLD));
//        } else {
//            Log.d(TAG, String.format(TAG + ": App did not use above %d mb over %d minutes", (THRESHOLD_FOR_DATA_USAGE / 100000), CHECK_TIME_THRESHOLD));
//        }
//        process.setLastDataThreshold(process.getCurrentDataUsage());

        if (process.getCurrentDataUsage() - process.getLastDataThreshold() > ONE_MEGA_BYTE_IN_BYTES) {
            int dataUsageInGivenTimeInMb = (int) ((process.getCurrentDataUsage() - process.getLastDataThreshold()) / ONE_MEGA_BYTE_IN_BYTES);
            Notification notification = new Notification(context);
            notification.createNewNotification(process, dataUsageInGivenTimeInMb);
        }
        Log.d(TAG, String.format("%s: %s used %d mb in the last %d minutes", TAG, process.getAppName(),
                THRESHOLD_FOR_DATA_USAGE / 100000, CHECK_TIME_THRESHOLD));
        process.setLastDataThreshold(process.getCurrentDataUsage());
    }
}