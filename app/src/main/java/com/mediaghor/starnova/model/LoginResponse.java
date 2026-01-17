package com.mediaghor.starnova.model;

public class LoginResponse {

    private String token;
    private boolean profile_completed;
    private UserData user_data; // nullable

    public String getToken() {
        return token;
    }

    public boolean isProfileCompleted() {
        return profile_completed;
    }

    public UserData getUserData() {
        return user_data;
    }
}
