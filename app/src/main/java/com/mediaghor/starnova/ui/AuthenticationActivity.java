package com.mediaghor.starnova.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hbb20.CountryCodePicker;
import com.mediaghor.starnova.R;
import com.mediaghor.starnova.ui.util.KeyBoardUtils;
import com.mediaghor.starnova.ui.util.SystemBarUtils;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText etPhone;
    private CountryCodePicker ccp;
    private Button btnGetOtp;
    private CheckBox checkboxTerms;
    private ProgressBar progressGetOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SystemBarUtils.setSystemBars(this, ContextCompat.getColor(this, R.color.layout_bg), false);

        // Initialize views
        initViews();

        // Setup listeners
        setupListeners();

        // Initially disable the button
        updateGetOtpButtonState();


    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        ccp = findViewById(R.id.ccp);
        btnGetOtp = findViewById(R.id.btn_get_otp);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        progressGetOtp = findViewById(R.id.progress_get_otp);
    }

    private void setupListeners() {
        // Phone number text change listener
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateGetOtpButtonState();
            }
        });

        // Checkbox state change listener
        checkboxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateGetOtpButtonState();
        });

        // Get OTP button click listener
        btnGetOtp.setOnClickListener(v -> {
            if (isFormValid()) {
                // Show progress bar and disable button
                progressGetOtp.setVisibility(View.VISIBLE);
                btnGetOtp.setText(null);
                btnGetOtp.setEnabled(false);

                // Get phone number with country code
                String countryCode = ccp.getSelectedCountryCodeWithPlus();
                String phoneNumber = etPhone.getText().toString().trim();
                String fullPhoneNumber = countryCode + phoneNumber;

                // TODO: Implement OTP sending logic here
                // For now, simulate API call with delay
                simulateOtpRequest();
            }
        });
    }

    private void updateGetOtpButtonState() {
        boolean isValid = isFormValid();

        if (isValid) {
            // Enable button with enabled background
            btnGetOtp.setBackgroundResource(R.drawable.button_background_enable);
            btnGetOtp.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGetOtp.setEnabled(true);
        } else {
            // Disable button with disabled background
            btnGetOtp.setBackgroundResource(R.drawable.button_background_disabled);
            btnGetOtp.setTextColor(ContextCompat.getColor(this, R.color.white_disable));
            btnGetOtp.setEnabled(false);
        }
    }

    private boolean isFormValid() {
        // Check if phone number has more than 7 digits
        String phoneNumber = etPhone.getText().toString().trim();
        boolean isPhoneValid = phoneNumber.length() > 7;

        // Check if terms checkbox is checked
        boolean isTermsAccepted = checkboxTerms.isChecked();

        return isPhoneValid && isTermsAccepted;
    }

    private void simulateOtpRequest() {
        // Simulate API call delay
        new android.os.Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                // Hide progress bar
                progressGetOtp.setVisibility(View.GONE);
                btnGetOtp.setEnabled(true);
                btnGetOtp.setText(ContextCompat.getString(this,R.string.get_otp));

                Intent intent = new Intent(AuthenticationActivity.this, OtpActivity.class);

                startActivity(intent);

// finish();




                // TODO: Navigate to OTP verification screen
                // For example:
                // Intent intent = new Intent(AuthenticationActivity.this, OtpVerificationActivity.class);
                // intent.putExtra("phone_number", fullPhoneNumber);
                // startActivity(intent);

                // For now, just update button state
                updateGetOtpButtonState();
            });
        }, 2000); // 2 second delay
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update button state when returning to this activity
        updateGetOtpButtonState();
    }
}