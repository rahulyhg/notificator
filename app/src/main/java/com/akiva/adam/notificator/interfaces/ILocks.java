package com.akiva.adam.notificator.interfaces;

public interface ILocks {
    void setServiceLock(Boolean serviceLock);
    Boolean getServiceLock();

    void setThreadLock(Boolean threadLock);
    Boolean getThreadLock();

    void setAppThreadLock(Boolean appThreadLock);
    Boolean getAppThreadLock();
}
