package com.akiva.adam.notificator.interfaces;

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
