package com.mediaghor.starnova.ui.util;

import com.mediaghor.starnova.model.LanguageInfo;

import java.util.HashMap;
import java.util.Map;

public class LanguageUtils {

    private static final Map<String, LanguageInfo> LANGUAGE_MAP = new HashMap<>();

    static {
        LANGUAGE_MAP.put("English", new LanguageInfo("English", "en"));
        LANGUAGE_MAP.put("हिंदी", new LanguageInfo("Hindi", "hi"));
        LANGUAGE_MAP.put("বাংলা", new LanguageInfo("Bangla", "bn"));
        LANGUAGE_MAP.put("தமிழ்", new LanguageInfo("Tamil", "ta"));
        LANGUAGE_MAP.put("తెలుగు", new LanguageInfo("Telugu", "te"));
    }

    public static LanguageInfo getLanguageInfo(String selectedText) {
        return LANGUAGE_MAP.get(selectedText);
    }
}

