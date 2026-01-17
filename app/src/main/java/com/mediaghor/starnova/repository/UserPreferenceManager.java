package com.mediaghor.starnova.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class UserPreferenceManager {

    private static final String PREF_NAME = "user_profile_pref";

    private static final String KEY_LANGUAGE = "userLanguage";
    private static final String KEY_NAME = "userName";
    private static final String KEY_EXPERIENCE = "userExperience";
    private static final String KEY_CLASS = "userClass";
    private static final String KEY_AGE = "userAge";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public UserPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /* ================= SAVE METHODS ================= */

    public void saveUserLanguage(String language) {
        editor.putString(KEY_LANGUAGE, language).apply();
    }

    public void saveUserName(String name) {
        editor.putString(KEY_NAME, name).apply();
    }

    public void saveUserExperience(String experience) {
        editor.putString(KEY_EXPERIENCE, experience).apply();
    }

    public void saveUserClass(String userClass) {
        editor.putString(KEY_CLASS, userClass).apply();
    }

    public void saveUserAge(String age) {
        editor.putString(KEY_AGE, age).apply();
    }

    public void saveAllUserData(
            String language,
            String name,
            String experience,
            String userClass,
            String age
    ) {
        editor.putString(KEY_LANGUAGE, language);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EXPERIENCE, experience);
        editor.putString(KEY_CLASS, userClass);
        editor.putString(KEY_AGE, age);
        editor.apply();
    }
    public boolean isUserProfileComplete() {
        return !TextUtils.isEmpty(getUserLanguage())
                && !TextUtils.isEmpty(getUserName())
                && !TextUtils.isEmpty(getUserExperience())
                && !TextUtils.isEmpty(getUserClass())
                && !TextUtils.isEmpty(getUserAge());
    }


    /* ================= GET METHODS ================= */

    public String getUserLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "en");
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_NAME, "");
    }

    public String getUserExperience() {
        return sharedPreferences.getString(KEY_EXPERIENCE, "");
    }

    public String getUserClass() {
        return sharedPreferences.getString(KEY_CLASS, "");
    }

    public String getUserAge() {
        return sharedPreferences.getString(KEY_AGE, "");
    }

    /* ================= CLEAR ================= */

    public void clearAll() {
        editor.clear().apply();
    }
}

