package com.example.smsreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class PhishingReceiver extends BroadcastReceiver {

    public static String REPORT_PHISHING = "actionReportPhishing";

    public PhishingReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String country = tMgr.getNetworkCountryIso();
    }
}