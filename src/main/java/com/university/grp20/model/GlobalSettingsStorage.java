package com.university.grp20.model;

import java.util.HashMap;
import java.util.Map;

public class GlobalSettingsStorage {

    private static GlobalSettingsStorage instance;

    private String bounceType = "Pages Viewed";
    private String bounceValue = "1";

    private Map<String, Boolean> metricVisibility;

    private GlobalSettingsStorage() {
        metricVisibility = new HashMap<>();
        initializeMetricVisibility();
    }

    private void initializeMetricVisibility() {
        metricVisibility.put("impressions", true);
        metricVisibility.put("clicks", true);
        metricVisibility.put("uniques", true);
        metricVisibility.put("bounces", true);
        metricVisibility.put("conversions", true);
        metricVisibility.put("totalCost", true);
        metricVisibility.put("ctr", true);
        metricVisibility.put("cpa", true);
        metricVisibility.put("cpc", true);
        metricVisibility.put("cpm", true);
        metricVisibility.put("bounceRate", true);
    }

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

    public boolean isMetricVisible(String metricName) {
        return metricVisibility.getOrDefault(metricName, true);
    }

    public void setMetricVisibility(String metricName, boolean isVisible) {
        metricVisibility.put(metricName, isVisible);
    }

    public Map<String, Boolean> getAllMetricVisibility() {
        return new HashMap<>(metricVisibility);
    }
}
