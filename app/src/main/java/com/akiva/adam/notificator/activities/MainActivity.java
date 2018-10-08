package com.akiva.adam.notificator.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.akiva.adam.notificator.classes.Database;
import com.akiva.adam.notificator.classes.Locks;
import com.akiva.adam.notificator.dagger.MyApp;
import com.akiva.adam.notificator.services.MainService;
import com.akiva.adam.notificator.R;

import javax.inject.Inject;

public class MainActivity extends MyActivity {

    // Singleton for the database
    @Inject
    public Database mDatabase;

    // Singleton for the locks
    @Inject
    public Locks mLocks;

    private ScrollView scSettings;
    private RelativeLayout rlSettings;
    private LinearLayout llInternetConnection;
    private TextView tvInternetConnection;
    private Switch swInternetConnection;

    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApp) getApplicationContext()).getDatabaseComponent().inject(this);  // Injection of the database and locks singletons

        setContentView(R.layout.activity_main);

        scSettings = (ScrollView) findViewById(R.id.svSettings);
        rlSettings = (RelativeLayout) findViewById(R.id.rlSettings);
        llInternetConnection = (LinearLayout) findViewById(R.id.llInternetConnection);
        tvInternetConnection = (TextView) findViewById(R.id.tvInternetConnection);
        swInternetConnection = (Switch) findViewById(R.id.swInternetConnection);

        swInternetConnection.setChecked(true);  // The base value of the switch in on

        // checks if the service is not online and if not activate it
        if (!mLocks.getServiceLock()) {
            Log.d(TAG, TAG + ": Starting service");
            Intent intent = new Intent(this, MainService.class);
            // if the build is 26 and above need to be created as a foreground service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }
}
