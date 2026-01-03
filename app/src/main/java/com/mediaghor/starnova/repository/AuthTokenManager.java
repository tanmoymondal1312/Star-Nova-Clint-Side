package com.mediaghor.starnova.repository;

// File: AuthTokenManager.java

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AuthTokenManager {

    private static final String PREF_FILE = "secure_prefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";

    private SharedPreferences sharedPreferences;

    public AuthTokenManager(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_FILE,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Fallback to regular SharedPreferences if encryption fails (optional)
            sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        }
    }

    // Save token
    public void setToken(String token) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }

    // Get token
    public String getToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    // Check if token exists
    public boolean hasToken() {
        return sharedPreferences.contains(KEY_AUTH_TOKEN);
    }

    // Delete token
    public void deleteToken() {
        sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply();
    }
}
