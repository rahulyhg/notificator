package com.akiva.adam.notificator.classes;

import com.akiva.adam.notificator.interfaces.IDatabase;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;
import javax.inject.Singleton;

// A singleton for the database instance that is being used in the entire application
@Singleton
public class Database implements IDatabase{
    private final FirebaseDatabase mDatabase;

    @Inject
    public Database() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public FirebaseDatabase getDatabaseService() {
        return mDatabase;
    }
}
