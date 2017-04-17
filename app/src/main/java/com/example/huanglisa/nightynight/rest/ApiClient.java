package com.example.huanglisa.nightynight.rest;

/**
 * Created by huanglisa on 11/12/16.
 */
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // base path for rest api
    public static final String BASE_URL = "http://nighty-night.azurewebsites.net/api/";
    private static Retrofit retrofit = null;

    private static OkHttpClient getRequestHeader() {
        OkHttpClient client = new OkHttpClient();
        OkHttpClient clientWith30sTimeout = client.newBuilder()
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS)
                .build();
        return clientWith30sTimeout;
    }


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getRequestHeader())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
