package com.university.grp20.model;

import com.university.grp20.controller.ProgressBarListener;
import com.university.grp20.controller.ProgressLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CalculateMetricsService {

  private final Logger logger = LogManager.getLogger(CalculateMetricsService.class);
  private ProgressBarListener filterProgressBar;
  private ProgressLabel filterProgressLabel;

  public MetricsDTO getMetrics(FilterCriteriaDTO filterCriteriaDTO) {
    MetricsDTO metricsDTO = new MetricsDTO();
    try (Connection conn = DBHelper.getConnection()) {
      filterProgressLabel.labelText("Filtering impressions...");
      double impressions = rawMetricGetter(conn, buildImpressionQuery(filterCriteriaDTO));
      filterProgressBar.progressBar((double) 1 / 6);
      filterProgressLabel.labelText("Filtering clicks...");
      double clicks = rawMetricGetter(conn, buildClickQuery(filterCriteriaDTO));
      filterProgressBar.progressBar((double) 2 / 6);
      filterProgressLabel.labelText("Filtering uniques...");
      double uniques = rawMetricGetter(conn, buildUniquesQuery(filterCriteriaDTO));
      filterProgressBar.progressBar((double) 3 / 6);
      filterProgressLabel.labelText("Filtering bounces...");
      double bounces = rawMetricGetter(conn, buildBounceQuery(filterCriteriaDTO));
      filterProgressBar.progressBar((double) 4 / 6);
      filterProgressLabel.labelText("Filtering conversions...");
      double conversions = rawMetricGetter(conn, buildConversionQuery(filterCriteriaDTO));
      filterProgressBar.progressBar((double) 5 / 6);

      metricsDTO.setImpressions(impressions);
      metricsDTO.setClicks(clicks);
      metricsDTO.setUniques(uniques);
      metricsDTO.setBounces(bounces);
      metricsDTO.setConversions(conversions);

      filterProgressLabel.labelText("Filtering total cost...");
      double clickCost = rawMetricGetter(conn, buildClickCostQuery(filterCriteriaDTO));
      double impressionCost = rawMetricGetter(conn, buildImpressionCostQuery(filterCriteriaDTO));
      filterProgressBar.progressBar((double) 6 / 6);
      double totalCost = clickCost + impressionCost;
      metricsDTO.setTotalCost(totalCost);

      metricsDTO.setCtr((impressions == 0) ? 0 : (clicks / impressions));
      metricsDTO.setCpa((conversions == 0) ? 0 : (totalCost / conversions));
      metricsDTO.setCpc((clicks == 0) ? 0 : (totalCost / clicks));
      metricsDTO.setCpm((impressions == 0) ? 0 : ((totalCost / impressions) * 1000));
      metricsDTO.setBounceRate((clicks == 0) ? 0 : (bounces / clicks));
    } catch (SQLException e) {
      throw new RuntimeException("Error fetching metrics", e);
    }
    return metricsDTO;
  }

  public double rawMetricGetter(Connection conn, String query) {
    double result = 0.0;
    try (ResultSet rs = DBHelper.executeQuery(conn, query)) {
      if (rs != null && rs.next()) {
        result = rs.getDouble(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error executing query: " + query, e);
    }
    return result;
  }

  private String buildImpressionQuery(FilterCriteriaDTO filterCriteriaDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT COUNT(*) FROM (");
    sb.append(" SELECT u.ID, i.Date ");
    sb.append(" FROM impressionLog i JOIN userData u ON u.ID = i.ID ");
    sb.append(" WHERE 1=1 ");
    if (filterCriteriaDTO != null) {
      appendDateFilter(sb, filterCriteriaDTO, "i", "Date");
      appendGenderFilter(sb, filterCriteriaDTO, "u");
      appendAgeFilter(sb, filterCriteriaDTO, "u");
      appendIncomeFilter(sb, filterCriteriaDTO, "u");
      appendContextFilter(sb, filterCriteriaDTO, "u");
    }
    sb.append(" GROUP BY u.ID, i.Date");
    sb.append(")");
    return sb.toString();
  }

  private String buildClickQuery(FilterCriteriaDTO filterCriteriaDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT COUNT(*) FROM (");
    sb.append(" SELECT u.ID, c.Date ");
    sb.append(" FROM clickLog c JOIN userData u ON u.ID = c.ID ");
    sb.append(" JOIN impressionLog i ON i.ID = u.ID ");
    sb.append(" WHERE c.Date IS NOT NULL ");
    if (filterCriteriaDTO != null) {
      appendDateFilter(sb, filterCriteriaDTO, "c", "Date");
      appendGenderFilter(sb, filterCriteriaDTO, "u");
      appendAgeFilter(sb, filterCriteriaDTO, "u");
      appendIncomeFilter(sb, filterCriteriaDTO, "u");
      appendContextFilter(sb, filterCriteriaDTO, "u");
    }
    sb.append(" GROUP BY u.ID, c.Date");
    sb.append(")");
    return sb.toString();
  }

  private String buildUniquesQuery(FilterCriteriaDTO filterCriteriaDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT COUNT(*) FROM (");
    sb.append(" SELECT u.ID ");
    sb.append(" FROM clickLog c JOIN userData u ON u.ID = c.ID ");
    sb.append(" JOIN impressionLog i ON i.ID = u.ID ");
    sb.append(" WHERE c.Date IS NOT NULL ");
    if (filterCriteriaDTO != null) {
      appendDateFilter(sb, filterCriteriaDTO, "c", "Date");
      appendGenderFilter(sb, filterCriteriaDTO, "u");
      appendAgeFilter(sb, filterCriteriaDTO, "u");
      appendIncomeFilter(sb, filterCriteriaDTO, "u");
      appendContextFilter(sb, filterCriteriaDTO, "u");
    }
    sb.append(" GROUP BY u.ID");
    sb.append(")");
    return sb.toString();
  }

  private String buildBounceQuery(FilterCriteriaDTO filterCriteriaDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT COUNT(*) FROM (");
    sb.append(" SELECT u.ID, s.EntryDate ");
    sb.append(" FROM serverLog s JOIN userData u ON u.ID = s.ID ");
    sb.append(" JOIN impressionLog i ON i.ID = u.ID ");
    sb.append(" WHERE s.PagesViewed = 1 ");
    if (filterCriteriaDTO != null) {
      appendDateFilter(sb, filterCriteriaDTO, "s", "EntryDate");
      appendGenderFilter(sb, filterCriteriaDTO, "u");
      appendAgeFilter(sb, filterCriteriaDTO, "u");
      appendIncomeFilter(sb, filterCriteriaDTO, "u");
      appendContextFilter(sb, filterCriteriaDTO, "u");
    }
    sb.append(" GROUP BY u.ID, s.EntryDate");
    sb.append(")");
    return sb.toString();
  }

  private String buildConversionQuery(FilterCriteriaDTO filterCriteriaDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT COUNT(*) FROM (");
    sb.append(" SELECT u.ID, s.EntryDate ");
    sb.append(" FROM serverLog s JOIN userData u ON u.ID = s.ID ");
    sb.append(" JOIN impressionLog i ON i.ID = u.ID ");
    sb.append(" WHERE s.Conversion = 'Yes' ");
    if (filterCriteriaDTO != null) {
      appendDateFilter(sb, filterCriteriaDTO, "s", "EntryDate");
      appendGenderFilter(sb, filterCriteriaDTO, "u");
      appendAgeFilter(sb, filterCriteriaDTO, "u");
      appendIncomeFilter(sb, filterCriteriaDTO, "u");
      appendContextFilter(sb, filterCriteriaDTO, "u");
    }
    sb.append(" GROUP BY u.ID, s.EntryDate");
    sb.append(")");
    return sb.toString();
  }

  private String buildClickCostQuery(FilterCriteriaDTO filterCriteriaDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT SUM(unique_click_cost) FROM (");
    sb.append(" SELECT c.clickCost AS unique_click_cost ");
    sb.append(" FROM clickLog c JOIN userData u ON u.ID = c.ID ");
    sb.append(" JOIN impressionLog i ON i.ID = u.ID ");
    sb.append(" WHERE 1=1 ");
    if (filterCriteriaDTO != null) {
      appendDateFilter(sb, filterCriteriaDTO, "c", "Date");
      appendGenderFilter(sb, filterCriteriaDTO, "u");
      appendAgeFilter(sb, filterCriteriaDTO, "u");
      appendIncomeFilter(sb, filterCriteriaDTO, "u");
      appendContextFilter(sb, filterCriteriaDTO, "u");
    }
    sb.append(" GROUP BY u.ID, c.Date");
    sb.append(")");
    return sb.toString();
  }

  private String buildImpressionCostQuery(FilterCriteriaDTO filterCriteriaDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT SUM(unique_impression_cost) FROM (");
    sb.append(" SELECT i.impressionCost AS unique_impression_cost ");
    sb.append(" FROM impressionLog i JOIN userData u ON u.ID = i.ID ");
    sb.append(" WHERE 1=1 ");
    if (filterCriteriaDTO != null) {
      appendDateFilter(sb, filterCriteriaDTO, "i", "Date");
      appendGenderFilter(sb, filterCriteriaDTO, "u");
      appendAgeFilter(sb, filterCriteriaDTO, "u");
      appendIncomeFilter(sb, filterCriteriaDTO, "u");
      appendContextFilter(sb, filterCriteriaDTO, "u");
    }
    sb.append(" GROUP BY u.ID, i.Date");
    sb.append(")");
    return sb.toString();
  }

  private void appendDateFilter(StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias, String dateColumn) {
    if (filterCriteriaDTO.getStartDate() != null) {
      sb.append(" AND ").append(alias).append(".").append(dateColumn)
              .append(" >= '").append(filterCriteriaDTO.getStartDate()).append(" 00:00:00").append("'");
    }
    if (filterCriteriaDTO.getEndDate() != null) {
      sb.append(" AND ").append(alias).append(".").append(dateColumn)
              .append(" <= '").append(filterCriteriaDTO.getEndDate()).append(" 23:59:59").append("'");
    }
  }

  private void appendGenderFilter(StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    if (filterCriteriaDTO.getGender() != null && !filterCriteriaDTO.getGender().isEmpty()) {
      sb.append(" AND ").append(alias).append(".Gender = '")
              .append(filterCriteriaDTO.getGender()).append("'");
    }
  }

  private void appendAgeFilter(StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> ages = filterCriteriaDTO.getAgeRanges();
    if (ages != null && !ages.isEmpty()) {
      sb.append(" AND ").append(alias).append(".Age IN (")
              .append(listToSQLInClause(ages)).append(")");
    }
  }

  private void appendIncomeFilter(StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> incomes = filterCriteriaDTO.getIncomes();
    if (incomes != null && !incomes.isEmpty()) {
      sb.append(" AND ").append(alias).append(".Income IN (")
              .append(listToSQLInClause(incomes)).append(")");
    }
  }

  private void appendContextFilter(StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> contexts = filterCriteriaDTO.getContexts();
    if (contexts != null && !contexts.isEmpty()) {
      sb.append(" AND ").append(alias).append(".Context IN (")
              .append(listToSQLInClause(contexts)).append(")");
    }
  }

  private String listToSQLInClause(List<String> items) {
    return items.stream().map(item -> "'" + item + "'").collect(Collectors.joining(", "));
  }

  public void setOnFilterStart(ProgressBarListener listener) {
    this.filterProgressBar = listener;
  }

  public void setOnFilterLabelStart(ProgressLabel listener) {
    this.filterProgressLabel = listener;
  }
}
