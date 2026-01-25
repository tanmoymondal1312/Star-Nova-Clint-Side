package com.mediaghor.starnova.network;

// ApiService.java
import com.mediaghor.starnova.model.LoginRequest;
import com.mediaghor.starnova.model.LoginResponse;
import com.mediaghor.starnova.model.UserData;
import com.mediaghor.starnova.model.UserDataRequest;
import com.mediaghor.starnova.model.UserDataResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("accounts/api/login")
    Call<LoginResponse> login(@Body LoginRequest request);


    // Get & set user data (Token required)
    @POST("accounts/api/get-set-user-data")
    Call<UserDataResponse> getAndSetUserData(
            @Header("Authorization") String token,
            @Body UserDataRequest request
    );

    // For token-only call (no body)
    @POST("accounts/api/get-set-user-data")
    Call<UserDataResponse> getUserData(
            @Header("Authorization") String token
    );



}
