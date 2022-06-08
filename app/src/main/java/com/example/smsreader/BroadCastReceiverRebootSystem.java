package com.example.smsreader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadCastReceiverRebootSystem extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
           Intent serviceIntent = new Intent(context, ForegroundServiceSMS.class);
           context.startForegroundService(serviceIntent);
       }
    }
}