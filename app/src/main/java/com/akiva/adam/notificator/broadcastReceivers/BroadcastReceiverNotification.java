package com.akiva.adam.notificator.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import static com.akiva.adam.notificator.activities.MyActivity.NOTIFICATION_RECEIVER;

public class BroadcastReceiverNotification extends BroadcastReceiver {

    public static final String TAG = BroadcastReceiverNotification.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(NOTIFICATION_RECEIVER)) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    wifiManager.setWifiEnabled(true);
                }
            }
        }
    }
}
