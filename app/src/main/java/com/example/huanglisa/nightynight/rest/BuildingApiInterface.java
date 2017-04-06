package com.example.huanglisa.nightynight.rest;

import com.example.huanglisa.nightynight.ReceivedBuilding;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by huanglisa on 11/25/16.
 */

public interface BuildingApiInterface {
    @GET("building")
    Call<List<ReceivedBuilding>> getBuildings(@Header("x-zumo-auth") String authorization);

    @FormUrlEncoded
    @POST("building")
    Call<ReceivedBuilding> addBuilding(@Header("x-zumo-auth") String authorization, @Field("name") String name, @Field("index") int index);

    @FormUrlEncoded
    @POST("building/item")
    Call <ReceivedBuilding> getBuilding(@Header("x-zumo-auth") String authorization, @Field("id") String id);

}
