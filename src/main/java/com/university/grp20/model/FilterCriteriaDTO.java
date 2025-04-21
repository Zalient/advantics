package com.university.grp20.model;

import java.time.LocalDate;
import java.util.List;

public record FilterCriteriaDTO(
    List<String> ageRanges,
    List<String> incomes,
    List<String> contexts,
    String timeGranularity,
    String gender,
    LocalDate startDate,
    LocalDate endDate) {}
