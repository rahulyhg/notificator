package com.akiva.adam.notificator.classes;

import com.akiva.adam.notificator.interfaces.ILocks;

import javax.inject.Inject;
import javax.inject.Singleton;

// A singleton for a bunch of locks used to prevent race condition in different threads
@Singleton
public class Locks implements ILocks{

    private Boolean serviceLock;
    private Boolean appLock;

    @Inject
    public Locks() {
        serviceLock = false;
        appLock = false;
    }

    @Override
    public synchronized void setServiceLock(Boolean serviceLock) {
        this.serviceLock = serviceLock;
    }

    @Override
    public synchronized Boolean getServiceLock() {
        return serviceLock;
    }
}
