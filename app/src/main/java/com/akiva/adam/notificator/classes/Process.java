package com.akiva.adam.notificator.classes;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.support.annotation.NonNull;
import android.util.Log;

import com.akiva.adam.notificator.interfaces.IProcess;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// A class the represent an active process in the phone
public class Process implements IProcess {

    private final String uuid;  // unique id for device every reboot
    private final int uid; // process id
    private final String processName; // process name
    private String appName;
    private Long currentDataUsage;  // process current data usage
    private Long lastDataThreshold;  // last known process data usage (from database)
    private final DatabaseReference mDatabaseRef; // ref to the database
    private final PackageManager packageManager;
    private final List<ApplicationInfo> appsInfo;

    public static final String TAG = Process.class.getName();

    public Process(Context context, @NonNull Database database, @NonNull final String uuid, final int uid, @NonNull final String processName) {
        packageManager = context.getPackageManager();
        appsInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        this.uuid = uuid;
        this.uid = uid;
        this.processName = processName;
        mDatabaseRef = database.getDatabaseService().getReference().child(uuid).child(processName.replaceAll("\\.", "-"));
        getLastDataThresholdFromDatabase();
        currentDataUsage = TrafficStats.getUidRxBytes(uid);
        checkAppName(processName);
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public String getProcessName() {
        return processName;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    @Override
    public void setCurrentDataUsage(long currentDataUsageInKb) {
        this.currentDataUsage = currentDataUsageInKb;
    }

    @Override
    public long getCurrentDataUsage() {
        return currentDataUsage;
    }

    @Override
    public void setLastDataThreshold(long lastDataThresholdInKb) {
        this.lastDataThreshold = lastDataThresholdInKb;
        mDatabaseRef.setValue(lastDataThreshold);
    }

    @Override
    public long getLastDataThreshold() {
        if (lastDataThreshold == null) {
            lastDataThreshold = TrafficStats.getUidRxBytes(uid);
            mDatabaseRef.setValue(lastDataThreshold);
        }
        return lastDataThreshold;
    }

    private void getLastDataThresholdFromDatabase() {
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lastDataThreshold = dataSnapshot.getValue(long.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                lastDataThreshold = TrafficStats.getUidRxBytes(uid);
                Log.d(TAG, TAG + ": " + databaseError.getMessage());
            }
        });
    }

    // A function used only by the class itself to determine  a user readable app name for the notification
    private void checkAppName(String processName) {
        for (ApplicationInfo appInfo : appsInfo) {
            if (processName.equals(appInfo.processName)) {
                appName = appInfo.loadLabel(packageManager).toString();
            }
        }
    }
}