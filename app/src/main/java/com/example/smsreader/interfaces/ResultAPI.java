package com.example.smsreader.interfaces;

import com.example.smsreader.models.CheckMessage;
import com.example.smsreader.models.ReportMessage;
import com.example.smsreader.models.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ResultAPI {
    @POST("check/")
    Call<Result> analyze(@Body CheckMessage message);

    @POST("report-message/")
    Call<Result> reportMessage(@Body ReportMessage body);

}
