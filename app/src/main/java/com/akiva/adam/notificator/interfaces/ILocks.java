package com.akiva.adam.notificator.interfaces;

// An interface for usage instead of the class Lock to allow changes easily if they are needed
public interface ILocks {
    void setServiceLock(Boolean serviceLock);
    Boolean getServiceLock();

}
