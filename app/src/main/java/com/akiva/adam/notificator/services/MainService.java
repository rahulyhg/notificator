package com.akiva.adam.notificator.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.akiva.adam.notificator.classes.CheckDataUsageThreshold;
import com.akiva.adam.notificator.classes.Database;
import com.akiva.adam.notificator.classes.Locks;
import com.akiva.adam.notificator.classes.Notification;
import com.akiva.adam.notificator.classes.Process;
import com.akiva.adam.notificator.dagger.MyApp;
import com.akiva.adam.notificator.interfaces.IProcess;
import com.akiva.adam.notificator.threads.CheckIfProcessIsRunning;

import java.util.List;
import java.util.Timer;
import java.util.UUID;

import javax.inject.Inject;

import static com.akiva.adam.notificator.activities.MyActivity.GOOGLE_CHROME_PROCESS_NAME;
import static com.akiva.adam.notificator.activities.MyActivity.SLEEP_TIME_FOR_APP_DATA_USAGE_CHECK;
import static com.akiva.adam.notificator.activities.MyActivity.SLEEP_TIME_FOR_WIFI_CHECK_CONNECTION;
import static com.akiva.adam.notificator.activities.MyActivity.TIMER_TASK_CHECK_RATE;
import static com.akiva.adam.notificator.activities.MyActivity.YOUTUBE_PROCESS_NAME;
import static com.akiva.adam.notificator.activities.MyActivity.isAppStopped;
import static com.akiva.adam.notificator.activities.MyActivity.isSystemApp;

// A service that checks the data usage of indicated apps and if they pass
// a given mobile data threshold display a notification for the user
public class MainService extends Service {

    @Inject
    public Database mDatabase;

    @Inject
    public Locks mLocks;

    private String uniqueId;

    public static final String TAG = MainService.class.getName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ((MyApp) getApplicationContext()).getDatabaseComponent().inject(this);

        // get a unique id each reboot or whenever the service is destroyed
        // to used the data used by a program in a database
        uniqueId = UUID.randomUUID().toString();

        // If the phone version is >= 26 create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.createNotificationChannel(this);
            //startForeground();
        }

        // A thread which checks that checks it runs only once and that the wifi is offline
        // otherwise sleep for a given time until the next check if wifi is offline and when it is
        // get the process data usage, otherwise
        Thread mainProcess = new Thread(new Runnable() {
            @Override
            public void run() {
                mLocks.setServiceLock(true);
                while (true) {
                    try {
                        if (!checkIfWifiIsConnected()) {
                            getAppDataUsage();
                            Thread.sleep(SLEEP_TIME_FOR_APP_DATA_USAGE_CHECK);
                        } else {
                            Thread.sleep(SLEEP_TIME_FOR_WIFI_CHECK_CONNECTION);
                        }
                    } catch (InterruptedException e) {
                        Log.d(TAG, TAG + ": " + e.getMessage());
                        mLocks.setServiceLock(false);
                        return;
                    }
                }
            }
        });
        if (!mLocks.getServiceLock()) {
            mainProcess.start();
        }
        return START_STICKY;
    }

    // No binder for this service
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // when the service is destroyed release the lock and delete the unique id related information
    // NOTE: this does not work when the service is closed by operation system meaning I need to find a better
    // and more reliable way to do this (maybe a daily check for unused values and delete them?)
    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocks.setServiceLock(false);
        if (uniqueId != null) {
            mDatabase.getDatabaseService().getReference().child(uniqueId).removeValue();
        }
    }

    // checks if the wifi is offline and if it does allow the service to continue
    // otherwise sleep until the next check (as of now its always false for testing purposes)
    public boolean checkIfWifiIsConnected() {
//        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (manager != null) {
//            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            if (wifi != null) {
//                if (wifi.isConnected()) {
//                    Log.d(TAG, TAG + ": Wifi is connected");
//                    return true;
//                } else {
//                    Log.d(TAG, TAG + ": wifi is not connected to any network");
//                }
//            } else {
//                Log.d(TAG, TAG + ": wifi is offline");
//            }
//        } else {
//            Log.d(TAG, TAG + ": could not get ConnectivityManager");
//        }
        return false;
    }

    // A function that checks if given process are running and if so create a specific Process instance for it,
    // timer, thread to check it is still running and a timer task to display the notification to the user
    // after a given time
    public void getAppDataUsage() {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> appsInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        if (manager != null) {
            for (ApplicationInfo appInfo : appsInfo) {
                if (!isAppStopped(appInfo)) {
                    if ((appInfo.processName.equals(GOOGLE_CHROME_PROCESS_NAME) || appInfo.processName.equals(YOUTUBE_PROCESS_NAME))
                            || !isSystemApp(appInfo)) {
                        final IProcess process = new Process(this, mDatabase, uniqueId, appInfo.uid, appInfo.processName);
                        final Timer timer = new Timer();
                        CheckIfProcessIsRunning checkIfProcessIsRunning = new CheckIfProcessIsRunning(this, process, timer);
                        CheckDataUsageThreshold checkThreshold = new CheckDataUsageThreshold(this, process);
                        timer.scheduleAtFixedRate(checkThreshold, TIMER_TASK_CHECK_RATE, TIMER_TASK_CHECK_RATE);
                        new Thread(checkIfProcessIsRunning).start();
                    }
                }
            }
        } else {
            Log.d(TAG, TAG + ": uniqueId is null");
        }
    }
}