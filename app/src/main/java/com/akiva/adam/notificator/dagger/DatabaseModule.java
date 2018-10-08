package com.akiva.adam.notificator.dagger;

import com.akiva.adam.notificator.classes.Database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

// Database module to provide a singleton database instance for usage in the classes specified in the component
@Module
public class DatabaseModule {

    @Provides
    @Singleton
    Database provideDatabase() {
        return new Database();
    }
}
