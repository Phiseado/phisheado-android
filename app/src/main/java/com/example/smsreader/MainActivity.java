package com.example.smsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUESTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isForegroundServiceRunning()){
            Intent backgroundService = new Intent(this, ForegroundServiceSMS.class);
            startForegroundService(backgroundService);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUESTS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Gracias por permitir el acceso", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Vaya, no puedo funcionar sin permisos", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("MainActivity", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.e("MainActivity", "onPause");
        super.onPause();
    }

    public boolean isForegroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundServiceSMS.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}