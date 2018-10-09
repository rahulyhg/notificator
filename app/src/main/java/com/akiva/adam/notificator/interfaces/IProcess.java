package com.akiva.adam.notificator.interfaces;

// An interface for usage instead of the class Process to allow changes easily if they are needed
public interface IProcess {
    String getUuid();

    int getUid();

    String getProcessName();

    String getAppName();

    void setCurrentDataUsage(long currentDataUsage);
    long getCurrentDataUsage();

    void setLastDataThreshold(long lastDataThreshold);
    long getLastDataThreshold();
}