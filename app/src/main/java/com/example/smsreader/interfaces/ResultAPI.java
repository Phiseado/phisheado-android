package com.example.smsreader.interfaces;

import com.example.smsreader.models.CallAPI;
import com.example.smsreader.models.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface ResultAPI {
    @POST("check/")
    Call<Result> analyze(@Body CallAPI message);
}
