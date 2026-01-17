package com.mediaghor.starnova.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.mediaghor.starnova.R;
import com.mediaghor.starnova.model.LanguageInfo;
import com.mediaghor.starnova.repository.AuthTokenManager;
import com.mediaghor.starnova.repository.UserPreferenceManager;
import com.mediaghor.starnova.repository.UserSyncRepository;
import com.mediaghor.starnova.ui.util.BaseActivity;
import com.mediaghor.starnova.ui.util.LanguageManager;
import com.mediaghor.starnova.ui.util.LanguageUtils;
import com.mediaghor.starnova.ui.util.SystemBarUtils;
import com.mediaghor.starnova.ui.util.VibrationUtil;
import com.mediaghor.starnova.ui.util.WizardState;
import com.skydoves.powerspinner.PowerSpinnerView;

public class ActivityGetUserData extends BaseActivity {
    private ConstraintLayout includeContainer;
    private AppCompatButton nextButton;
    private int currentLayoutIndex = 1;
    private View currentLayoutView;

    // Variables to store user data
    private String userLanguage;
    private String userName;
    private String userExperience;
    private String userClass;
    private String userAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_user_data);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom + imeInsets.bottom
            );
            return insets;
        });

        SystemBarUtils.setSystemBars(
                this,
                ContextCompat.getColor(this, R.color.layout_bg),
                false
        );

        includeContainer = findViewById(R.id.include);
        nextButton = findViewById(R.id.appCompatButton);

        // Load the first layout
        currentLayoutIndex = WizardState.getStep(this);
        loadLayout(currentLayoutIndex);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNextButtonClick();
            }
        });
    }

    private void loadLayout(int layoutIndex) {
        int layoutResource = getLayoutResource(layoutIndex);
        if (layoutResource == 0) {
            return;
        }

        // Inflate the new layout
        View newLayout = LayoutInflater.from(this).inflate(layoutResource, includeContainer, false);

        // Initialize layout-specific UI elements
        initializeLayoutElements(newLayout, layoutIndex);

        // Animate the new layout coming from right
        Animation slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        newLayout.startAnimation(slideInRight);

        // Remove old layout with animation if exists
        if (currentLayoutView != null) {
            Animation slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
            currentLayoutView.startAnimation(slideOutLeft);
            includeContainer.removeView(currentLayoutView);
        }

        // Add new layout
        includeContainer.addView(newLayout);
        currentLayoutView = newLayout;

        // Update button text based on current layout
        updateButtonText();
    }

    private void initializeLayoutElements(View layoutView, int layoutIndex) {
        switch (layoutIndex) {
            case 1:
                PowerSpinnerView spinnerLanguage = layoutView.findViewById(R.id.spinnerLanguage);
                if (spinnerLanguage != null) {
                    spinnerLanguage.selectItemByIndex(0);
                }
                break;
            case 2:
                PowerSpinnerView spinnerExp = layoutView.findViewById(R.id.spinnerExp);
                if (spinnerExp != null) {
                    spinnerExp.selectItemByIndex(0);
                }
                break;
            case 3:
                // Initialize lay3 elements if needed
                break;
        }
    }

    private int getLayoutResource(int layoutIndex) {
        switch (layoutIndex) {
            case 1:
                return R.layout.lay1;
            case 2:
                return R.layout.lay2;
            case 3:
                return R.layout.lay3;
            default:
                return 0;
        }
    }

    private void handleNextButtonClick() {
        // Collect data from current layout before switching
        boolean isDataValid = collectCurrentLayoutData();

        if (!isDataValid) {
            return; // Don't proceed if data is invalid
        }

        if (currentLayoutIndex < 3) {
            currentLayoutIndex++;
            WizardState.saveStep(this, currentLayoutIndex);
            loadLayout(currentLayoutIndex);
        } else {
            WizardState.clear(this);
            submitAllData();
        }

    }

    private boolean collectCurrentLayoutData() {
        boolean isValid = true;

        switch (currentLayoutIndex) {
            case 1:
                PowerSpinnerView spinnerLanguage = currentLayoutView.findViewById(R.id.spinnerLanguage);
                if (spinnerLanguage != null) {
                    userLanguage = spinnerLanguage.getText().toString().trim();
                    if (!userLanguage.isEmpty() && !userLanguage.equals("Select Language")) {
                        Toast.makeText(this, userLanguage +" Is Your Language", Toast.LENGTH_SHORT).show();
                        LanguageInfo info = LanguageUtils.getLanguageInfo(userLanguage);
                        LanguageManager.saveLanguage(this, info.code);
                        WizardState.saveStep(this, 2);
                        restartSelf();
                        return false;
                    } else {
                        Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show();
                        VibrationUtil.vibratePhone(this, 100);
                        return false;
                    }
                }
                return false;

            case 2:
                EditText name = currentLayoutView.findViewById(R.id.etName);
                PowerSpinnerView spinnerExp = currentLayoutView.findViewById(R.id.spinnerExp);

                // Reset validation
                isValid = true;
                TextInputLayout nameLay = currentLayoutView.findViewById(R.id.tilName);

                // Validate name
                if (name != null) {
                    userName = name.getText().toString().trim();
                    if (userName.isEmpty()) {
                        nameLay.setError("Name Is Required");
                        VibrationUtil.vibratePhone(this, 100);
                        isValid = false;
                    } else {
                        nameLay.setError(null); // Clear error if valid
                    }
                }

                // Validate experience
                if (spinnerExp != null) {
                    userExperience = spinnerExp.getText().toString().trim();
                    if (userExperience.isEmpty() || userExperience.equals("Select Experience")) {
                        Toast.makeText(this, "Please select your experience level", Toast.LENGTH_SHORT).show();
                        VibrationUtil.vibratePhone(this, 100);
                        isValid = false;
                    }
                }
                return isValid;

            case 3:
                // Collect and validate both Class and Age fields
                EditText classEditText = currentLayoutView.findViewById(R.id.etClass);
                EditText ageEditText = currentLayoutView.findViewById(R.id.etAge);
                TextInputLayout classLay = currentLayoutView.findViewById(R.id.classLay);
                TextInputLayout ageLay = currentLayoutView.findViewById(R.id.AgeLay);

                // Reset errors
                classLay.setError(null);
                ageLay.setError(null);

                // Validate Class
                if (classEditText != null) {
                    userClass = classEditText.getText().toString().trim();
                    if (userClass.isEmpty()) {
                        classLay.setError("Class Is Required");
                        VibrationUtil.vibratePhone(this, 100);
                        isValid = false;
                    }
                }

                // Validate Age
                if (ageEditText != null) {
                    userAge = ageEditText.getText().toString().trim();
                    if (userAge.isEmpty()) {
                        ageLay.setError("Age Is Required");
                        VibrationUtil.vibratePhone(this, 100);
                        isValid = false;
                    } else {
                        // Optional: Validate age range
                        try {
                            int age = Integer.parseInt(userAge);
                            if (age < 5 || age > 100) {
                                ageLay.setError("Please enter a valid age (5-100)");
                                VibrationUtil.vibratePhone(this, 100);
                                isValid = false;
                            }
                        } catch (NumberFormatException e) {
                            ageLay.setError("Please enter a valid number");
                            VibrationUtil.vibratePhone(this, 100);
                            isValid = false;
                        }
                    }
                }
                return isValid;
        }
        return false;
    }

    private void submitAllData() {
        StringBuilder errorMessage = new StringBuilder("Please complete:\n");
        boolean hasErrors = false;

        userLanguage = LanguageManager.getLanguage(this);


        if (userLanguage == null || userLanguage.isEmpty() || userLanguage.equals("Select Language")) {
            errorMessage.append("• Language\n");
            hasErrors = true;
        }
        if (userName == null || userName.isEmpty()) {
            errorMessage.append("• Name\n");
            hasErrors = true;
        }
        if (userExperience == null || userExperience.isEmpty() || userExperience.equals("Select Experience")) {
            errorMessage.append("• Experience\n");
            hasErrors = true;
        }
        if (userClass == null || userClass.isEmpty()) {
            errorMessage.append("• Class\n");
            hasErrors = true;
        }
        if (userAge == null || userAge.isEmpty()) {
            errorMessage.append("• Age\n");
            hasErrors = true;
        }

        if (hasErrors) {
            Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_LONG).show();
            // Optionally, vibrate for error
            VibrationUtil.vibratePhone(this, 200);
        } else {
            // All data is valid - submit it
            processUserData();
        }
    }

    private void processUserData() {
        // Submit data to server or save locally
        // For example:
        // UserData userData = new UserData(userLanguage, userName, userExperience, userClass, userAge);
        // saveOrSubmitUserData(userData);
        UserSyncRepository repo = new UserSyncRepository(this);
        AuthTokenManager authTokenManager = new AuthTokenManager(this);
        String token = authTokenManager.getToken();
        repo.saveAndSyncUser(
                token,
                userLanguage,
                userName,
                userExperience,
                userClass,
                userExperience
        );

        Toast.makeText(this, "Data submitted successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateButtonText() {
        if (currentLayoutIndex == 3) {
            nextButton.setText("Submit");
        } else {
            nextButton.setText(R.string.next);
        }
    }

    // Optional: Add back button functionality
    public void onBackButtonClick(View view) {
        if (currentLayoutIndex > 1) {
            // Go to previous layout
            currentLayoutIndex--;
            loadLayout(currentLayoutIndex);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLayoutIndex > 1) {
            // Go to previous layout
            currentLayoutIndex--;
            loadLayout(currentLayoutIndex);
        } else {
            super.onBackPressed();
        }
    }

    private void restartSelf() {
        Intent intent = getIntent();
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK
        );
        startActivity(intent);
        finish();
    }


}