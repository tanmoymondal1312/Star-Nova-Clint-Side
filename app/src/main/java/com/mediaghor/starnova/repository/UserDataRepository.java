package com.mediaghor.starnova.repository;

import androidx.annotation.NonNull;

import com.mediaghor.starnova.model.UserDataModel;
import com.mediaghor.starnova.model.UserDataRequest;
import com.mediaghor.starnova.model.UserDataResponse;
import com.mediaghor.starnova.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataRepository {

    private final ApiService apiService;

    public UserDataRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    // =========================
    // 1️⃣ SAVE / UPDATE USER DATA
    // =========================
    public void saveUserData(
            String token,
            UserDataRequest request,
            UserDataCallback callback
    ) {

        apiService.getAndSetUserData("Token " + token, request)
                .enqueue(new Callback<UserDataResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserDataResponse> call,
                                           @NonNull Response<UserDataResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().getUser_data());
                        } else {
                            callback.onError("Failed to save user data");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserDataResponse> call,
                                          @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    // =========================
    // 2️⃣ GET USER DATA (TOKEN ONLY)
    // =========================
    public void getUserData(
            String token,
            UserDataCallback callback
    ) {

        apiService.getUserData("Token " + token)
                .enqueue(new Callback<UserDataResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserDataResponse> call,
                                           @NonNull Response<UserDataResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().getUser_data());
                        } else {
                            callback.onError("Failed to fetch user data");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserDataResponse> call,
                                          @NonNull Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    // =========================
    // CALLBACK
    // =========================
    public interface UserDataCallback {
        void onSuccess(UserDataModel userData);
        void onError(String error);
    }
}
