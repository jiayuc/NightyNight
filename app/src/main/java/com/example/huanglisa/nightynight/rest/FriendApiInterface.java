package com.example.huanglisa.nightynight.rest;

import com.example.huanglisa.nightynight.models.ReceivedFriend;
import com.example.huanglisa.nightynight.models.User;

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

public interface FriendApiInterface {
    @GET("friend")
    Call<List<ReceivedFriend>> getFriends(@Header("x-zumo-auth") String authorization);

    //TODO: ADD AUTH
    @FormUrlEncoded
    @POST("friend/individual")
    Call<User> getFriend(@Field("friendId") String friendId);

    @FormUrlEncoded
    @POST("friend")
    Call<ReceivedFriend> addFriend(@Header("x-zumo-auth") String authorization, @Field("buildingId") String buildingId, @Field("friendId") String friendId);

    @FormUrlEncoded
    @POST("friend/building")
    Call<List<ReceivedFriend>> getFriendInBuilding(@Header("x-zumo-auth") String authorization, @Field("buildingId") String buildingId);
}
