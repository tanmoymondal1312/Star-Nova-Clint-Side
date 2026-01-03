package com.mediaghor.starnova.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.mediaghor.starnova.R;
import com.mediaghor.starnova.ui.util.KeyBoardUtils;
import com.mediaghor.starnova.ui.util.SystemBarUtils;
import com.mediaghor.starnova.ui.util.VibrationUtil;
import com.mukeshsolanki.OtpView;

public class OtpActivity extends AppCompatActivity {

    private OtpView otpView;
    private Button btnContinue;
    private MaterialButton errorMessage;
    private TextView resendOtp, timer;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000; // 60 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);

        // Handle edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set system bars color
        SystemBarUtils.setSystemBars(this, ContextCompat.getColor(this, R.color.layout_bg), false);

        initViews();
        setupListeners();
        startTimer();

    }

    private void initViews() {
        otpView = findViewById(R.id.otp_view);
        btnContinue = findViewById(R.id.btn_continue);
        errorMessage = findViewById(R.id.error_message);
        resendOtp = findViewById(R.id.resend_otp);
        timer = findViewById(R.id.timer);

        // Set back button listener
        findViewById(R.id.arrow_back).setOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        // OTP Text Change Listener
        otpView.setOtpCompletionListener(otp -> {
            // Enable continue button when OTP is complete
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundResource(R.drawable.button_background_enable);
            btnContinue.setTextColor(ContextCompat.getColor(this, R.color.white));
        });

        otpView.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideErrorMessage();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Disable button if OTP is not complete
                if (s.length() < 6) {
                    btnContinue.setEnabled(false);
                    btnContinue.setBackgroundResource(R.drawable.button_background_disabled);
                    btnContinue.setTextColor(ContextCompat.getColor(OtpActivity.this, R.color.white_disable));
                }
            }
        });

        // Continue Button Click Listener
        btnContinue.setOnClickListener(v -> {
            String enteredOtp = otpView.getText().toString();

            if (enteredOtp.length() < 6) {
                showErrorMessage("Please enter complete OTP");
                return;
            }

            // TODO: Implement OTP verification logic here
            verifyOtp(enteredOtp);
        });

        // Resend OTP Click Listener
        resendOtp.setOnClickListener(v -> {
            if (resendOtp.isEnabled()) {
                // TODO: Implement resend OTP logic here
                resendOtp();
            }
        });
    }

    private void verifyOtp(String otp) {
        // Simulate OTP verification
        showLoading(true);

        // Mock API call - Replace with actual API call
        new android.os.Handler().postDelayed(() -> {
            showLoading(false);

            // For demo, assume OTP "123456" is correct
            if (otp.equals("123456")) {
                // OTP verified successfully
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to next screen
            } else {
                showErrorMessage("Invalid OTP. Please try again.");
            }
        }, 1500);
    }

    private void resendOtp() {
        // TODO: Implement resend OTP API call
        Toast.makeText(this, "OTP Resent Successfully!", Toast.LENGTH_SHORT).show();
        startTimer();
    }

    private void startTimer() {

        // 🔴 RESET time
        timeLeftInMillis = 60000; // 60 seconds

        // 🔴 SHOW timer again
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
            }
        }.start();
    }


    private void updateCountDownText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        String timeLeft = String.format("%02ds", seconds);
        timer.setText(timeLeft);
    }

    private void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        otpView.setItemBackground(
                ContextCompat.getDrawable(this, R.drawable.bg_otp_item_error)
        );

        // 🔴 Change number color
        otpView.setTextColor(
                ContextCompat.getColor(this, R.color.red)
        );
        VibrationUtil.vibratePhone(OtpActivity.this,300);
    }

    private void hideErrorMessage() {
        errorMessage.setVisibility(View.GONE);
        otpView.setItemBackground(
                ContextCompat.getDrawable(this, R.drawable.bg_otp_item)
        );

        // 🔴 Change number color
        otpView.setTextColor(
                ContextCompat.getColor(this, R.color.white)
        );
    }

    private void showLoading(boolean show) {
        if (show) {
            btnContinue.setText("Verifying...");
            btnContinue.setEnabled(false);
        } else {
            btnContinue.setText(R.string.cont);
            btnContinue.setEnabled(otpView.getText().length() == 6);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}