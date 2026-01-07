package com.mediaghor.starnova.ui;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class FirebaseAuthManager {

    private static final String TAG = "FirebaseAuthManager";

    private static FirebaseAuthManager instance;
    private FirebaseAuth mAuth;
    private AuthCallback authCallback;

    // Verification state
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    public interface AuthCallback {
        void onVerificationCompleted(PhoneAuthCredential credential);
        void onVerificationFailed(Exception e);
        void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token);
        void onCodeAutoRetrievalTimeOut(String verificationId);
        void onSignInSuccess(AuthResult authResult);
        void onSignInFailed(Exception exception);
    }

    private FirebaseAuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseAuthManager getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthManager();
        }
        return instance;
    }

    public void setAuthCallback(AuthCallback callback) {
        this.authCallback = callback;
    }

    public void sendVerificationCode(String phoneNumber, Activity activity) {
        try {
            Log.i(TAG, "sendVerificationCode: Phone: " + phoneNumber);

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(createCallbacks())
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(options);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "sendVerificationCode: Invalid phone number", e);
            if (authCallback != null) {
                authCallback.onVerificationFailed(e);
            }
        } catch (Exception e) {
            Log.e(TAG, "sendVerificationCode: Unexpected error", e);
            if (authCallback != null) {
                authCallback.onVerificationFailed(new FirebaseException("Failed to send verification code: " + e.getMessage()) {
                    @Override
                    public String getMessage() {
                        return super.getMessage();
                    }
                });
            }
        }
    }

    public void resendVerificationCode(String phoneNumber, Activity activity) {
        try {
            Log.i(TAG, "resendVerificationCode: Phone: " + phoneNumber);

            PhoneAuthOptions.Builder optionsBuilder = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(createCallbacks());

            if (resendingToken != null) {
                optionsBuilder.setForceResendingToken(resendingToken);
                Log.d(TAG, "Using resending token");
            }

            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build());

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "resendVerificationCode: Invalid phone number", e);
            if (authCallback != null) {
                authCallback.onVerificationFailed(e);
            }
        } catch (Exception e) {
            Log.e(TAG, "resendVerificationCode: Unexpected error", e);
            if (authCallback != null) {
                authCallback.onVerificationFailed(new FirebaseException("Failed to resend verification code: " + e.getMessage()) {
                    @Override
                    public String getMessage() {
                        return super.getMessage();
                    }
                });
            }
        }
    }

    public void verifyOtp(String otp) {
        try {
            Log.i(TAG, "verifyOtp: OTP length: " + otp.length());

            if (verificationId == null) {
                Log.e(TAG, "verifyOtp: Verification ID is null!");
                if (authCallback != null) {
                    authCallback.onSignInFailed(new Exception("Verification ID not found"));
                }
                return;
            }

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            signInWithPhoneAuthCredential(credential);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "verifyOtp: Invalid OTP format", e);
            if (authCallback != null) {
                authCallback.onSignInFailed(e);
            }
        } catch (Exception e) {
            Log.e(TAG, "verifyOtp: Unexpected error", e);
            if (authCallback != null) {
                authCallback.onSignInFailed(e);
            }
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "signInWithPhoneAuthCredential: SUCCESS");
                                if (authCallback != null) {
                                    authCallback.onSignInSuccess(task.getResult());
                                }
                            } else {
                                Log.e(TAG, "signInWithPhoneAuthCredential: FAILED", task.getException());
                                if (authCallback != null) {
                                    authCallback.onSignInFailed(task.getException());
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "signInWithPhoneAuthCredential: Error in callback", e);
                            if (authCallback != null) {
                                authCallback.onSignInFailed(e);
                            }
                        }
                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks createCallbacks() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.i(TAG, "onVerificationCompleted: Auto verification");
                verificationId = null; // Clear verification ID as it's not needed
                if (authCallback != null) {
                    authCallback.onVerificationCompleted(credential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "onVerificationFailed: " + e.getMessage(), e);
                if (authCallback != null) {
                    authCallback.onVerificationFailed(e);
                }
            }

            @Override
            public void onCodeSent(@NonNull String verId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.i(TAG, "onCodeSent: Verification code sent");
                verificationId = verId;
                resendingToken = token;

                if (authCallback != null) {
                    authCallback.onCodeSent(verId, token);
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String verificationId) {
                Log.w(TAG, "onCodeAutoRetrievalTimeOut");
                if (authCallback != null) {
                    authCallback.onCodeAutoRetrievalTimeOut(verificationId);
                }
            }
        };
    }

    // Getters and Setters
    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public PhoneAuthProvider.ForceResendingToken getResendingToken() {
        return resendingToken;
    }

    public void setResendingToken(PhoneAuthProvider.ForceResendingToken resendingToken) {
        this.resendingToken = resendingToken;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    public String getCurrentUserPhone() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getPhoneNumber() : null;
    }

    public void signOut() {
        mAuth.signOut();
        verificationId = null;
        resendingToken = null;
        Log.i(TAG, "User signed out");
    }

    public void clearVerificationState() {
        verificationId = null;
        resendingToken = null;
        Log.d(TAG, "Verification state cleared");
    }
}