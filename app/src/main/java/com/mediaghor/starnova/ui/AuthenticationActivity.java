package com.mediaghor.starnova.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.mediaghor.starnova.R;
import com.mediaghor.starnova.ui.util.AuthExceptionHandler;
import com.mediaghor.starnova.ui.FirebaseAuthManager;
import com.mediaghor.starnova.ui.util.SystemBarUtils;

public class AuthenticationActivity extends AppCompatActivity implements FirebaseAuthManager.AuthCallback {

    private static final String TAG = "AuthenticationActivity";
    private static final String PHONE_NUMBER_KEY = "phone_number";
    private static final String VERIFICATION_ID_KEY = "verification_id";

    private EditText etPhone;
    private CountryCodePicker ccp;
    private Button btnGetOtp;
    private CheckBox checkboxTerms;
    private ProgressBar progressGetOtp;

    private FirebaseAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(TAG, "onCreate: Activity starting");
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_authentication);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            SystemBarUtils.setSystemBars(
                    this,
                    ContextCompat.getColor(this, R.color.layout_bg),
                    false
            );

            // Initialize auth manager
            authManager = FirebaseAuthManager.getInstance();
            authManager.setAuthCallback(this);

            // Restore state if available
            if (savedInstanceState != null) {
                String verificationId = savedInstanceState.getString(VERIFICATION_ID_KEY);
                if (verificationId != null) {
                    authManager.setVerificationId(verificationId);
                }
                String savedPhone = savedInstanceState.getString(PHONE_NUMBER_KEY);
                if (savedPhone != null && etPhone != null) {
                    etPhone.setText(savedPhone);
                }
                Log.d(TAG, "onCreate: State restored");
            }

            initViews();
            setupListeners();
            updateGetOtpButtonState();

            Log.d(TAG, "onCreate: Activity created successfully");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to create activity", e);
            Toast.makeText(this, "Failed to initialize authentication. Please restart the app.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            String verificationId = authManager.getVerificationId();
            if (verificationId != null) {
                outState.putString(VERIFICATION_ID_KEY, verificationId);
            }
            if (etPhone != null) {
                outState.putString(PHONE_NUMBER_KEY, etPhone.getText().toString());
            }
            Log.d(TAG, "onSaveInstanceState: State saved");
        } catch (Exception e) {
            Log.e(TAG, "onSaveInstanceState: Failed to save state", e);
        }
    }

    private void initViews() {
        try {
            etPhone = findViewById(R.id.et_phone);
            ccp = findViewById(R.id.ccp);
            btnGetOtp = findViewById(R.id.btn_get_otp);
            checkboxTerms = findViewById(R.id.checkbox_terms);
            progressGetOtp = findViewById(R.id.progress_get_otp);

            Log.d(TAG, "initViews: Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "initViews: Failed to initialize views", e);
            Toast.makeText(this, "Failed to initialize UI components", Toast.LENGTH_SHORT).show();
            throw e;
        }
    }

    private void setupListeners() {
        try {
            etPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG, "afterTextChanged: Phone number length=" + s.length());
                    updateGetOtpButtonState();
                }
            });

            checkboxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(TAG, "Terms checkbox changed: " + isChecked);
                updateGetOtpButtonState();
            });

            btnGetOtp.setOnClickListener(v -> {
                Log.d(TAG, "Get OTP button clicked");
                try {
                    if (isFormValid()) {
                        progressGetOtp.setVisibility(View.VISIBLE);
                        btnGetOtp.setText(null);
                        btnGetOtp.setEnabled(false);

                        String countryCode = ccp.getSelectedCountryCodeWithPlus();
                        String phone = etPhone.getText().toString().trim();
                        String fullPhone = countryCode + phone;

                        Log.i(TAG, "Initiating OTP request for phone: " + fullPhone);

                        // Use auth manager to send OTP
                        authManager.sendVerificationCode(fullPhone, AuthenticationActivity.this);
                    } else {
                        Log.w(TAG, "Form validation failed");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in button click handler", e);
                    resetButtonState();
                    Toast.makeText(AuthenticationActivity.this,
                            "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
                }
            });

            Log.d(TAG, "setupListeners: Listeners set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "setupListeners: Failed to set up listeners", e);
            Toast.makeText(this, "Failed to set up UI interactions", Toast.LENGTH_SHORT).show();
        }
    }

    // FirebaseAuthManager.AuthCallback Implementation
    @Override
    public void onVerificationCompleted(PhoneAuthCredential credential) {
        Log.i(TAG, "onVerificationCompleted: Auto-verification succeeded");
        runOnUiThread(() -> {
            try {
                resetButtonState();
                // Auto sign-in if needed
                // authManager.verifyOtp(credential.getSmsCode());
            } catch (Exception e) {
                Log.e(TAG, "onVerificationCompleted: UI update failed", e);
            }
        });
    }

    @Override
    public void onVerificationFailed(Exception e) {
        Log.e(TAG, "onVerificationFailed: " + e.getMessage(), e);
        runOnUiThread(() -> {
            try {
                resetButtonState();
                String errorMessage = AuthExceptionHandler.getFriendlyErrorMessage(e);
                Toast.makeText(AuthenticationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            } catch (Exception uiException) {
                Log.e(TAG, "onVerificationFailed: UI update failed", uiException);
            }
        });
    }

    @Override
    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
        Log.i(TAG, "onCodeSent: Verification code sent successfully");
        runOnUiThread(() -> {
            try {
                resetButtonState();
                redirectToOtpActivity();
            } catch (Exception e) {
                Log.e(TAG, "onCodeSent: UI update failed", e);
                Toast.makeText(AuthenticationActivity.this,
                        "Failed to proceed. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCodeAutoRetrievalTimeOut(String verificationId) {
        Log.w(TAG, "onCodeAutoRetrievalTimeOut: Auto-retrieval timed out");
        // Handle timeout if needed
    }

    @Override
    public void onSignInSuccess(com.google.firebase.auth.AuthResult authResult) {
        // Not used in AuthenticationActivity - handled in OtpActivity
    }

    @Override
    public void onSignInFailed(Exception exception) {
        // Not used in AuthenticationActivity - handled in OtpActivity
    }

    private void redirectToOtpActivity() {
        try {
            String countryCode = ccp.getSelectedCountryCodeWithPlus();
            String phone = etPhone.getText().toString().trim();
            String fullPhone = countryCode + phone;

            Log.i(TAG, "redirectToOtpActivity: Navigating to OTP activity");

            String verificationId = authManager.getVerificationId();
            PhoneAuthProvider.ForceResendingToken token = authManager.getResendingToken();

            if (verificationId == null) {
                Log.e(TAG, "redirectToOtpActivity: verificationId is null!");
                Toast.makeText(this, "Verification failed. Please try again.", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(AuthenticationActivity.this, OtpActivity.class);
            intent.putExtra("verification_id", verificationId);
            intent.putExtra("phone", fullPhone);

            if (token != null) {
                intent.putExtra("resending_token", token);
            }

            startActivity(intent);
            Log.d(TAG, "redirectToOtpActivity: Activity started successfully");

        } catch (Exception e) {
            Log.e(TAG, "redirectToOtpActivity: Failed to start OTP activity", e);
            Toast.makeText(this, "Failed to proceed to OTP verification", Toast.LENGTH_LONG).show();
        }
    }

    private void resetButtonState() {
        try {
            runOnUiThread(() -> {
                progressGetOtp.setVisibility(View.GONE);
                btnGetOtp.setEnabled(true);
                btnGetOtp.setText(getString(R.string.get_otp));
            });
        } catch (Exception e) {
            Log.e(TAG, "resetButtonState: Failed to reset button state", e);
        }
    }

    private void updateGetOtpButtonState() {
        try {
            boolean isValid = isFormValid();
            Log.v(TAG, "updateGetOtpButtonState: Form valid = " + isValid);

            runOnUiThread(() -> {
                try {
                    if (isValid) {
                        btnGetOtp.setBackgroundResource(R.drawable.button_background_enable);
                        btnGetOtp.setTextColor(ContextCompat.getColor(this, R.color.white));
                        btnGetOtp.setEnabled(true);
                    } else {
                        btnGetOtp.setBackgroundResource(R.drawable.button_background_disabled);
                        btnGetOtp.setTextColor(ContextCompat.getColor(this, R.color.white_disable));
                        btnGetOtp.setEnabled(false);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "updateGetOtpButtonState: UI update failed", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updateGetOtpButtonState: Failed to update button state", e);
        }
    }

    private boolean isFormValid() {
        try {
            String phone = etPhone.getText().toString().trim();
            boolean isPhoneValid = phone.length() > 7;
            boolean isTermsAccepted = checkboxTerms.isChecked();

            return isPhoneValid && isTermsAccepted;
        } catch (Exception e) {
            Log.e(TAG, "isFormValid: Error checking form validity", e);
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear callback to prevent memory leaks
        authManager.setAuthCallback(null);
        Log.d(TAG, "onDestroy: Activity destroyed");
    }
}