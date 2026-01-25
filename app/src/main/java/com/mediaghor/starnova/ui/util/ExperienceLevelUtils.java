package com.mediaghor.starnova.ui.util;


import com.mediaghor.starnova.model.ExperienceInfo;
import java.util.HashMap;
import java.util.Map;

public class ExperienceLevelUtils {

    private static final Map<String, ExperienceInfo> EXPERIENCE_MAP = new HashMap<>();

    static {
        // English
        EXPERIENCE_MAP.put("Beginner", new ExperienceInfo("Beginner", "beginner"));
        EXPERIENCE_MAP.put("Intermediate", new ExperienceInfo("Intermediate", "intermediate"));
        EXPERIENCE_MAP.put("Advanced", new ExperienceInfo("Advanced", "advanced"));

        // Hindi
        EXPERIENCE_MAP.put("शुरुआती", new ExperienceInfo("Beginner", "beginner"));
        EXPERIENCE_MAP.put("मध्यवर्ती", new ExperienceInfo("Intermediate", "intermediate"));
        EXPERIENCE_MAP.put("उन्नत", new ExperienceInfo("Advanced", "advanced"));

        // Bangla
        EXPERIENCE_MAP.put("প্রাথমিক", new ExperienceInfo("Beginner", "beginner"));
        EXPERIENCE_MAP.put("মধ্যবর্তী", new ExperienceInfo("Intermediate", "intermediate"));
        EXPERIENCE_MAP.put("উন্নত", new ExperienceInfo("Advanced", "advanced"));

        // Tamil
        EXPERIENCE_MAP.put("ஆரம்பம்", new ExperienceInfo("Beginner", "beginner"));
        EXPERIENCE_MAP.put("நடுத்தரம்", new ExperienceInfo("Intermediate", "intermediate"));
        EXPERIENCE_MAP.put("உயர் நிலை", new ExperienceInfo("Advanced", "advanced"));

        // Telugu
        EXPERIENCE_MAP.put("పప్రారంభం", new ExperienceInfo("Beginner", "beginner"));
        EXPERIENCE_MAP.put("మధ్యస్థం", new ExperienceInfo("Intermediate", "intermediate"));
        EXPERIENCE_MAP.put("అధునాతన", new ExperienceInfo("Advanced", "advanced"));
    }

    public static String getServerValue(String selectedText) {
        ExperienceInfo info = EXPERIENCE_MAP.get(selectedText);
        return info != null ? info.getServerKey() : null;
    }
}

