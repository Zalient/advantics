package com.university.grp20.model;

import com.university.grp20.UIManager;
import com.university.grp20.controller.ProgressBarListener;
import com.university.grp20.controller.ProgressLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.function.Supplier;

public class CalculateMetricsService {
  private final Logger logger = LogManager.getLogger(CalculateMetricsService.class);
  private ProgressBarListener filterProgressBar;
  private ProgressLabel filterProgressLabel;
  private record MetricResult(String metricName, double value) {}

  public void setOnFilterStart(ProgressBarListener listener) {
    this.filterProgressBar = listener;
  }

  public void setOnFilterLabelStart(ProgressLabel listener) {
    this.filterProgressLabel = listener;
  }

  private String campaignName;

  public CalculateMetricsService(String c) {
    this.campaignName = c;
    System.out.println("Creating metrics service with campaign name: " + campaignName);
  }

  public MetricsDTO fetchMetrics(FilterCriteriaDTO filterCriteriaDTO) {
    final int totalTasks = 7;
    ExecutorService executorService = Executors.newFixedThreadPool(totalTasks);
    CompletionService<MetricResult> completionService =
        new ExecutorCompletionService<>(executorService);
    // For storing the results in a thread-safe way
    ConcurrentHashMap<String, Double> metricResultsMap = new ConcurrentHashMap<>();
    filterProgressLabel.labelText("Calculating metrics...");

    completionService.submit(
        metricTask("impressions", () -> buildImpressionQuery(filterCriteriaDTO)));
    completionService.submit(metricTask("clicks", () -> buildClickQuery(filterCriteriaDTO)));
    completionService.submit(metricTask("uniques", () -> buildUniquesQuery(filterCriteriaDTO)));
    completionService.submit(metricTask("bounces", () -> buildBounceQuery(filterCriteriaDTO)));
    completionService.submit(
        metricTask("conversions", () -> buildConversionQuery(filterCriteriaDTO)));
    completionService.submit(
        metricTask("click cost", () -> buildClickCostQuery(filterCriteriaDTO)));
    completionService.submit(
        metricTask("impression cost", () -> buildImpressionCostQuery(filterCriteriaDTO)));

    for (int i = 0; i < totalTasks; i++) {
      try {
        Future<MetricResult> futureMetricResult = completionService.take();
        MetricResult metricResult = futureMetricResult.get();
        metricResultsMap.put(metricResult.metricName, metricResult.value);
        filterProgressBar.progressBar((double) (i + 1) / totalTasks);
      } catch (InterruptedException | ExecutionException e) {
        executorService.shutdownNow();
        logger.error("Error fetching metrics: " + e);
      }
    }

    executorService.shutdown();
    logger.info("All metric queries complete");
    double impressions = metricResultsMap.get("impressions");
    double clicks = metricResultsMap.get("clicks");
    double uniques = metricResultsMap.get("uniques");
    double bounces = metricResultsMap.get("bounces");
    double conversions = metricResultsMap.get("conversions");
    double totalCost = metricResultsMap.get("impression cost") + metricResultsMap.get("click cost");
    double ctr = (impressions == 0) ? 0.0 : clicks / impressions;
    double cpa = (conversions == 0) ? 0.0 : totalCost / conversions;
    double cpc = (clicks == 0) ? 0.0 : totalCost / clicks;
    double cpm = (impressions == 0) ? 0.0 : (totalCost / impressions) * 1000;
    double bounceRate = (clicks == 0) ? 0.0 : bounces/clicks;
    return new MetricsDTO(
        impressions,
        clicks,
        uniques,
        bounces,
        conversions,
        totalCost,
        ctr,
        cpa,
        cpc,
        cpm,
        bounceRate);
  }

  private Callable<MetricResult> metricTask(String metricName, Supplier<String> querySupplier) {
    return () -> {
      try (Connection conn = DBHelper.getConnection(campaignName)) {
        String query = querySupplier.get();
        double val = fetchMetricValue(conn, query);
        logger.info("Fetched " + metricName + ": " + val);
        return new MetricResult(metricName, val);
      }
    };
  }

  private double fetchMetricValue(Connection conn, String query) {
    logger.info("Executing query: " + query);
    try (ResultSet rs = DBHelper.executeQuery(conn, query)) {
      return (rs != null && rs.next()) ? rs.getDouble(1) : 0.0;
    } catch (SQLException e) {
      logger.error("Error executing query: " + query, e);
      throw new RuntimeException("Error executing query: " + query, e);
    }
  }

  private String buildImpressionQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) return "SELECT COUNT(*) FROM impressionLog";

    StringBuilder filterQuery = new StringBuilder();
    filterQuery.append("SELECT COUNT(*) FROM (");
    filterQuery.append(" SELECT u.ID, i.Date ");
    filterQuery.append(" FROM impressionLog i JOIN userData u ON u.ID = i.ID ");
    filterQuery.append(" WHERE 1=1 ");
    appendDateFilter(filterQuery, filterCriteriaDTO, "i", "Date");
    appendGenderFilter(filterQuery, filterCriteriaDTO, "u");
    appendAgeFilter(filterQuery, filterCriteriaDTO, "u");
    appendIncomeFilter(filterQuery, filterCriteriaDTO, "u");
    appendContextFilter(filterQuery, filterCriteriaDTO, "u");
    filterQuery.append(" GROUP BY u.ID, i.Date");
    filterQuery.append(")");
    return filterQuery.toString();
  }

  private String buildClickQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) return "SELECT COUNT(*) FROM clickLog";

    StringBuilder filterQuery = new StringBuilder();
    filterQuery.append("SELECT COUNT(*) FROM (");
    filterQuery.append(" SELECT u.ID, c.Date ");
    filterQuery.append(" FROM clickLog c JOIN userData u ON u.ID = c.ID ");
    filterQuery.append(" JOIN impressionLog i ON i.ID = u.ID ");
    filterQuery.append(" WHERE 1=1 ");
    appendDateFilter(filterQuery, filterCriteriaDTO, "c", "Date");
    appendGenderFilter(filterQuery, filterCriteriaDTO, "u");
    appendAgeFilter(filterQuery, filterCriteriaDTO, "u");
    appendIncomeFilter(filterQuery, filterCriteriaDTO, "u");
    appendContextFilter(filterQuery, filterCriteriaDTO, "u");
    filterQuery.append(" GROUP BY u.ID, c.Date");
    filterQuery.append(")");
    return filterQuery.toString();
  }

  private String buildUniquesQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) return "SELECT COUNT(DISTINCT(ID)) FROM clickLog";

    StringBuilder filterQuery = new StringBuilder();
    filterQuery.append("SELECT COUNT(*) FROM (");
    filterQuery.append(" SELECT u.ID ");
    filterQuery.append(" FROM clickLog c JOIN userData u ON u.ID = c.ID ");
    filterQuery.append(" JOIN impressionLog i ON i.ID = u.ID ");
    filterQuery.append(" WHERE 1=1 ");
    appendDateFilter(filterQuery, filterCriteriaDTO, "c", "Date");
    appendGenderFilter(filterQuery, filterCriteriaDTO, "u");
    appendAgeFilter(filterQuery, filterCriteriaDTO, "u");
    appendIncomeFilter(filterQuery, filterCriteriaDTO, "u");
    appendContextFilter(filterQuery, filterCriteriaDTO, "u");
    filterQuery.append(" GROUP BY u.ID");
    filterQuery.append(")");
    return filterQuery.toString();
  }

  private String buildBounceQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null)
      return "SELECT COUNT(*) FROM serverLog s WHERE " + applyBounceDef("s");

    StringBuilder filterQuery = new StringBuilder();
    filterQuery.append("SELECT COUNT(*) FROM (");
    filterQuery.append(" SELECT u.ID, s.EntryDate ");
    filterQuery.append(" FROM serverLog s JOIN userData u ON u.ID = s.ID ");
    filterQuery.append(" JOIN impressionLog i ON i.ID = u.ID ");
    filterQuery.append(" WHERE ").append(applyBounceDef("s"));
    appendDateFilter(filterQuery, filterCriteriaDTO, "s", "EntryDate");
    appendGenderFilter(filterQuery, filterCriteriaDTO, "u");
    appendAgeFilter(filterQuery, filterCriteriaDTO, "u");
    appendIncomeFilter(filterQuery, filterCriteriaDTO, "u");
    appendContextFilter(filterQuery, filterCriteriaDTO, "u");
    filterQuery.append(" GROUP BY u.ID, s.EntryDate");
    filterQuery.append(")");
    logger.info(filterQuery.toString());
    return filterQuery.toString();
  }

  private String buildConversionQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) return "SELECT COUNT(*) FROM serverLog WHERE Conversion = 'Yes'";

    StringBuilder filterQuery = new StringBuilder();
    filterQuery.append("SELECT COUNT(*) FROM (");
    filterQuery.append(" SELECT u.ID, s.EntryDate ");
    filterQuery.append(" FROM serverLog s JOIN userData u ON u.ID = s.ID ");
    filterQuery.append(" JOIN impressionLog i ON i.ID = u.ID ");
    filterQuery.append(" WHERE s.Conversion = 'Yes' ");
    appendDateFilter(filterQuery, filterCriteriaDTO, "s", "EntryDate");
    appendGenderFilter(filterQuery, filterCriteriaDTO, "u");
    appendAgeFilter(filterQuery, filterCriteriaDTO, "u");
    appendIncomeFilter(filterQuery, filterCriteriaDTO, "u");
    appendContextFilter(filterQuery, filterCriteriaDTO, "u");
    filterQuery.append(" GROUP BY u.ID, s.EntryDate");
    filterQuery.append(")");
    return filterQuery.toString();
  }

  private String buildClickCostQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) return "SELECT SUM(clickCost) FROM clickLog";

    StringBuilder filterQuery = new StringBuilder();
    filterQuery.append("SELECT SUM(uniqueClickCost) FROM (");
    filterQuery.append(" SELECT c.clickCost AS uniqueClickCost ");
    filterQuery.append(" FROM clickLog c JOIN userData u ON u.ID = c.ID ");
    filterQuery.append(" JOIN impressionLog i ON i.ID = u.ID ");
    filterQuery.append(" WHERE 1=1 ");
    appendDateFilter(filterQuery, filterCriteriaDTO, "c", "Date");
    appendGenderFilter(filterQuery, filterCriteriaDTO, "u");
    appendAgeFilter(filterQuery, filterCriteriaDTO, "u");
    appendIncomeFilter(filterQuery, filterCriteriaDTO, "u");
    appendContextFilter(filterQuery, filterCriteriaDTO, "u");
    filterQuery.append(" GROUP BY u.ID, c.Date");
    filterQuery.append(")");
    return filterQuery.toString();
  }

  private String buildImpressionCostQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) return "SELECT SUM(impressionCost) FROM impressionLog";

    StringBuilder filterQuery = new StringBuilder();
    filterQuery.append("SELECT SUM(uniqueImpressionCost) FROM (");
    filterQuery.append(" SELECT i.impressionCost AS uniqueImpressionCost ");
    filterQuery.append(" FROM impressionLog i JOIN userData u ON u.ID = i.ID ");
    filterQuery.append(" WHERE 1=1 ");
    appendDateFilter(filterQuery, filterCriteriaDTO, "i", "Date");
    appendGenderFilter(filterQuery, filterCriteriaDTO, "u");
    appendAgeFilter(filterQuery, filterCriteriaDTO, "u");
    appendIncomeFilter(filterQuery, filterCriteriaDTO, "u");
    appendContextFilter(filterQuery, filterCriteriaDTO, "u");
    filterQuery.append(" GROUP BY u.ID, i.Date");
    filterQuery.append(")");
    return filterQuery.toString();
  }

  private void appendDateFilter(
      StringBuilder filterQuery,
      FilterCriteriaDTO filterCriteriaDTO,
      String alias,
      String dateColumn) {
    if (filterCriteriaDTO.startDate() != null) {
      filterQuery
          .append(" AND ")
          .append(alias)
          .append(".")
          .append(dateColumn)
          .append(" >= '")
          .append(filterCriteriaDTO.startDate())
          .append(" 00:00:00")
          .append("'");
    }
    if (filterCriteriaDTO.endDate() != null) {
      filterQuery
          .append(" AND ")
          .append(alias)
          .append(".")
          .append(dateColumn)
          .append(" <= '")
          .append(filterCriteriaDTO.endDate())
          .append(" 23:59:59")
          .append("'");
    }
  }

  private void appendGenderFilter(
      StringBuilder filterQuery, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    if (filterCriteriaDTO.gender() != null && !filterCriteriaDTO.gender().isEmpty()) {
      filterQuery
          .append(" AND ")
          .append(alias)
          .append(".Gender = '")
          .append(filterCriteriaDTO.gender())
          .append("'");
    }
  }

  private void appendAgeFilter(
      StringBuilder filterQuery, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> ages = filterCriteriaDTO.ageRanges();
    if (ages != null && !ages.isEmpty()) {
      filterQuery
          .append(" AND ")
          .append(alias)
          .append(".Age IN (")
          .append(listToSQLInClause(ages))
          .append(")");
    }
  }

  private void appendIncomeFilter(
      StringBuilder filterQuery, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> incomes = filterCriteriaDTO.incomes();
    if (incomes != null && !incomes.isEmpty()) {
      filterQuery
          .append(" AND ")
          .append(alias)
          .append(".Income IN (")
          .append(listToSQLInClause(incomes))
          .append(")");
    }
  }

  private void appendContextFilter(
      StringBuilder filterQuery, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> contexts = filterCriteriaDTO.contexts();
    if (contexts != null && !contexts.isEmpty()) {
      filterQuery
          .append(" AND ")
          .append(alias)
          .append(".Context IN (")
          .append(listToSQLInClause(contexts))
          .append(")");
    }
  }

  private String listToSQLInClause(List<String> items) {
    return items.stream().map(item -> "'" + item + "'").collect(Collectors.joining(", "));
  }

  private static String applyBounceDef(String alias) {
    String bounceStmt = null;
    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
    String bounceType = globalSettings.getBounceType();
    String bounceValue = globalSettings.getBounceValue();
    if (bounceType.equals("Pages Viewed")) {
      bounceStmt = alias + ".PagesViewed <= " + bounceValue;
    } else if (bounceType.equals("Time Spent on Page")) {
      bounceStmt = alias + ".TimeSpent >= 0 AND " + alias + ".TimeSpent < " + bounceValue;
    }
    return bounceStmt;
  }
}
