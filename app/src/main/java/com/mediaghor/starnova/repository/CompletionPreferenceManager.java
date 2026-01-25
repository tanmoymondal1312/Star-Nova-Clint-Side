package com.mediaghor.starnova.repository;
import android.content.Context;
import android.content.SharedPreferences;

public class CompletionPreferenceManager {

    private static final String PREF_NAME = "completion_pref";
    private static final String KEY_COMPLETED = "isCompleted";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public CompletionPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /* ================= SAVE ================= */
    public void setCompleted(boolean completed) {
        editor.putBoolean(KEY_COMPLETED, completed).apply();
    }

    /* ================= GET ================= */
    public boolean isCompleted() {
        return sharedPreferences.getBoolean(KEY_COMPLETED, false);
    }

    /* ================= CLEAR ================= */
    public void clear() {
        editor.clear().apply();
    }
}

