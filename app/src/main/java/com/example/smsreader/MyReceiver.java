package com.example.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    private static final String SMS = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    String msg = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Intent receive: "+intent.getAction());
        if (intent.getAction()==(SMS)){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                Object[] pdu = (Object[]) bundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[pdu.length];
                for (int i = 0; i < pdu.length ; i++){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        String format = bundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[]) pdu[i], format);
                    }else{
                        message[i] = SmsMessage.createFromPdu((byte[]) pdu[i]);
                    }
                    msg = message[i].getMessageBody();
                }
                //In msg we have text of the message
                Toast.makeText(context, "message: "+msg, Toast.LENGTH_LONG).show();

            }
        }
    }
}