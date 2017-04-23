package com.example.huanglisa.nightynight.rest;

/**
 * Created by huanglisa on 11/15/16.
 */

import com.example.huanglisa.nightynight.models.ReceivedClock;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ClockApiInterface {
    @FormUrlEncoded
    @POST("clock")
    Call<ReceivedClock> addClock(@Header("x-zumo-auth") String authorization, @Field("sleepHour") int sleepHour, @Field("sleepMin") int sleepMin, @Field("wakeHour") int wakeHour, @Field("wakeMin") int wakeMin);

    @GET("clock")
    Call<List<ReceivedClock>> getUserClocks(@Header("x-zumo-auth") String authorization);

    @DELETE("clock/{id}")
    Call<ReceivedClock> removeClock(@Header("x-zumo-auth") String authorization, @Path("id") String id);
}
