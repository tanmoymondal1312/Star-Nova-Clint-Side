package com.mediaghor.starnova.ui.util;

import android.app.LocaleManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public final class LanguageManager {

    private static final String PREF_NAME = "app_language_pref";
    private static final String KEY_LANGUAGE = "selected_language";

    private LanguageManager() {}

    /* Save language */
    public static void saveLanguage(Context context, String language) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANGUAGE, language)
                .apply();
    }

    /* Get saved language */
    public static String getLanguage(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, "en");
    }

    /* Apply language (All Android versions) */
    public static Context applyLanguage(Context context) {
        String lang = getLanguage(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setLocaleApi33(context, lang);
            return context;
        } else {
            return updateResources(context, lang);
        }
    }

    /* Android 13+ */
    private static void setLocaleApi33(Context context, String lang) {
        LocaleManager localeManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            localeManager = context.getSystemService(LocaleManager.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            localeManager.setApplicationLocales(
                    LocaleList.forLanguageTags(lang)
            );
        }
    }

    /* Android 8–12 */
    private static Context updateResources(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}

