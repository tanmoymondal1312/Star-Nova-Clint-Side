package com.mediaghor.starnova.ui;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.mediaghor.starnova.R;
import com.mediaghor.starnova.model.LanguageInfo;
import com.mediaghor.starnova.model.UserDataModel;
import com.mediaghor.starnova.network.ApiService;
import com.mediaghor.starnova.network.RetrofitClient;
import com.mediaghor.starnova.repository.AuthTokenManager;
import com.mediaghor.starnova.repository.CompletionPreferenceManager;
import com.mediaghor.starnova.repository.UserDataRepository;
import com.mediaghor.starnova.repository.UserPreferenceManager;
import com.mediaghor.starnova.ui.Dialog.CustomDialog;
import com.mediaghor.starnova.ui.Dialog.LoadingDialog;
import com.mediaghor.starnova.ui.Fragments.FragmentAi;
import com.mediaghor.starnova.ui.Fragments.FragmentDailyTask;
import com.mediaghor.starnova.ui.Fragments.FragmentProfile;
import com.mediaghor.starnova.ui.util.BaseActivity;
import com.mediaghor.starnova.ui.util.LanguageManager;
import com.mediaghor.starnova.ui.util.LanguageUtils;
import com.mediaghor.starnova.ui.util.SystemBarUtils;
import com.nafis.bottomnavigation.NafisBottomNavigation;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private NafisBottomNavigation bottomNavigation;
    private AuthTokenManager tokenManager;
    private UserPreferenceManager userPref;
    private CompletionPreferenceManager completionPref;

    private CustomDialog customDialog;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initManagers();

        if (!checkAuth()) return;
        if (!checkUserCompletion()) return;

        bottomNavigationSetup();
        SystemBarUtils.setSystemBars(
                this,
                ContextCompat.getColor(this, R.color.transparent),
                ContextCompat.getColor(this, R.color.on_layout_bg),
                false
        );
    }

    private void initViews() {
        customDialog = new CustomDialog(this);
        loadingDialog = new LoadingDialog(this);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void initManagers() {
        tokenManager = new AuthTokenManager(this);
        userPref = new UserPreferenceManager(this);
        completionPref = new CompletionPreferenceManager(this);
    }

    private boolean checkAuth() {
        if (!tokenManager.hasToken()) {
            Log.w(TAG, "Token not found → Redirecting to Auth");
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
            return false;
        }
        return true;
    }

    private boolean checkUserCompletion() {
        if (!completionPref.isCompleted()) {
            Log.i(TAG, "User data not completed → Redirecting");
            startActivity(new Intent(this, ActivityGetUserData.class));
            finish();
            return false;
        }

        if (!userPref.isUserProfileComplete()) {
            Log.i(TAG, "Profile incomplete → Fetching from server");
            fetchAndCacheUserData();
        }

        return true;
    }

    private void fetchAndCacheUserData() {
        showLoading();

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        UserDataRepository repo = new UserDataRepository(apiService);

        repo.getUserData(tokenManager.getToken(), new UserDataRepository.UserDataCallback() {

            @Override
            public void onSuccess(UserDataModel userData) {
                Log.d(TAG, "API Success → Raw UserData received");

                if (userData == null) {
                    Log.e(TAG, "UserDataModel is NULL");
                    hideLoading();
                    return;
                }

                // Full JSON log
                Log.d(TAG, "UserData JSON → " + new Gson().toJson(userData));

                try {
                    Log.d(TAG, "Saving user data to SharedPreferences");

                    userPref.saveAllUserData(
                            userData.getLanguage(),
                            userData.getName(),
                            userData.getExperience_level(),
                            userData.getClassification(),
                            String.valueOf(userData.getAge())
                    );

                    Log.d(TAG, "User data saved successfully");

                    String savedLanguage = userPref.getUserLanguage();
                    Log.d(TAG, "Saved Language → " + savedLanguage);

                    LanguageInfo info = LanguageUtils.getLanguageInfo(savedLanguage);

                    if (info == null) {
                        Log.e(TAG, "LanguageInfo is NULL for language: " + savedLanguage);
                    } else {
                        LanguageManager.saveLanguage(MainActivity.this, info.getCode());
                        Log.d(TAG,
                                "Language Applied → Code: " + info.getCode()
                                        + ", Display: " + info.getDisplayName()
                                        + ", EN: " + info.getDisplayNameEnglish()
                        );
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Exception while caching user data", e);
                }

                hideLoading();
                Log.d(TAG, "Restarting activity to apply changes");
                restartSelf();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error → " + error);

                hideLoading();
                showErrorDialog(error);
            }
        });
    }

    private void bottomNavigationSetup() {
        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.ic_icon_daily_task));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.ic_ai_btn));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.ic_profile));

        bottomNavigation.setOnShowListener(model -> {
            Fragment fragment = null;

            if (model.getId() == 1) fragment = new FragmentDailyTask();
            else if (model.getId() == 2) fragment = new FragmentAi();
            else if (model.getId() == 3) fragment = new FragmentProfile();

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
            }
            return null;
        });

        bottomNavigation.show(2, true);
    }

    private void showLoading() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.start();
        }
    }

    private void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void showErrorDialog(String message) {
        if (customDialog == null) {
            customDialog = new CustomDialog(this);
        }

        customDialog.setTitle("Account Problem");
        customDialog.setMainImage(R.drawable.icon_sad);
        customDialog.setIcon(R.drawable.icon_info);
        customDialog.setDescription(message);

        if (!customDialog.isShowing()) {
            customDialog.show();
        }
    }

    private void restartSelf() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }
}
