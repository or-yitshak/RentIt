package com.example.rentmystuff.interfaces;

import com.example.rentmystuff.login.RegisterActivity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FastApi {

    @POST("/create-user/")
    Call<String> createUser(@Body RegisterActivity.CreateUser user);
}
