package com.mediaghor.starnova.ui;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mediaghor.starnova.R;
import com.mediaghor.starnova.repository.AuthTokenManager;
import com.mediaghor.starnova.ui.Fragments.FragmentAi;
import com.mediaghor.starnova.ui.Fragments.FragmentDailyTask;
import com.mediaghor.starnova.ui.Fragments.FragmentProfile;
import com.mediaghor.starnova.ui.util.SystemBarUtils;
import com.nafis.bottomnavigation.NafisBottomNavigation;

public class MainActivity extends AppCompatActivity {
    NafisBottomNavigation nafisBottomNavigation;
    private AuthTokenManager tokenManager;

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
        //tokenManager.setToken("uehdfuerhfue874jhgrfurh78478");
        tokenManager.deleteToken();
        if (!tokenManager.hasToken()) {
            Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
            startActivity(intent);
        }
        bottomNavigation();
        SystemBarUtils.setSystemBars(this, ContextCompat.getColor(this, R.color.layout_bg),
                ContextCompat.getColor(this, R.color.on_layout_bg),
                false);



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