package com.university.grp20.model;

public record MetricsDTO(
    double impressions,
    double clicks,
    double uniques,
    double bounces,
    double conversions,
    double totalCost,
    double ctr,
    double cpa,
    double cpc,
    double cpm,
    double bounceRate) {}
