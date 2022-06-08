package com.example.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.smsreader.interfaces.ResultAPI;
import com.example.smsreader.models.ReportMessage;
import com.example.smsreader.models.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NormalMessageReceiver extends BroadcastReceiver {

    public NormalMessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat.from(context).cancel(1002);

        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String isoCode = tMgr.getNetworkCountryIso();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://phishing-alert-backend.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        ResultAPI api = retrofit.create(ResultAPI.class);
        String message = intent.getStringExtra("message");

        Call<Result> call = api.reportMessage(new ReportMessage(message,isoCode, Boolean.FALSE));
        call.enqueue(new Callback<Result>() {

            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.i("STATUS","Message reported successfully");
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e("FAILURE",t.getMessage());
            }
        });
    }
}