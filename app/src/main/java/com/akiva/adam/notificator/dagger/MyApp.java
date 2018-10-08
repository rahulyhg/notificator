package com.akiva.adam.notificator.dagger;

import android.app.Application;

// An extension of the base application class used to initialize the database component and the creation of the instances
// for usage in the specified classes in the component inject functions
public class MyApp extends Application {

    private DatabaseComponent databaseComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseComponent = DaggerDatabaseComponent.builder()
                .databaseModule(new DatabaseModule())
                .locksModule(new LocksModule())
                .build();
    }

    public DatabaseComponent getDatabaseComponent() {
        return databaseComponent;
    }
}
