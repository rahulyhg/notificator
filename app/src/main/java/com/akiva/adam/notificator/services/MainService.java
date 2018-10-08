package com.akiva.adam.notificator.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

import static com.akiva.adam.notificator.activities.MyActivity.SLEEP_TIME_FOR_WIFI_CHECK_CONNECTION;
import static com.akiva.adam.notificator.activities.MyActivity.TIMER_TASK_CHECK_RATE;

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

        uniqueId = UUID.randomUUID().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.createNotificationChannel(this);
            //startForeground();
        }

        Thread mainProcess = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!mLocks.getThreadLock() && !checkIfWifiIsConnected()) {
                        mLocks.setServiceLock(true);
                        getAppDataUsage();
                        break;
                    } else {
                        try {
                            Thread.sleep(SLEEP_TIME_FOR_WIFI_CHECK_CONNECTION);
                        } catch (InterruptedException e) {
                            mLocks.setThreadLock(false);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });
        if (!mLocks.getServiceLock()) {
            mLocks.setServiceLock(true);
            mainProcess.start();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocks.setServiceLock(false);
        if (uniqueId != null) {
            mDatabase.getDatabaseService().getReference().child(uniqueId).removeValue();
        }
    }

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

    public void getAppDataUsage() {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningAppProcessInfo> appsInfos = manager.getRunningAppProcesses();
            for (final ActivityManager.RunningAppProcessInfo appInfo : appsInfos) {
                if (uniqueId != null) {
                    if (appInfo.processName.equals("com.android.chrome")) {
                        final IProcess process = new Process(mDatabase, uniqueId, appInfo.uid, appInfo.processName);
                        mLocks.setThreadLock(true);
                        final Timer timer = new Timer();
                        CheckIfProcessIsRunning checkIfProcessIsRunning = new CheckIfProcessIsRunning(this, process, timer);
                        CheckDataUsageThreshold checkThreshold = new CheckDataUsageThreshold(this, process);
                        timer.scheduleAtFixedRate(checkThreshold, TIMER_TASK_CHECK_RATE, TIMER_TASK_CHECK_RATE);
                        checkIfProcessIsRunning.run();
                    }
                } else {
                    Log.d(TAG, TAG + ": uniqueId is null");
                }
            }
        } else {
            Log.d(TAG, TAG + ": Could not get Activity manager");
        }
    }
}
