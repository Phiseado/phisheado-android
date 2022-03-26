package com.example.smsreader;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationChannelCompat;
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

public class MyReceiver extends BroadcastReceiver {
    private static final String SMS = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    String msg = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Intent receive: "+intent.getAction());
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
                Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8000/")
                        .addConverterFactory(GsonConverterFactory.create()).build();
                ResultAPI api = retrofit.create(ResultAPI.class);
                Call<Result> call = api.analyze(new CallAPI(msg));
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Result result = response.body();
                        Toast.makeText(context, "Resultado del análisis: " + result.getResult(), Toast.LENGTH_LONG).show();
                        //NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(),"NOTIFICATION");
                        //builder.setSmallIcon(R.drawable.ic_baseline_assignment_late_24);
                        //builder.setContentTitle("Resultado del análisis");
                        //builder.setContentText(result.getResult());
                        //builder.setColor(Color.BLUE);

                        //NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
                        //notificationManagerCompat.notify(0,builder.build());
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG,t.getMessage());

                    }
                });
            }
        }
    }
}