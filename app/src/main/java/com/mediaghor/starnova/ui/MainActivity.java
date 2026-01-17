package com.mediaghor.starnova.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mediaghor.starnova.R;
import com.mediaghor.starnova.repository.AuthTokenManager;
import com.mediaghor.starnova.repository.UserPreferenceManager;
import com.mediaghor.starnova.ui.Dialog.CustomDialog;
import com.mediaghor.starnova.ui.Fragments.FragmentAi;
import com.mediaghor.starnova.ui.Fragments.FragmentDailyTask;
import com.mediaghor.starnova.ui.Fragments.FragmentProfile;
import com.mediaghor.starnova.ui.util.SystemBarUtils;
import com.nafis.bottomnavigation.NafisBottomNavigation;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    NafisBottomNavigation nafisBottomNavigation;
    private AuthTokenManager tokenManager;
    CustomDialog customDialog;

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
        tokenManager = new AuthTokenManager(this);
        UserPreferenceManager pref = new UserPreferenceManager(this);

//        if (!tokenManager.hasToken()) {
//            startActivity(new Intent(this, AuthenticationActivity.class));
//            finish();
//            return; // Stop executing further code
//        }

        if (!pref.isUserProfileComplete()) {
            startActivity(new Intent(this, ActivityGetUserData.class));
        }




        bottomNavigation();
        SystemBarUtils.setSystemBars(this, ContextCompat.getColor(this, R.color.transparent),
                ContextCompat.getColor(this, R.color.on_layout_bg),
                false);

        String source = getIntent().getStringExtra("source_activity");
        if (source != null && source.equals("OtpActivity")) {
            customDialog = new CustomDialog(this);
            customDialog.show();
        }
    }




    private void bottomNavigation() {
        nafisBottomNavigation = findViewById(R.id.bottomNavigation);

        // Add items
        nafisBottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.ic_icon_daily_task));
        nafisBottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.ic_ai_btn));
        nafisBottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.ic_profile));

        // Handle item click
        nafisBottomNavigation.setOnShowListener(model -> {
            Fragment selectedFragment = null;

            switch (model.getId()) {
                case 1:
                    selectedFragment = new FragmentDailyTask();
                    break;
                case 2:
                    selectedFragment = new FragmentAi();
                    break;
                case 3:
                    selectedFragment = new FragmentProfile();
                    break;
            }

            if (selectedFragment != null) {
                setFragment(selectedFragment);
            }
            return null;
        });

        // Show first item as selected
        nafisBottomNavigation.show(2, true);


    }
    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

}