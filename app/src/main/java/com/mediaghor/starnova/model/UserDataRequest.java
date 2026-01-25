package com.mediaghor.starnova.model;

public class UserDataRequest {

    private String name;
    private String language;
    private String experience_level;
    private Integer age;
    private String classification;

    public UserDataRequest(String name, String language,
                           String experience_level,
                           Integer age, String classification) {
        this.name = name;
        this.language = language;
        this.experience_level = experience_level;
        this.age = age;
        this.classification = classification;
    }
}
