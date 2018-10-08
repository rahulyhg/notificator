package com.akiva.adam.notificator.interfaces;

import com.google.firebase.database.FirebaseDatabase;

// An interface for usage instead of the class Database to allow changes easily if they are needed
public interface IDatabase {
    FirebaseDatabase getDatabaseService();
}
