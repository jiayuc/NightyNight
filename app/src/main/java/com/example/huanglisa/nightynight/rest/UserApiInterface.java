package com.example.huanglisa.nightynight.rest;

/**
 * Created by huanglisa on 11/13/16.
 */

import com.example.huanglisa.nightynight.ReceivedClock;
import com.example.huanglisa.nightynight.ReceivedFriend;
import com.example.huanglisa.nightynight.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;


public interface UserApiInterface {
    // native password login
    @FormUrlEncoded
    @POST("user")
    Call <User> userLogIn(@Field("email") String email, @Field("password") String password);

    // Facebook login API
    @FormUrlEncoded
    @POST("user/fb-access")
    Call<User> userLogInViaFacebook(@Field("id") String userId, @Field("token") String accessToken);

    // google login API
    @FormUrlEncoded
    @POST("user/gg-access")
    Call<User> userLogInViaGoogle(@Field("token") String token, @Field("email") String email);

    @PUT("user")
    Call <User> userSignUp(@Body User user);

    @GET("user")
    Call <User> getUserInfo(@Header("x-zumo-auth") String authorization);

    @GET("user/clock")
    Call <ReceivedClock> userGetActiveClock(@Header("x-zumo-auth") String authorization);

    @FormUrlEncoded
    @POST("user/friend")
    Call <ReceivedFriend> userGetFriend(@Header("x-zumo-auth") String authorization, @Field("friendId") String friendId);

    @FormUrlEncoded
    @PUT("user/clock")
    Call <ReceivedClock> userUpdateActiveClock(@Header("x-zumo-auth") String authorization, @Field("clock") String id);

    @FormUrlEncoded
    @PUT("user/status")
    Call <User> userUpdateStatus(@Header("x-zumo-auth") String authorization, @Field("status") boolean status);

    @FormUrlEncoded
    @PUT("user/address")
    Call <User> userUpdateAddress(@Header("x-zumo-auth") String authorization, @Field("address") String address);

    @FormUrlEncoded
    @PUT("user/phone")
    Call <User> userUpdatePhone(@Header("x-zumo-auth") String authorization, @Field("phone") String phone);

    @FormUrlEncoded
    @PUT("user/name")
    Call <User> userUpdateName(@Header("x-zumo-auth") String authorization, @Field("name") String name);

}
