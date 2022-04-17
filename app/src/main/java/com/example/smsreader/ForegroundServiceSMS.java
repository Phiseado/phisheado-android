package com.example.smsreader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smsreader.interfaces.ResultAPI;
import com.example.smsreader.models.CallAPI;
import com.example.smsreader.models.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForegroundServiceSMS extends Service {
    private static BroadcastReceiver br_SMSReceiver;

    private static final String SMS = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    String msg = "";
    private PendingIntent reportPhishingIntent;
    private PendingIntent reportNormalMessageIntent;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiverSMS();
        final String CHANNELID = "foreground_channel";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("SMS Reader")
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        startForeground(1001, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(br_SMSReceiver);
    }

    public void registerReceiverSMS() {
        br_SMSReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG,"Intent receive: "+intent.getAction());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    NotificationChannel channel = new NotificationChannel("My notification","My notification", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = context.getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                if (intent.getAction()==(SMS)){
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Object[] pdu = (Object[]) bundle.get("pdus");
                        final SmsMessage[] message = new SmsMessage[pdu.length];
                        for (int i = 0; i < pdu.length; i++) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                String format = bundle.getString("format");
                                message[i] = SmsMessage.createFromPdu((byte[]) pdu[i], format);
                            } else {
                                message[i] = SmsMessage.createFromPdu((byte[]) pdu[i]);
                            }
                            msg = message[i].getMessageBody();
                        }
                        //In msg we have text of the message
                        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://phishing-alert-backend.herokuapp.com/")
                                .addConverterFactory(GsonConverterFactory.create()).build();
                        ResultAPI api = retrofit.create(ResultAPI.class);
                        Call<Result> call = api.analyze(new CallAPI(msg));
                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                Result result = response.body();
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(),"My notification");
                                builder.setContentTitle("Resultado del análisis");
                                if (result.getResult()=="true"){
                                    builder.setContentText("¡Cuidado! podría tratarse de un mensaje malicioso");
                                    builder.addAction(R.drawable.ic_baseline_assignment_late_24, "Denunciar", reportPhishingIntent);
                                    builder.addAction(R.drawable.ic_baseline_assignment_late_24, "No es phishing", reportNormalMessageIntent);
                                }else{
                                    builder.setContentText("Este mensaje se considera seguro");
                                }
                                builder.setSmallIcon(R.drawable.ic_baseline_assignment_late_24);
                                builder.setAutoCancel(true);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
                                notificationManagerCompat.notify(1,builder.build());
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Log.e(TAG,t.getMessage());
                            }
                        });
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(br_SMSReceiver,intentFilter);

    }
}