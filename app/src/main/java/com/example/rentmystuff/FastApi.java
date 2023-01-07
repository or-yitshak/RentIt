package com.example.rentmystuff;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FastApi {

    @FormUrlEncoded
    @POST("/create-user/")
    Call<ResponseBody> createUser(@FieldMap Map<String,String> fields);

    @POST("/create-user/")
    Call<RegisterActivity.CreateUser> createUser(@Body RegisterActivity.CreateUser user);

    @POST("/create-user/")
    Call<User> createUser2(@Body User user);
}
