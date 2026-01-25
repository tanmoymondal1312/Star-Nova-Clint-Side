package com.mediaghor.starnova.model;


public class ExperienceInfo {

    private final String displayName;
    private final String serverKey;

    public ExperienceInfo(String displayName, String serverKey) {
        this.displayName = displayName;
        this.serverKey = serverKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getServerKey() {
        return serverKey;
    }
}

