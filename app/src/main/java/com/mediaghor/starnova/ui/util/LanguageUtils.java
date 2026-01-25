package com.mediaghor.starnova.ui.util;

import com.mediaghor.starnova.model.LanguageInfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageUtils {

    private static final Map<String, LanguageInfo> LOOKUP_MAP = new HashMap<>();

    static {
        // ---- English ----
        register(
                new LanguageInfo("English", "English", "en"),
                "english", "en"
        );

        // ---- Bangla ----
        register(
                new LanguageInfo("বাংলা", "Bangla", "bn"),
                "বাংলা", "bangla", "bengali", "bn"
        );

        // ---- Hindi ----
        register(
                new LanguageInfo("हिंदी", "Hindi", "hi"),
                "हिंदी", "hindi", "hi"
        );

        // ---- Tamil ----
        register(
                new LanguageInfo("தமிழ்", "Tamil", "ta"),
                "தமிழ்", "tamil", "ta"
        );

        // ---- Telugu ----
        register(
                new LanguageInfo("తెలుగు", "Telugu", "te"),
                "తెలుగు", "telugu", "te"
        );
    }

    private static void register(LanguageInfo info, String... keys) {
        for (String key : keys) {
            LOOKUP_MAP.put(normalize(key), info);
        }
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim().toLowerCase(Locale.US);
    }

    public static LanguageInfo getLanguageInfo(String input) {
        return LOOKUP_MAP.get(normalize(input));
    }
}
