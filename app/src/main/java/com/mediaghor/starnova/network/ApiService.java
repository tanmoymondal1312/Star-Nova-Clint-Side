package com.mediaghor.starnova.network;

// ApiService.java
import com.mediaghor.starnova.model.LoginRequest;
import com.mediaghor.starnova.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("accounts/api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // 🔥 Future endpoints example
    // @GET("quiz/list")
    // Call<QuizResponse> getQuizList();
}
