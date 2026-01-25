package com.mediaghor.starnova.model;

public class LanguageInfo {

    private String displayName;          // Native name (বাংলা)
    private String displayNameEnglish;   // English name (Bangla)
    private String code;                 // Language code (bn)

    public LanguageInfo(String displayName, String displayNameEnglish, String code) {
        this.displayName = displayName;
        this.displayNameEnglish = displayNameEnglish;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameEnglish() {
        return displayNameEnglish;
    }

    public String getCode() {
        return code;
    }
}
