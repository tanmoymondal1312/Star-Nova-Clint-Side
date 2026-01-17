package com.mediaghor.starnova.repository;

// UserSyncRepository.java

import android.content.Context;
import android.util.Log;

import com.mediaghor.starnova.model.UserSyncResponse;
import com.mediaghor.starnova.network.ApiService;
import com.mediaghor.starnova.network.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSyncRepository {

    private final UserPreferenceManager pref;
    private final ApiService api;

    public UserSyncRepository(Context context) {
        pref = new UserPreferenceManager(context);
        api = RetrofitClient.getClient().create(ApiService.class);
    }

    public void saveAndSyncUser(
            String token,
            String language,
            String name,
            String experience,
            String userClass,
            String age
    ) {

        // ✅ 1. Save locally first (offline safe)
        pref.saveAllUserData(language, name, experience, userClass, age);

        // ✅ 2. Prepare server payload
        Map<String, Object> body = new HashMap<>();
        body.put("language", language);
        body.put("name", name);
        body.put("experience_level", experience);
        body.put("classification", userClass);
        body.put("age", age);

        // ✅ 3. Sync with server
        api.syncUserData("Token " + token, body)
                .enqueue(new Callback<UserSyncResponse>() {
                    @Override
                    public void onResponse(
                            Call<UserSyncResponse> call,
                            Response<UserSyncResponse> response
                    ) {

                        if (response.isSuccessful() && response.body() != null) {

                            UserSyncResponse.UserData serverData =
                                    response.body().user_data;

                            // 🔁 4. Re-sync local with server response
                            pref.saveAllUserData(
                                    serverData.language,
                                    serverData.name,
                                    serverData.experience_level,
                                    serverData.classification,
                                    String.valueOf(serverData.age)
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<UserSyncResponse> call, Throwable t) {
                        Log.e("SYNC", "Server sync failed, will retry later", t);
                    }
                });
    }
}

