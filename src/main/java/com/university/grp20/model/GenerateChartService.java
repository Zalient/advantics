package com.university.grp20.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateChartService {
  private final Logger logger = LogManager.getLogger(GenerateChartService.class);

  private DefaultCategoryDataset getCategoryDataset(String query, String metricLine) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    try (Connection conn = DBHelper.getConnection();
        ResultSet rs = DBHelper.executeQuery(conn, query)) {
      while (rs != null && rs.next()) {
        String x = rs.getString(1);
        double y = rs.getDouble(2);
        dataset.addValue(y, metricLine, x);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error fetching category dataset", e);
    }
    return dataset;
  }

  private HistogramDataset getHistogramDataset(String query, String metricLine, int binSize) {
    List<Double> dataPoints = new ArrayList<>();
    try (Connection conn = DBHelper.getConnection();
        ResultSet rs = DBHelper.executeQuery(conn, query)) {
      while (rs != null && rs.next()) {
        dataPoints.add(rs.getDouble(1));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error fetching histogram dataset", e);
    }
    double[] dataArray = dataPoints.stream().mapToDouble(Double::doubleValue).toArray();
    HistogramDataset histogramDataset = new HistogramDataset();
    histogramDataset.addSeries(metricLine, dataArray, binSize);
    return histogramDataset;
  }

  private String applyCommonFilter(
      String baseSQL,
      FilterCriteriaDTO filterDTO,
      String dateAlias,
      String dateColumn,
      String impressionAlias,
      boolean hasImpression) {
    StringBuilder sb = new StringBuilder(baseSQL);

    if (filterDTO.getStartDate() != null) {
      sb.append(" AND ")
          .append(dateAlias)
          .append(".")
          .append(dateColumn)
          .append(" >= '")
          .append(filterDTO.getStartDate())
          .append("'");
    }
    if (filterDTO.getEndDate() != null) {
      sb.append(" AND ")
          .append(dateAlias)
          .append(".")
          .append(dateColumn)
          .append(" <= '")
          .append(filterDTO.getEndDate())
          .append("'");
    }
    if (hasImpression && impressionAlias != null && !impressionAlias.isEmpty()) {
      if (filterDTO.getGender() != null) {
        sb.append(" AND ")
            .append(impressionAlias)
            .append(".Gender = '")
            .append(filterDTO.getGender())
            .append("'");
      }
      if (filterDTO.getAgeRanges() != null && !filterDTO.getAgeRanges().isEmpty()) {
        sb.append(" AND ").append(impressionAlias).append(".Age IN (");
        for (int i = 0; i < filterDTO.getAgeRanges().size(); i++) {
          String age = filterDTO.getAgeRanges().get(i);
          if ("Below 25".equals(age)) {
            age = "<25";
          } else if ("Above 54".equals(age)) {
            age = ">54";
          }
          sb.append("'").append(age).append("'");
          if (i < filterDTO.getAgeRanges().size() - 1) {
            sb.append(", ");
          }
        }
        sb.append(")");
      }
      if (filterDTO.getIncomes() != null && !filterDTO.getIncomes().isEmpty()) {
        sb.append(" AND ").append(impressionAlias).append(".Income IN (");
        sb.append(
            filterDTO.getIncomes().stream()
                .map(income -> "'" + income + "'")
                .collect(Collectors.joining(", ")));
        sb.append(")");
      }
      if (filterDTO.getContexts() != null && !filterDTO.getContexts().isEmpty()) {
        sb.append(" AND ").append(impressionAlias).append(".Context IN (");
        sb.append(
            filterDTO.getContexts().stream()
                .map(context -> "'" + context + "'")
                .collect(Collectors.joining(", ")));
        sb.append(")");
      }
      if (filterDTO.getTimeGranularity() == null
          || filterDTO.getTimeGranularity().isEmpty()
          || "Per Day".equals(filterDTO.getTimeGranularity())) {
        sb.append(" GROUP BY STRFTIME('%Y-%m-%d', ")
            .append(dateAlias)
            .append(".")
            .append(dateColumn)
            .append(") ");
      } else if ("Per Week".equals(filterDTO.getTimeGranularity())) {
        sb.append(" GROUP BY STRFTIME('%Y-%W', ")
            .append(dateAlias)
            .append(".")
            .append(dateColumn)
            .append(") ");
      }
    }
    return sb.toString();
  }

  public JFreeChart getFilteredChart(String metricType, FilterCriteriaDTO filterCriteriaDTO) {
    return switch (metricType) {
      case "Impressions" -> filteredImpressionsChart(filterCriteriaDTO);
      case "Clicks" -> filteredClicksChart(filterCriteriaDTO);
      case "Uniques" -> filteredUniquesChart(filterCriteriaDTO);
      case "Bounces" -> filteredBouncesChart(filterCriteriaDTO);
      case "Conversions" -> filteredConversionsChart(filterCriteriaDTO);
      case "Total Cost" -> filteredTotalCostChart(filterCriteriaDTO);
      case "CTR" -> filteredCTRChart(filterCriteriaDTO);
      case "CPA" -> filteredCPAChart(filterCriteriaDTO);
      case "CPC" -> filteredCPCChart(filterCriteriaDTO);
      case "CPM" -> filteredCPMChart(filterCriteriaDTO);
      case "Bounce Rate" -> filteredBounceRateChart(filterCriteriaDTO);
      default -> {
        System.out.println("Unknown metricType: " + metricType + ". Returning null chart.");
        yield null;
      }
    };
  }

  public JFreeChart filteredImpressionsChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        "SELECT strftime('%Y-%m-%d', Date) AS day, COUNT(*) AS impressions_by_day "
            + "FROM impressionLog i WHERE 1=1 ";
    String finalSQL =
        applyCommonFilter(baseSQL, filterDTO, "i", "Date", "i", true) + " ORDER BY day";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Impressions");
    return ChartFactory.createLineChart("Impressions Per Day", "Day", "Impressions", dataset);
  }

  public JFreeChart filteredClicksChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        "SELECT strftime('%Y-%m-%d', c.Date) AS day, COUNT(*) AS clicks_by_day "
            + "FROM clickLog c JOIN impressionLog i ON c.ID = i.ID WHERE 1=1 ";
    String finalSQL =
        applyCommonFilter(baseSQL, filterDTO, "c", "Date", "i", true) + " ORDER BY day";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Clicks");
    return ChartFactory.createLineChart("Clicks Per Day", "Day", "Clicks", dataset);
  }

  public JFreeChart filteredUniquesChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        "SELECT strftime('%Y-%m-%d', Date) AS day, COUNT(DISTINCT i.ID) AS uniques_by_day "
            + "FROM impressionLog i WHERE 1=1 ";
    String finalSQL =
        applyCommonFilter(baseSQL, filterDTO, "i", "Date", "i", true) + " ORDER BY day";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Uniques");
    return ChartFactory.createLineChart("Uniques Per Day", "Day", "Uniques", dataset);
  }

  public JFreeChart filteredBouncesChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        "SELECT strftime('%Y-%m-%d', EntryDate) AS day, COUNT(*) AS bounces_by_day "
            + "FROM serverLog s JOIN impressionLog i ON s.ID = i.ID WHERE s.PagesViewed = 1 ";
    String finalSQL =
        applyCommonFilter(baseSQL, filterDTO, "s", "EntryDate", "i", true) + " ORDER BY day";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Bounces");
    return ChartFactory.createLineChart("Bounces Per Day", "Day", "Bounces", dataset);
  }

  public JFreeChart filteredConversionsChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        "SELECT strftime('%Y-%m-%d', EntryDate) AS day, COUNT(*) AS conversions_by_day "
            + "FROM serverLog s JOIN impressionLog i ON s.ID = i.ID WHERE s.Conversion = 'Yes' ";
    String finalSQL =
        applyCommonFilter(baseSQL, filterDTO, "s", "EntryDate", "i", true) + " ORDER BY day";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Conversions");
    return ChartFactory.createLineChart("Conversions Per Day", "Day", "Conversions", dataset);
  }

  public JFreeChart filteredTotalCostChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        "SELECT STRFTIME('%d', i.Date) AS day, SUM(i.ImpressionCost) AS total_amount "
            + "FROM impressionLog i WHERE 1=1 ";
    impressionSQL =
        applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "i", true) + " GROUP BY day";

    String clickSQL =
        "SELECT STRFTIME('%d', c.Date) AS day, SUM(c.ClickCost) AS total_amount "
            + "FROM clickLog c JOIN impressionLog i2 ON c.ID = i2.ID WHERE 1=1 ";
    clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "i2", true) + " GROUP BY day";

    String finalSQL =
        "SELECT day, SUM(total_amount) AS combined_total FROM ("
            + impressionSQL
            + " UNION ALL "
            + clickSQL
            + ") AS daily_total_cost GROUP BY day ORDER BY day";
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Total Cost");
    return ChartFactory.createLineChart("Total Cost Per Day", "Day", "Total Cost", dataset);
  }

  public JFreeChart filteredCTRChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        "SELECT STRFTIME('%d', i.Date) AS day, COUNT(*) AS Num_Of_Imp "
            + "FROM impressionLog i WHERE 1=1 ";
    impressionSQL =
        applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "i", true) + " GROUP BY day";

    String clickSQL =
        "SELECT STRFTIME('%d', c.Date) AS day, COUNT(*) AS Num_Of_Clicks "
            + "FROM clickLog c JOIN impressionLog i2 ON c.ID = i2.ID WHERE 1=1 ";
    clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "i2", true) + " GROUP BY day";

    String finalSQL =
        "SELECT imp.day, (cli.Num_Of_Clicks * 100.0 / imp.Num_Of_Imp) AS ctr "
            + "FROM ("
            + impressionSQL
            + ") imp JOIN ("
            + clickSQL
            + ") cli ON imp.day = cli.day ORDER BY imp.day";
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "CTR");
    return ChartFactory.createLineChart("CTR Per Day", "Day", "CTR", dataset);
  }

  public JFreeChart filteredCPAChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        "SELECT strftime('%d', i.Date) AS Day_Of_Month, SUM(i.ImpressionCost) AS total_cost "
            + "FROM impressionLog i WHERE 1=1 ";
    impressionSQL =
        applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "i", true)
            + " GROUP BY Day_Of_Month";

    String clickSQL =
        "SELECT strftime('%d', c.Date) AS Day_Of_Month, SUM(c.ClickCost) AS total_cost "
            + "FROM clickLog c JOIN impressionLog i2 ON c.ID = i2.ID WHERE 1=1 ";
    clickSQL =
        applyCommonFilter(clickSQL, filterDTO, "c", "Date", "i2", true) + " GROUP BY Day_Of_Month";

    String unionCost =
        "SELECT Day_Of_Month, SUM(total_cost) AS Daily_Cost FROM ("
            + impressionSQL
            + " UNION ALL "
            + clickSQL
            + ") GROUP BY Day_Of_Month";

    String conversionSQL =
        "SELECT strftime('%d', s.EntryDate) AS Day_Of_Month, COUNT(*) AS Total_Conversions "
            + "FROM serverLog s JOIN impressionLog i3 ON s.ID = i3.ID WHERE s.Conversion = 'Yes' ";
    conversionSQL =
        applyCommonFilter(conversionSQL, filterDTO, "s", "EntryDate", "i3", true)
            + " GROUP BY Day_Of_Month";

    String finalSQL =
        "SELECT cost.Day_Of_Month, (cost.Daily_Cost * 1.0 / conv.Total_Conversions) AS CPA "
            + "FROM ("
            + unionCost
            + ") cost JOIN ("
            + conversionSQL
            + ") conv ON cost.Day_Of_Month = conv.Day_Of_Month ORDER BY cost.Day_Of_Month";
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "CPA");
    return ChartFactory.createLineChart("CPA Per Day", "Day", "CPA", dataset);
  }

  public JFreeChart filteredCPCChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        "SELECT strftime('%d', i.Date) AS Day_Of_Month, SUM(i.ImpressionCost) AS total_cost "
            + "FROM impressionLog i WHERE 1=1 ";
    impressionSQL =
        applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "i", true)
            + " GROUP BY Day_Of_Month";

    String clickSQL =
        "SELECT strftime('%d', c.Date) AS Day_Of_Month, SUM(c.ClickCost) AS total_cost "
            + "FROM clickLog c JOIN impressionLog i2 ON c.ID = i2.ID WHERE 1=1 ";
    clickSQL =
        applyCommonFilter(clickSQL, filterDTO, "c", "Date", "i2", true) + " GROUP BY Day_Of_Month";

    String unionCost =
        "SELECT Day_Of_Month, SUM(total_cost) AS Daily_Cost FROM ("
            + impressionSQL
            + " UNION ALL "
            + clickSQL
            + ") GROUP BY Day_Of_Month";

    String totalClickSQL =
        "SELECT strftime('%d', c2.Date) AS Day_Of_Month, COUNT(*) AS Total_Clicks "
            + "FROM clickLog c2 JOIN impressionLog i3 ON c2.ID = i3.ID WHERE 1=1 ";
    totalClickSQL =
        applyCommonFilter(totalClickSQL, filterDTO, "c2", "Date", "i3", true)
            + " GROUP BY Day_Of_Month";

    String finalSQL =
        "SELECT cost.Day_Of_Month, (COALESCE(cost.Daily_Cost, 0) * 1.0 / NULLIF(clicks.Total_Clicks, 0)) AS CPC "
            + "FROM ("
            + unionCost
            + ") cost JOIN ("
            + totalClickSQL
            + ") clicks ON cost.Day_Of_Month = clicks.Day_Of_Month ORDER BY cost.Day_Of_Month";
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "CPC");
    return ChartFactory.createLineChart("CPC Per Day", "Day", "CPC", dataset);
  }

  public JFreeChart filteredCPMChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        "SELECT strftime('%d', i.Date) AS Day_Of_Month, SUM(i.ImpressionCost) AS cost_part "
            + "FROM impressionLog i WHERE 1=1 ";
    impressionSQL =
        applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "i", true)
            + " GROUP BY Day_Of_Month";

    String clickSQL =
        "SELECT strftime('%d', c.Date) AS Day_Of_Month, SUM(c.ClickCost) AS cost_part "
            + "FROM clickLog c JOIN impressionLog i2 ON c.ID = i2.ID WHERE 1=1 ";
    clickSQL =
        applyCommonFilter(clickSQL, filterDTO, "c", "Date", "i2", true) + " GROUP BY Day_Of_Month";

    String unionCost =
        "SELECT Day_Of_Month, SUM(cost_part) AS Daily_Cost FROM ("
            + impressionSQL
            + " UNION ALL "
            + clickSQL
            + ") GROUP BY Day_Of_Month";

    String impressionCountSQL =
        "SELECT strftime('%d', i3.Date) AS Day_Of_Month, COUNT(*) AS Total_Impressions "
            + "FROM impressionLog i3 WHERE 1=1 ";
    impressionCountSQL =
        applyCommonFilter(impressionCountSQL, filterDTO, "i3", "Date", "i3", true)
            + " GROUP BY Day_Of_Month";

    String finalSQL =
        "SELECT cost.Day_Of_Month, ((cost.Daily_Cost * 1.0 / impression.Total_Impressions) * 1000) AS CPM "
            + "FROM ("
            + unionCost
            + ") cost JOIN ("
            + impressionCountSQL
            + ") impression ON cost.Day_Of_Month = impression.Day_Of_Month ORDER BY cost.Day_Of_Month";
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "CPM");
    return ChartFactory.createLineChart("CPM Per Day", "Day", "CPM", dataset);
  }

  public JFreeChart filteredBounceRateChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        "SELECT strftime('%Y-%m-%d', s.EntryDate) AS day, COUNT(*) AS bounces_by_day "
            + "FROM serverLog s JOIN impressionLog i ON s.ID = i.ID WHERE s.PagesViewed = 1 ";
    String finalSQL =
        applyCommonFilter(baseSQL, filterDTO, "s", "EntryDate", "i", true)
            + " GROUP BY day ORDER BY day";
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Bounces");
    return ChartFactory.createLineChart("Bounces Per Day", "Day", "Bounces", dataset);
  }

  public JFreeChart clickCostHistogram(int numBins) {
    HistogramDataset histogramDataset =
        getHistogramDataset("SELECT clickCost FROM clickLog", "Click Cost Distribution", numBins);
    return ChartFactory.createHistogram(
        "Histogram of Click Costs", "Cost Intervals", "Frequency", histogramDataset);
  }

  public JFreeChart impressionsChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Impressions FROM impressionLog GROUP BY Day ORDER BY Day",
            "Impressions By Day");
    return ChartFactory.createLineChart("Impressions Per Day", "Day", "Impressions", dataset);
  }

  public JFreeChart clicksChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Clicks FROM clickLog GROUP BY Day ORDER BY Day",
            "Clicks");
    return ChartFactory.createLineChart("Clicks Per Day", "Day", "Clicks", dataset);
  }

  public JFreeChart uniquesChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(DISTINCT ID) AS Total_Unique_IDs FROM impressionLog GROUP BY Day ORDER BY Day",
            "Uniques");
    return ChartFactory.createLineChart("Uniques Per Day", "Day", "Uniques", dataset);
  }

  public JFreeChart bouncesChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', EntryDate) AS Day, COUNT(*) AS Total_Bounces FROM serverLog WHERE PagesViewed = 1 GROUP BY Day ORDER BY Day",
            "Bounces");
    return ChartFactory.createLineChart("Bounces Per Day", "Day", "Bounces", dataset);
  }

  public JFreeChart conversionsChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', EntryDate) AS Day, COUNT(*) AS Total_Conversions FROM serverLog WHERE Conversion = 'Yes' GROUP BY Day ORDER BY Day",
            "Conversions");
    return ChartFactory.createLineChart("Conversions Per Day", "Day", "Conversions", dataset);
  }

  public JFreeChart totalCostChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT Day, SUM(Total_Cost) AS Daily_Cost FROM ("
                + "SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(ImpressionCost) AS Total_Cost FROM impressionLog GROUP BY Day "
                + "UNION ALL "
                + "SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(clickCost) AS Total_Cost FROM clickLog GROUP BY Day"
                + ") GROUP BY Day ORDER BY Day",
            "Total Cost");
    return ChartFactory.createLineChart("Total Cost Per Day", "Day", "Total Cost", dataset);
  }

  public JFreeChart ctrChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT imp.Day, (cli.Total_Clicks * 100.0 / imp.Total_Impressions) AS CTR "
                + "FROM (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Impressions FROM impressionLog GROUP BY Day) imp "
                + "INNER JOIN (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Clicks FROM clickLog GROUP BY Day) cli "
                + "ON imp.Day = cli.Day ORDER BY imp.Day",
            "CTR");
    return ChartFactory.createLineChart("CTR Per Day", "Day", "CTR", dataset);
  }

  public JFreeChart cpaChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT total.Day, (total.Daily_Cost / conv.Total_Conversions) AS CPA "
                + "FROM (SELECT Day, SUM(Total_Cost) AS Daily_Cost FROM ("
                + "SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(ImpressionCost) AS Total_Cost FROM impressionLog GROUP BY Day "
                + "UNION ALL "
                + "SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(ClickCost) AS Total_Cost FROM clickLog GROUP BY Day"
                + ") GROUP BY Day) AS total "
                + "INNER JOIN (SELECT strftime('%Y-%m-%d', EntryDate) AS Day, COUNT(*) AS Total_Conversions FROM serverLog WHERE Conversion = 'Yes' GROUP BY Day) AS conv "
                + "ON total.Day = conv.Day ORDER BY total.Day",
            "CPA");
    return ChartFactory.createLineChart("CPA Per Day", "Day", "CPA", dataset);
  }

  public JFreeChart cpcChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT total.Day, (total.Daily_Cost / cli.Total_Clicks) AS CPC "
                + "FROM (SELECT Day, SUM(Total_Cost) AS Daily_Cost FROM ("
                + "SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(ImpressionCost) AS Total_Cost FROM impressionLog GROUP BY Day "
                + "UNION ALL "
                + "SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(ClickCost) AS Total_Cost FROM clickLog GROUP BY Day"
                + ") GROUP BY Day) total "
                + "INNER JOIN (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Clicks FROM clickLog GROUP BY Day) cli "
                + "ON total.Day = cli.Day ORDER BY total.Day",
            "CPC");
    return ChartFactory.createLineChart("CPC Per Day", "Day", "CPC", dataset);
  }

  public JFreeChart cpmChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT c_imp.Day, (c_imp.Total_Cost/imp.Total_Impressions) * 1000 AS CPM "
                + "FROM (SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(ImpressionCost) AS Total_Cost FROM impressionLog GROUP BY Day) AS c_imp "
                + "INNER JOIN (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Impressions FROM impressionLog GROUP BY Day) AS imp "
                + "ON c_imp.Day = imp.Day ORDER BY c_imp.Day",
            "CPM");
    return ChartFactory.createLineChart("CPM Per Day", "Day", "CPM", dataset);
  }

  public JFreeChart bounceRateChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT bounce.Day, (bounce.Total_Bounces * 1.0 / cli.Total_Clicks) AS Bounce_Rate "
                + "FROM (SELECT strftime('%Y-%m-%d', EntryDate) AS Day, COUNT(*) AS Total_Bounces FROM serverLog WHERE PagesViewed = 1 GROUP BY Day) bounce "
                + "INNER JOIN (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Clicks FROM clickLog GROUP BY Day) cli "
                + "ON bounce.Day = cli.Day ORDER BY bounce.Day",
            "Bounce Rate");
    return ChartFactory.createLineChart("Bounce Rate Per Day", "Day", "Bounce Rate", dataset);
  }
}
