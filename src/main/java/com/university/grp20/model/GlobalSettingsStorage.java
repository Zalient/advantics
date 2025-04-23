package com.university.grp20.model;

public class GlobalSettingsStorage {

    private static GlobalSettingsStorage instance;

    private String bounceType = "Pages Viewed";
    private String bounceValue = "1";

    private GlobalSettingsStorage() {}

    public static GlobalSettingsStorage getInstance() {
        if (instance == null) {
            instance = new GlobalSettingsStorage();
        }
        return instance;
    }

    public String getBounceType() {
        return bounceType;
    }

    public void setBounceType(String bounceType) {
        this.bounceType = bounceType;
    }

    // Getter and Setter for bounceValue
    public String getBounceValue() {
        return bounceValue;
    }

    public void setBounceValue(String bounceValue) {
        this.bounceValue = bounceValue;
    }
}
