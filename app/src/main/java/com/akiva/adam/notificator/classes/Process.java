package com.akiva.adam.notificator.classes;

import android.net.TrafficStats;
import android.support.annotation.NonNull;
import android.util.Log;

import com.akiva.adam.notificator.interfaces.IProcess;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import static com.akiva.adam.notificator.activities.MyActivity.GOOGLE_CHROME;
import static com.akiva.adam.notificator.activities.MyActivity.UNDEFINED;

public class Process implements IProcess {

    private final String uuid;  // unique id for device every reboot
    private final int uid; // process id
    private final String processName; // process name
    private String appName;
    private Long currentDataUsage;  // process current data usage
    private Long lastDataThreshold;  // last known process data usage (from database)
    private final DatabaseReference mDatabaseRef; // ref to the database

    public static final String TAG = Process.class.getName();

    public Process(@NonNull Database database, @NonNull final String uuid, final int uid, @NonNull final String processName) {
        this.uuid = uuid;
        this.uid = uid;
        this.processName = processName.replaceAll("\\.", "-");
        mDatabaseRef = database.getDatabaseService().getReference().child(uuid).child(this.processName);
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
            return (TrafficStats.getUidRxBytes(uid));
        }
        return lastDataThreshold;
    }

    private void getLastDataThresholdFromDatabase() {
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lastDataThreshold = dataSnapshot.getValue(long.class);
                } else {
                    lastDataThreshold = (TrafficStats.getUidRxBytes(uid));
                    mDatabaseRef.setValue(lastDataThreshold);
                    Log.d(TAG, String.format(TAG, ": %s node does not exist", processName));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                lastDataThreshold = TrafficStats.getUidRxBytes(uid);
                Log.d(TAG, TAG + ": " + databaseError.getMessage());
            }
        });
    }

    private void checkAppName(String processName) {
        switch (processName) {
            case "com.android.chrome":
                appName = GOOGLE_CHROME;
                break;
            default:
                appName = UNDEFINED;
                break;
        }
    }
}
