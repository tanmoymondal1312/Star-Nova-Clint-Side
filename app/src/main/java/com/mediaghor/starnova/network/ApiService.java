package com.mediaghor.starnova.network;

// ApiService.java
import com.mediaghor.starnova.model.LoginRequest;
import com.mediaghor.starnova.model.LoginResponse;
import com.mediaghor.starnova.model.UserSyncResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("accounts/api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("accounts/api/get-set-user-data")
    Call<UserSyncResponse> syncUserData(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );



    // 🔥 Future endpoints example
    // @GET("quiz/list")
    // Call<QuizResponse> getQuizList();
}
