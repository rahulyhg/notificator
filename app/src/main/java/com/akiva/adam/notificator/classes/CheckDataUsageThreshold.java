package com.akiva.adam.notificator.classes;

import android.content.Context;
import android.util.Log;

import com.akiva.adam.notificator.interfaces.IProcess;

import java.util.TimerTask;

import static com.akiva.adam.notificator.activities.MyActivity.CHECK_TIME_THRESHOLD;
import static com.akiva.adam.notificator.activities.MyActivity.THRESHOLD_FOR_DATA_USAGE;

public class CheckDataUsageThreshold extends TimerTask {

    private final Context context;
    private final IProcess process;

    public static final String TAG = CheckDataUsageThreshold.class.getName();

    public CheckDataUsageThreshold(Context context, IProcess processToCheck) {
        this.context = context;
        process = processToCheck;
    }

    @Override
    public void run() {
        if (process.getCurrentDataUsage() - process.getLastDataThreshold() >= THRESHOLD_FOR_DATA_USAGE) {
            int dataUsageInGivenTimeInMb = (int) ((process.getCurrentDataUsage() - process.getLastDataThreshold()) / 1000000);
            Notification notification = new Notification(context);
            notification.createNewNotification(process, dataUsageInGivenTimeInMb);
            Log.d(TAG, String.format(TAG + ": App used more than %d mb in the last %d minutes", (THRESHOLD_FOR_DATA_USAGE / 100000), CHECK_TIME_THRESHOLD));
        } else {
            Log.d(TAG, String.format(TAG + ": App did not use above %d mb over %d minutes", (THRESHOLD_FOR_DATA_USAGE / 100000), CHECK_TIME_THRESHOLD));
        }
    }
}
