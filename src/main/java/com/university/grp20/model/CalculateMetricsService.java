package com.university.grp20.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CalculateMetricsService {
  public MetricsDTO getMetrics(FilterCriteriaDTO filterCriteriaDTO) {
    MetricsDTO metricsDTO = new MetricsDTO();
    try (Connection conn = DBHelper.getConnection()) {
      double impressions = rawMetricGetter(conn, buildImpressionQuery(filterCriteriaDTO));
      double clicks = rawMetricGetter(conn, buildClickQuery(filterCriteriaDTO));
      double uniques = rawMetricGetter(conn, buildUniquesQuery(filterCriteriaDTO));
      double bounces = rawMetricGetter(conn, buildBounceQuery(filterCriteriaDTO));
      double conversions = rawMetricGetter(conn, buildConversionQuery(filterCriteriaDTO));

      metricsDTO.setImpressions(impressions);
      metricsDTO.setClicks(clicks);
      metricsDTO.setUniques(uniques);
      metricsDTO.setBounces(bounces);
      metricsDTO.setConversions(conversions);

      double impressionCost = rawMetricGetter(conn, buildImpressionCostQuery(filterCriteriaDTO));
      double clickCost = rawMetricGetter(conn, buildClickCostQuery(filterCriteriaDTO));
      double totalCost = impressionCost + clickCost;
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
    if (filterCriteriaDTO == null) {
      return "SELECT COUNT(*) FROM impressionLog";
    }
    StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM impressionLog i WHERE 1=1");
    appendDateFilter(sb, filterCriteriaDTO, "i", "Date");
    appendGenderFilter(sb, filterCriteriaDTO, "i");
    appendAgeFilter(sb, filterCriteriaDTO, "i");
    appendIncomeFilter(sb, filterCriteriaDTO, "i");
    appendContextFilter(sb, filterCriteriaDTO, "i");
    return sb.toString();
  }

  private String buildClickQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) {
      return "SELECT COUNT(*) FROM clickLog cl";
    }
    StringBuilder sb =
        new StringBuilder(
            "SELECT COUNT(*) FROM clickLog cl INNER JOIN impressionLog i ON cl.ID = i.ID WHERE 1=1");
    appendDateFilter(sb, filterCriteriaDTO, "cl", "Date");
    appendGenderFilter(sb, filterCriteriaDTO, "i");
    appendAgeFilter(sb, filterCriteriaDTO, "i");
    appendIncomeFilter(sb, filterCriteriaDTO, "i");
    appendContextFilter(sb, filterCriteriaDTO, "i");
    return sb.toString();
  }

  private String buildUniquesQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) {
      return "SELECT COUNT(DISTINCT ID) FROM clickLog";
    }
    StringBuilder sb =
        new StringBuilder(
            "SELECT COUNT(DISTINCT i.ID) FROM clickLog cl INNER JOIN impressionLog i ON cl.ID = i.ID WHERE 1=1");
    appendDateFilter(sb, filterCriteriaDTO, "i", "Date");
    appendGenderFilter(sb, filterCriteriaDTO, "i");
    appendAgeFilter(sb, filterCriteriaDTO, "i");
    appendIncomeFilter(sb, filterCriteriaDTO, "i");
    appendContextFilter(sb, filterCriteriaDTO, "i");
    return sb.toString();
  }

  private String buildBounceQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) {
      return "SELECT COUNT(*) FROM serverLog s WHERE s.PagesViewed = 1";
    }
    StringBuilder sb =
        new StringBuilder(
            "SELECT COUNT(*) FROM serverLog s INNER JOIN impressionLog i ON s.ID = i.ID WHERE s.PagesViewed = 1");
    appendDateFilter(sb, filterCriteriaDTO, "s", "EntryDate");
    appendGenderFilter(sb, filterCriteriaDTO, "i");
    appendAgeFilter(sb, filterCriteriaDTO, "i");
    appendIncomeFilter(sb, filterCriteriaDTO, "i");
    appendContextFilter(sb, filterCriteriaDTO, "i");
    return sb.toString();
  }

  private String buildConversionQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) {
      return "SELECT COUNT(*) FROM serverLog s WHERE s.Conversion = 'Yes'";
    }
    StringBuilder sb =
        new StringBuilder(
            "SELECT COUNT(*) FROM serverLog s INNER JOIN impressionLog i ON s.ID = i.ID WHERE s.Conversion = 'Yes'");
    appendDateFilter(sb, filterCriteriaDTO, "s", "EntryDate");
    appendGenderFilter(sb, filterCriteriaDTO, "i");
    appendAgeFilter(sb, filterCriteriaDTO, "i");
    appendIncomeFilter(sb, filterCriteriaDTO, "i");
    appendContextFilter(sb, filterCriteriaDTO, "i");
    return sb.toString();
  }

  private String buildImpressionCostQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) {
      return "SELECT SUM(ImpressionCost) FROM impressionLog";
    }
    StringBuilder sb =
        new StringBuilder("SELECT SUM(ImpressionCost) FROM impressionLog i WHERE 1=1");
    appendDateFilter(sb, filterCriteriaDTO, "i", "Date");
    appendGenderFilter(sb, filterCriteriaDTO, "i");
    appendAgeFilter(sb, filterCriteriaDTO, "i");
    appendIncomeFilter(sb, filterCriteriaDTO, "i");
    appendContextFilter(sb, filterCriteriaDTO, "i");
    return sb.toString();
  }

  private String buildClickCostQuery(FilterCriteriaDTO filterCriteriaDTO) {
    if (filterCriteriaDTO == null) {
      return "SELECT SUM(ClickCost) FROM clickLog cl";
    }
    StringBuilder sb =
        new StringBuilder(
            "SELECT SUM(ClickCost) FROM clickLog cl INNER JOIN impressionLog i ON cl.ID = i.ID WHERE 1=1");
    appendDateFilter(sb, filterCriteriaDTO, "cl", "Date");
    appendGenderFilter(sb, filterCriteriaDTO, "i");
    appendAgeFilter(sb, filterCriteriaDTO, "i");
    appendIncomeFilter(sb, filterCriteriaDTO, "i");
    appendContextFilter(sb, filterCriteriaDTO, "i");
    return sb.toString();
  }

  private void appendDateFilter(
      StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias, String dateColumn) {
    if (filterCriteriaDTO.getStartDate() != null) {
      sb.append(" AND ")
          .append(alias)
          .append(".")
          .append(dateColumn)
          .append(" >= '")
          .append(filterCriteriaDTO.getStartDate())
          .append("'");
    }
    if (filterCriteriaDTO.getEndDate() != null) {
      sb.append(" AND ")
          .append(alias)
          .append(".")
          .append(dateColumn)
          .append(" <= '")
          .append(filterCriteriaDTO.getEndDate())
          .append("'");
    }
  }

  private void appendGenderFilter(
      StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    if (filterCriteriaDTO.getGender() != null && !filterCriteriaDTO.getGender().isEmpty()) {
      sb.append(" AND ")
          .append(alias)
          .append(".Gender = '")
          .append(filterCriteriaDTO.getGender())
          .append("'");
    }
  }

  private void appendAgeFilter(
      StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> ages = filterCriteriaDTO.getAgeRanges();
    if (ages != null && !ages.isEmpty()) {
      sb.append(" AND ")
          .append(alias)
          .append(".Age IN (")
          .append(listToSQLInClause(ages))
          .append(")");
    }
  }

  private void appendIncomeFilter(
      StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> incomes = filterCriteriaDTO.getIncomes();
    if (incomes != null && !incomes.isEmpty()) {
      sb.append(" AND ")
          .append(alias)
          .append(".Income IN (")
          .append(listToSQLInClause(incomes))
          .append(")");
    }
  }

  private void appendContextFilter(
      StringBuilder sb, FilterCriteriaDTO filterCriteriaDTO, String alias) {
    List<String> contexts = filterCriteriaDTO.getContexts();
    if (contexts != null && !contexts.isEmpty()) {
      sb.append(" AND ")
          .append(alias)
          .append(".Context IN (")
          .append(listToSQLInClause(contexts))
          .append(")");
    }
  }

  private String listToSQLInClause(List<String> items) {
    return items.stream().map(item -> "'" + item + "'").collect(Collectors.joining(", "));
  }
}
