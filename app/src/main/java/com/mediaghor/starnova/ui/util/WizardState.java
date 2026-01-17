package com.mediaghor.starnova.ui.util;

import android.content.Context;

public final class WizardState {

    private static final String PREF = "wizard_state";
    private static final String KEY_STEP = "current_step";

    private WizardState() {}

    public static void saveStep(Context context, int step) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_STEP, step)
                .apply();
    }

    public static int getStep(Context context) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getInt(KEY_STEP, 1);
    }

    public static void clear(Context context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
