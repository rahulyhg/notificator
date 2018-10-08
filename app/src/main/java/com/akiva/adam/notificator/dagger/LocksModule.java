package com.akiva.adam.notificator.dagger;

import com.akiva.adam.notificator.classes.Locks;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

// Locks module to provide a singleton locks instance for usage in the classes specified in the component
@Module
public class LocksModule {

    @Provides
    @Singleton
    Locks provideLocks() {
        return new Locks();
    }
}
