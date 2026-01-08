package com.mediaghor.starnova.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mediaghor.starnova.R;
import com.mediaghor.starnova.model.LoginRequest;
import com.mediaghor.starnova.model.LoginResponse;
import com.mediaghor.starnova.network.ApiService;
import com.mediaghor.starnova.network.RetrofitClient;
import com.mediaghor.starnova.repository.AuthTokenManager;
import com.mediaghor.starnova.ui.util.AuthExceptionHandler;
import com.mediaghor.starnova.ui.FirebaseAuthManager;
import com.mediaghor.starnova.ui.util.SystemBarUtils;
import com.mediaghor.starnova.ui.util.VibrationUtil;
import com.mukeshsolanki.OtpView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity implements FirebaseAuthManager.AuthCallback {

    private static final String TAG = "OtpActivity";

    private OtpView otpView;
    private Button btnContinue;
    private MaterialButton errorMessage;
    private TextView resendOtp, timer;
    private TextView tvPhoneNumber;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000;

    private FirebaseAuthManager authManager;
    private String phone = null;
    private AuthTokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(TAG, "onCreate: Activity starting");
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_otp);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            SystemBarUtils.setSystemBars(this, ContextCompat.getColor(this, R.color.layout_bg), false);

            // Initialize auth manager
            authManager = FirebaseAuthManager.getInstance();
            authManager.setAuthCallback(this);

            initViews();
            setupListeners();

            // Get data from AuthenticationActivity
            if (getIntent() != null) {
                String verificationId = getIntent().getStringExtra("verification_id");
                phone = getIntent().getStringExtra("phone");
                PhoneAuthProvider.ForceResendingToken token = getIntent().getParcelableExtra("resending_token");

                if (verificationId != null) {
                    authManager.setVerificationId(verificationId);
                }
                if (token != null) {
                    authManager.setResendingToken(token);
                }

                Log.i(TAG, "onCreate: Phone: " + phone);

                // Display phone number
                if (phone != null && tvPhoneNumber != null) {
                    String displayPhone = "******" + phone.substring(phone.length() - 4);
                    tvPhoneNumber.setText(getString(R.string.sent_to, displayPhone));
                }
            } else {
                Log.e(TAG, "onCreate: No intent data received");
                Toast.makeText(this, "Invalid session. Please try again.", Toast.LENGTH_LONG).show();
                finish();
            }

            startTimer();
            Log.d(TAG, "onCreate: Activity created successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to create activity", e);
            Toast.makeText(this, "Failed to initialize. Please restart the app.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        try {
            tokenManager = new AuthTokenManager(this);
            otpView = findViewById(R.id.otp_view);
            btnContinue = findViewById(R.id.btn_continue);
            errorMessage = findViewById(R.id.error_message);
            resendOtp = findViewById(R.id.resend_otp);
            timer = findViewById(R.id.timer);
            tvPhoneNumber = findViewById(R.id.tv_phone_number);

            findViewById(R.id.arrow_back).setOnClickListener(v -> onBackPressed());

            Log.d(TAG, "initViews: Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "initViews: Failed to initialize views", e);
            Toast.makeText(this, "Failed to initialize UI components", Toast.LENGTH_SHORT).show();
            throw e;
        }
    }

    private void setupListeners() {
        try {
            otpView.setOtpCompletionListener(otp -> {
                Log.d(TAG, "OTP entered: " + otp);
                btnContinue.setEnabled(true);
                btnContinue.setBackgroundResource(R.drawable.button_background_enable);
                btnContinue.setTextColor(ContextCompat.getColor(this, R.color.white));
            });

            otpView.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.v(TAG, "OTP text changed: " + s);
                    hideErrorMessage();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() < 6) {
                        btnContinue.setEnabled(false);
                        btnContinue.setBackgroundResource(R.drawable.button_background_disabled);
                        btnContinue.setTextColor(ContextCompat.getColor(OtpActivity.this, R.color.white_disable));
                    }
                }
            });

            btnContinue.setOnClickListener(v -> {
                String enteredOtp = otpView.getText().toString();
                Log.i(TAG, "Continue button clicked. OTP: " + enteredOtp);

                if (enteredOtp.length() < 6) {
                    showErrorMessage("Please enter complete OTP");
                    return;
                }

                verifyOtp(enteredOtp);
            });

            resendOtp.setOnClickListener(v -> {
                if (resendOtp.isEnabled()) {
                    Log.i(TAG, "Resend OTP clicked");
                    resendOtp();
                }
            });

            Log.d(TAG, "setupListeners: Listeners set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "setupListeners: Failed to set up listeners", e);
            Toast.makeText(this, "Failed to set up UI interactions", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyOtp(String otp) {
        try {
            Log.i(TAG, "verifyOtp: Starting OTP verification");
            showLoading(true);

            // Use auth manager to verify OTP
            authManager.verifyOtp(otp);

        } catch (Exception e) {
            Log.e(TAG, "verifyOtp: Error during verification", e);
            showErrorMessage("Invalid OTP format");
            showLoading(false);
        }
    }

    private void resendOtp() {
        try {
            if (phone == null) {
                Log.e(TAG, "resendOtp: Phone number is null");
                Toast.makeText(this, "Unable to resend OTP. Please go back and try again.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Log.i(TAG, "resendOtp: Resending OTP to " + phone);
            showLoading(true);
            resendOtp.setEnabled(false);

            // Use auth manager to resend OTP
            authManager.resendVerificationCode(phone, OtpActivity.this);

        } catch (Exception e) {
            Log.e(TAG, "resendOtp: Error resending OTP", e);
            showLoading(false);
            resendOtp.setEnabled(true);
            Toast.makeText(this, "Failed to resend OTP. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    // FirebaseAuthManager.AuthCallback Implementation
    @Override
    public void onVerificationCompleted(com.google.firebase.auth.PhoneAuthCredential credential) {
        Log.i(TAG, "onVerificationCompleted: Auto verification");
        runOnUiThread(() -> {
            showLoading(false);
            // Auto sign-in
            authManager.verifyOtp(credential.getSmsCode());
        });
    }

    @Override
    public void onVerificationFailed(Exception e) {
        Log.e(TAG, "onVerificationFailed: " + e.getMessage(), e);
        runOnUiThread(() -> {
            showLoading(false);
            resendOtp.setEnabled(true);
            String errorMessage = AuthExceptionHandler.getFriendlyErrorMessage(e);
            Toast.makeText(OtpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
        Log.i(TAG, "onCodeSent: OTP resent successfully");
        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(OtpActivity.this, "OTP resent successfully!", Toast.LENGTH_SHORT).show();
            startTimer();
        });
    }

    @Override
    public void onCodeAutoRetrievalTimeOut(String verificationId) {
        Log.w(TAG, "onCodeAutoRetrievalTimeOut");
        // Handle timeout if needed
    }

    @Override
    public void onSignInSuccess(AuthResult authResult) {
        Log.i(TAG, "onSignInSuccess: Authentication successful");
        runOnUiThread(() -> {


            Toast.makeText(OtpActivity.this,
                    "Authentication successful!", Toast.LENGTH_SHORT).show();
            if(tokenManager.hasToken()){
                tokenManager.deleteToken();
            }

            ApiService apiService = RetrofitClient
                    .getClient()
                    .create(ApiService.class);

            LoginRequest request = new LoginRequest(phone);

            apiService.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();
                        tokenManager.setToken(token);
                        Log.d(TAG,"The Auth Token IS: "+token.toString());
                        showLoading(false);
                        // TODO: Navigate to main activity or next screen
                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                    Log.d(TAG,throwable.getMessage().toString());

                }
            });




        });
    }

    @Override
    public void onSignInFailed(Exception exception) {
        Log.e(TAG, "onSignInFailed: " + exception.getMessage(), exception);
        runOnUiThread(() -> {
            showLoading(false);
            String errorMessage = AuthExceptionHandler.getSignInErrorMessage(exception);
            showErrorMessage(errorMessage);

            if (exception instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                VibrationUtil.vibratePhone(OtpActivity.this, 500);
            }
        });
    }

    private void startTimer() {
        try {
            timeLeftInMillis = 60000;
            timer.setVisibility(View.VISIBLE);
            otpView.setText("");
            hideErrorMessage();

            resendOtp.setEnabled(false);
            resendOtp.setTextColor(ContextCompat.getColor(this, R.color.color_white_low));
            resendOtp.setText("Resend OTP in");

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateCountDownText();
                }

                @Override
                public void onFinish() {
                    timer.setText("00s");
                    resendOtp.setEnabled(true);
                    resendOtp.setTextColor(ContextCompat.getColor(OtpActivity.this, R.color.white));
                    resendOtp.setText("Resend");
                    timer.setVisibility(View.GONE);
                    Log.d(TAG, "Timer finished. Resend OTP enabled.");
                }
            }.start();

            Log.d(TAG, "startTimer: Timer started for 60 seconds");
        } catch (Exception e) {
            Log.e(TAG, "startTimer: Failed to start timer", e);
        }
    }

    private void updateCountDownText() {
        try {
            int seconds = (int) (timeLeftInMillis / 1000);
            String timeLeft = String.format("%02ds", seconds);
            timer.setText(timeLeft);
        } catch (Exception e) {
            Log.e(TAG, "updateCountDownText: Error updating timer", e);
        }
    }

    private void showErrorMessage(String message) {
        try {
            Log.w(TAG, "showErrorMessage: " + message);
            errorMessage.setText(message);
            errorMessage.setVisibility(View.VISIBLE);
            otpView.setItemBackground(
                    ContextCompat.getDrawable(this, R.drawable.bg_otp_item_error)
            );
            otpView.setTextColor(ContextCompat.getColor(this, R.color.red));
            VibrationUtil.vibratePhone(OtpActivity.this, 300);
        } catch (Exception e) {
            Log.e(TAG, "showErrorMessage: Failed to show error", e);
        }
    }

    private void hideErrorMessage() {
        try {
            errorMessage.setVisibility(View.GONE);
            otpView.setItemBackground(
                    ContextCompat.getDrawable(this, R.drawable.bg_otp_item)
            );
            otpView.setTextColor(ContextCompat.getColor(this, R.color.white));
        } catch (Exception e) {
            Log.e(TAG, "hideErrorMessage: Failed to hide error", e);
        }
    }

    private void showLoading(boolean show) {
        try {
            runOnUiThread(() -> {
                if (show) {
                    btnContinue.setText("Verifying...");
                    btnContinue.setEnabled(false);
                    btnContinue.setBackgroundResource(R.drawable.button_background_disabled);
                } else {
                    btnContinue.setText(R.string.cont);
                    boolean isOtpComplete = otpView.getText().length() == 6;
                    btnContinue.setEnabled(isOtpComplete);

                    if (isOtpComplete) {
                        btnContinue.setBackgroundResource(R.drawable.button_background_enable);
                        btnContinue.setTextColor(ContextCompat.getColor(OtpActivity.this, R.color.white));
                    } else {
                        btnContinue.setBackgroundResource(R.drawable.button_background_disabled);
                        btnContinue.setTextColor(ContextCompat.getColor(OtpActivity.this, R.color.white_disable));
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "showLoading: Error updating loading state", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            authManager.setAuthCallback(null);
            Log.d(TAG, "onDestroy: Activity destroyed");
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: Error during destruction", e);
        }
    }
}