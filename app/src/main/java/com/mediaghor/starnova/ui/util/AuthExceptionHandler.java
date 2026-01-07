package com.mediaghor.starnova.ui.util;

import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class AuthExceptionHandler {

    private static final String TAG = "AuthExceptionHandler";

    public static String getFriendlyErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid phone number format. Please check and try again.";
        } else if (exception instanceof FirebaseTooManyRequestsException) {
            return "Too many requests. Please try again later.";
        } else if (exception instanceof IllegalArgumentException) {
            return "Invalid phone number format. Please check and try again.";
        } else if (exception instanceof FirebaseException) {
            return "Authentication failed. Please check your connection and try again.";
        } else {
            return "An error occurred. Please try again.";
        }
    }

    public static String getSignInErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid OTP. Please try again.";
        } else if (exception instanceof FirebaseTooManyRequestsException) {
            return "Too many attempts. Please try again later.";
        } else if (exception instanceof IllegalArgumentException) {
            return "Invalid OTP format. Please try again.";
        } else {
            return "Authentication failed. Please try again.";
        }
    }

    public static void logError(String tag, String operation, Exception e) {
        Log.e(tag, operation + " failed: " + e.getMessage(), e);
    }
}