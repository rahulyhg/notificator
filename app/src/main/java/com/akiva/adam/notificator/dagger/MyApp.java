package com.akiva.adam.notificator.dagger;

import android.app.Application;

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
