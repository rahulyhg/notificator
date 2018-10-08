package com.akiva.adam.notificator.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.akiva.adam.notificator.services.MainService;

// receiver used after reboot action to restart the service automatically
public class BroadcastReceiverService extends BroadcastReceiver {

    public static final String TAG = BroadcastReceiverService.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null ) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                Intent service = new Intent(context, MainService.class);
                context.startService(service);
                Log.d(TAG, TAG + ": Service started successfully");
            } else {
                Log.d(TAG, TAG + ": Intent was not ACTION_BOOT_COMPLETED");
            }
        } else {
            Log.d(TAG, TAG + ": intent action is null");
        }
    }
}
