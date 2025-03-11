package com.university.grp20.model;

import java.time.LocalDate;
import java.util.List;

public class FilterDTO {
    private String metricName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String timeGranularity;
    private List<String> ageRanges;
    private List<String> incomes;
    private List<String> contexts;
    private String gender;

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTimeGranularity(String timeGranularity) {
        this.timeGranularity = timeGranularity;
    }

    public void setAgeRanges(List<String> ageRanges) {
        this.ageRanges = ageRanges;
    }

    public void setIncomes(List<String> incomes) {
        this.incomes = incomes;
    }

    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getTimeGranularity() {
        return timeGranularity;
    }

    public List<String> getAgeRanges() {
        return ageRanges;
    }

    public List<String> getContexts() {
        return contexts;
    }

    public List<String> getIncomes() {
        return incomes;
    }
}