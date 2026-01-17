package com.mediaghor.starnova.model;

// UserSyncResponse.java

public class UserSyncResponse {

    public String message;
    public UserData user_data;

    public static class UserData {
        public String language;
        public String name;
        public String experience_level;
        public int age;
        public String classification;
    }
}

