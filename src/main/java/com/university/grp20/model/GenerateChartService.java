package com.university.grp20.model;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.List;

public class GenerateChartService {
  private final Logger logger = LogManager.getLogger(GenerateChartService.class);
  private Integer numOfDays;
  public void setNumOfDays(int days){
    numOfDays = days;
  }

  private static DefaultCategoryDataset getCategoryDataset(String query, String metricLine) {
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

  private static HistogramDataset getHistogramDataset(
      String query, String metricLine, int binSize) {
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

  public String applyCommonFilter(
          String baseSQL,
          FilterCriteriaDTO filterDTO,
          String dateAlias,
          String dateColumn,
          String userAlias) {

    StringBuilder sb = new StringBuilder(baseSQL);

    if (filterDTO.startDate() != null) {
      sb.append(" AND ")
          .append(dateAlias)
          .append(".")
          .append(dateColumn)
          .append(" >= '")
          .append(filterDTO.startDate())
          .append("'");
    }
    if (filterDTO.endDate() != null) {
      sb.append(" AND ")
          .append(dateAlias)
          .append(".")
          .append(dateColumn)
          .append(" <= '")
          .append(filterDTO.endDate())
          .append("'");
    }

    if (userAlias != null && !userAlias.isEmpty()) {

      if (filterDTO.gender() != null) {
        sb.append(" AND ")
            .append(userAlias)
            .append(".Gender = '")
            .append(filterDTO.gender())
            .append("'");
      }
      if (filterDTO.ageRanges() != null && !filterDTO.ageRanges().isEmpty()) {
        sb.append(" AND ").append(userAlias).append(".Age IN (");
        for (int i = 0; i < filterDTO.ageRanges().size(); i++) {
          String age = filterDTO.ageRanges().get(i);
          if ("Below 25".equals(age)) {
            age = "<25";
          } else if ("Above 54".equals(age)) {
            age = ">54";
          }
          sb.append("'").append(age).append("'");
          if (i < filterDTO.ageRanges().size() - 1) {
            sb.append(", ");
          }
        }
        sb.append(")");
      }
      if (filterDTO.incomes() != null && !filterDTO.incomes().isEmpty()) {
        sb.append(" AND ").append(userAlias).append(".Income IN (");
        sb.append(
            filterDTO.incomes().stream()
                .map(income -> "'" + income + "'")
                .collect(Collectors.joining(", ")));
        sb.append(")");
      }
      if (filterDTO.contexts() != null && !filterDTO.contexts().isEmpty()) {
        sb.append(" AND ").append(userAlias).append(".Context IN (");
        sb.append(
            filterDTO.contexts().stream()
                .map(context -> "'" + context + "'")
                .collect(Collectors.joining(", ")));
        sb.append(")");
      }
      if (filterDTO.timeGranularity() == null
          || filterDTO.timeGranularity().isEmpty()
          || "Per Day".equals(filterDTO.timeGranularity())) {
        sb.append(" GROUP BY STRFTIME('%Y-%m-%d', ")
            .append(dateAlias)
            .append(".")
            .append(dateColumn)
            .append(") ");
      } else if ("Per Week".equals(filterDTO.timeGranularity())) {
        sb.append(" GROUP BY STRFTIME('%Y-%W', ")
            .append(dateAlias)
            .append(".")
            .append(dateColumn)
            .append(") ");
      } else if ("Per Hour".equals(filterDTO.timeGranularity())) {
        sb.append(" GROUP BY STRFTIME('%Y-%m-%d %H', ")
            .append(dateAlias)
            .append(".")
            .append(dateColumn)
            .append(") ");
      }
    }
    return sb.toString();
  }

  private String applyGranularitySelect(
      FilterCriteriaDTO filterDTO, String dateAlias, String dateColumn) {
    String selectStmt;
    if ("Per Hour".equals(filterDTO.timeGranularity())) {
      selectStmt =
          "SELECT strftime('%Y-%m-%d %H:00:00', " + dateAlias + "." + dateColumn + ") AS time";
    } else if ("Per Week".equals(filterDTO.timeGranularity())) {
      selectStmt = "SELECT strftime('%Y-%W', " + dateAlias + "." + dateColumn + ") AS time";
    } else {
      selectStmt = "SELECT strftime('%Y-%m-%d', " + dateAlias + "." + dateColumn + ") AS time";
    }
    return selectStmt;
  }

  private static String applyBounceDef() {
    String bounceStmt = "";
    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
    String bounceType = globalSettings.getBounceType();
    String bounceValue = globalSettings.getBounceValue();
    if (bounceType.equals("Pages Viewed")) {
      bounceStmt = "s.PagesViewed <= " + bounceValue;
    } else if (bounceType.equals("Time Spent on Page")) {
      bounceStmt = "s.TimeSpent >= 0 AND s.TimeSpent < " + bounceValue;
    }
    return bounceStmt;
  }

  private String granularityLabel(FilterCriteriaDTO filterDTO) {
    String granularityLabel;
    if ("Per Hour".equals(filterDTO.timeGranularity())) {
      granularityLabel = "Hour";
    } else if ("Per Week".equals(filterDTO.timeGranularity())) {
      granularityLabel = "Week";
    } else {
      granularityLabel = "Day";
    }
    return granularityLabel;
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

  private void configXAxisLabels (JFreeChart chart, FilterCriteriaDTO filterDTO) {
    if (numOfDays == null) {
      getNumOfDays();
    }
    logger.info("Day count: " + numOfDays);
    if (chart.getPlot() instanceof CategoryPlot categoryPlot) {
      CategoryAxis xAxis = categoryPlot.getDomainAxis();
      xAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));

      String timeGranularity = filterDTO.timeGranularity();
      if (timeGranularity != null && timeGranularity.equals("Per Hour")) {
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) categoryPlot.getDataset();
        List<?> categories = dataset.getColumnKeys();
        int step = (int) Math.ceil((numOfDays * 24.0) / 15.0);

        for (int i = 0; i < categories.size(); i++) {
          String label = categories.get(i).toString();
          if (i % 120 != 0) {
            xAxis.setTickLabelPaint(label, new Color(0, 0, 0, 0));
          }
        }
      }
    }
  }

  private void getNumOfDays (){
    String query = "SELECT COUNT(DISTINCT strftime('%Y-%m-%d', Date)) AS count FROM clickLog";
    try (Connection conn = DBHelper.getConnection();
         ResultSet rs = DBHelper.executeQuery(conn, query)) {
      if (rs != null && rs.next()) {
        numOfDays = rs.getInt("count");

      }
    } catch (SQLException e) {
      throw new RuntimeException("Error fetching number of days", e);
    }
  }

  private static void showDefaultFilters(JFreeChart chart){
    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
    String filterApplied = "Time Granularity: Per Day   Start Date: []   End Date: []\n" +
            "Gender: All Gender   Age Ranges: []   Income: []   Contexts: []\n" +
            "Bounce Type " + globalSettings.getBounceType() + "   Bounce Value: " + globalSettings.getBounceValue();
    TextTitle subtitle = new TextTitle(filterApplied);
    subtitle.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    subtitle.setPaint(new Color(0, 0, 140));
    chart.addSubtitle(subtitle);
  }

  public JFreeChart filteredImpressionsChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        applyGranularitySelect(filterDTO, "i", "Date")
            + ", COUNT(*) AS impressions_by_day "
            + "FROM impressionLog i "
            + "JOIN userData u ON u.ID = i.ID "
            + "WHERE 1=1 ";
    String finalSQL = applyCommonFilter(baseSQL, filterDTO, "i", "Date", "u") + " ORDER BY time";

    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Impressions");
    String label = granularityLabel(filterDTO);
    JFreeChart chart =
        ChartFactory.createLineChart("Impressions Per " + label, label, "Impressions", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredClicksChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        applyGranularitySelect(filterDTO, "c", "Date")
            + ", COUNT(*) AS clicks_by_day "
            + "FROM clickLog c "
            + "JOIN userData u ON u.ID = c.ID "
            + "WHERE 1=1 ";
    String finalSQL = applyCommonFilter(baseSQL, filterDTO, "c", "Date", "u") + " ORDER BY time";

    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Clicks");
    String label = granularityLabel(filterDTO);
    JFreeChart chart =
        ChartFactory.createLineChart("Clicks Per " + label, label, "Clicks", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredUniquesChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        applyGranularitySelect(filterDTO, "i", "Date")
            + ", COUNT(DISTINCT i.ID) AS uniques_by_day "
            + "FROM impressionLog i "
            + "JOIN userData u ON u.ID = i.ID "
            + "WHERE 1=1 ";
    String finalSQL = applyCommonFilter(baseSQL, filterDTO, "i", "Date", "u") + " ORDER BY time";

    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Uniques");
    String label = granularityLabel(filterDTO);
    JFreeChart chart =
        ChartFactory.createLineChart("Uniques Per " + label, label, "Uniques", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredBouncesChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        applyGranularitySelect(filterDTO, "s", "EntryDate")
            + ", COUNT(*) AS bounces_by_day "
            + "FROM serverLog s "
            + "JOIN userData u ON u.ID = s.ID "
            + "WHERE 1=1 "
            + "AND "
            + applyBounceDef();
    String finalSQL =
        applyCommonFilter(baseSQL, filterDTO, "s", "EntryDate", "u") + " ORDER BY time";

    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Bounces");
    String label = granularityLabel(filterDTO);
    JFreeChart chart =
        ChartFactory.createLineChart("Bounces Per " + label, label, "Bounces", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredConversionsChart(FilterCriteriaDTO filterDTO) {
    String baseSQL =
        applyGranularitySelect(filterDTO, "s", "EntryDate")
            + ", COUNT(*) AS conversions_by_day "
            + "FROM serverLog s "
            + "JOIN userData u ON u.ID = s.ID "
            + "WHERE s.Conversion = 'Yes' ";
    String finalSQL =
        applyCommonFilter(baseSQL, filterDTO, "s", "EntryDate", "u") + " ORDER BY time";

    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Conversions");
    String label = granularityLabel(filterDTO);
    JFreeChart chart =
        ChartFactory.createLineChart("Conversions Per " + label, label, "Conversions", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredTotalCostChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        applyGranularitySelect(filterDTO, "i", "Date")
            + ", SUM(i.ImpressionCost) AS total_amount "
            + "FROM impressionLog i "
            + "JOIN userData u ON u.ID = i.ID "
            + "WHERE 1=1 ";
    impressionSQL = applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "u");

    String clickSQL =
        applyGranularitySelect(filterDTO, "c", "Date")
            + ", SUM(c.ClickCost) AS total_amount "
            + "FROM clickLog c "
            + "JOIN userData u ON u.ID = c.ID "
            + "WHERE 1=1 ";

    clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "u");

    String finalSQL =
        "SELECT time, SUM(total_amount) AS combined_total FROM ("
            + impressionSQL
            + " UNION ALL "
            + clickSQL
            + ") AS daily_total_cost GROUP BY time ORDER BY time";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Total Cost");
    String label = granularityLabel(filterDTO);
    JFreeChart chart =
        ChartFactory.createLineChart("Total Cost Per " + label, label, "Total Cost", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredCTRChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        applyGranularitySelect(filterDTO, "i", "Date")
            + ", COUNT(*) AS Num_Of_Imp "
            + "FROM impressionLog i "
            + "JOIN userData u ON u.ID = i.ID "
            + "WHERE 1=1 ";
    impressionSQL = applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "u");

    String clickSQL =
        applyGranularitySelect(filterDTO, "c", "Date")
            + ", COUNT(*) AS Num_Of_Clicks "
            + "FROM clickLog c "
            + "JOIN userData u ON u.ID = c.ID "
            + "WHERE 1=1 ";

    clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "u");

    String finalSQL =
        "SELECT imp.time, (cli.Num_Of_Clicks * 100.0 / imp.Num_Of_Imp) AS ctr "
            + "FROM ("
            + impressionSQL
            + ") imp JOIN ("
            + clickSQL
            + ") cli ON imp.time = cli.time ORDER BY imp.time";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "CTR");
    String label = granularityLabel(filterDTO);
    JFreeChart chart = ChartFactory.createLineChart("CTR Per " + label, label, "CTR", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredCPAChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        applyGranularitySelect(filterDTO, "i", "Date")
            + ", SUM(i.ImpressionCost) AS total_cost "
            + "FROM impressionLog i "
            + "JOIN userData u ON u.ID = i.ID "
            + "WHERE 1=1 ";

    impressionSQL = applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "u");

    String clickSQL =
        applyGranularitySelect(filterDTO, "c", "Date")
            + ", SUM(c.ClickCost) AS total_cost "
            + "FROM clickLog c "
            + "JOIN userData u ON u.ID = c.ID "
            + "WHERE 1=1 ";

    clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "u");

    String unionCost =
        "SELECT time, SUM(total_cost) AS Daily_Cost FROM ("
            + impressionSQL
            + " UNION ALL "
            + clickSQL
            + ") GROUP BY time";

    String conversionSQL =
        applyGranularitySelect(filterDTO, "s", "EntryDate")
            + ", COUNT(*) AS Total_Conversions "
            + "FROM serverLog s "
            + "JOIN userData u ON u.ID = s.ID "
            + "WHERE s.Conversion = 'Yes' ";

    conversionSQL = applyCommonFilter(conversionSQL, filterDTO, "s", "EntryDate", "u");

    String finalSQL =
        "SELECT cost.time, (cost.Daily_Cost * 1.0 / conv.Total_Conversions) AS CPA "
            + "FROM ("
            + unionCost
            + ") cost JOIN ("
            + conversionSQL
            + ") conv ON cost.time = conv.time ORDER BY cost.time";

    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "CPA");
    String label = granularityLabel(filterDTO);
    JFreeChart chart = ChartFactory.createLineChart("CPA Per " + label, label, "CPA", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredCPCChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        applyGranularitySelect(filterDTO, "i", "Date")
            + ", SUM(i.ImpressionCost) AS total_cost "
            + "FROM impressionLog i "
            + "JOIN userData u ON u.ID = i.ID "
            + "WHERE 1=1 ";
    impressionSQL = applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "u");

    String clickSQL =
        applyGranularitySelect(filterDTO, "c", "Date")
            + ", SUM(c.ClickCost) AS total_cost "
            + "FROM clickLog c "
            + "JOIN userData u ON u.ID = c.ID "
            + "WHERE 1=1 ";
    clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "u");

    String unionCost =
        "SELECT time, SUM(total_cost) AS Daily_Cost FROM ("
            + impressionSQL
            + " UNION ALL "
            + clickSQL
            + ") GROUP BY time";

    String totalClickSQL =
        applyGranularitySelect(filterDTO, "c2", "Date")
            + ", COUNT(*) AS Total_Clicks "
            + "FROM clickLog c2 "
            + "JOIN userData u ON u.ID = c2.ID "
            + "WHERE 1=1 ";
    totalClickSQL = applyCommonFilter(totalClickSQL, filterDTO, "c2", "Date", "u");

    String finalSQL =
        "SELECT cost.time, (cost.Daily_Cost * 1.0 / clicks.Total_Clicks) AS CPC "
            + "FROM ("
            + unionCost
            + ") cost JOIN ("
            + totalClickSQL
            + ") clicks ON cost.time = clicks.time ORDER BY cost.time";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "CPC");
    String label = granularityLabel(filterDTO);
    JFreeChart chart = ChartFactory.createLineChart("CPC Per " + label, label, "CPC", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredCPMChart(FilterCriteriaDTO filterDTO) {
    String impressionSQL =
        applyGranularitySelect(filterDTO, "i", "Date")
            + ", SUM(i.ImpressionCost) AS total_cost "
            + "FROM impressionLog i "
            + "JOIN userData u ON u.ID = i.ID "
            + "WHERE 1=1 ";
    impressionSQL = applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "u");

    String impressionCountSQL =
        applyGranularitySelect(filterDTO, "i2", "Date")
            + ", COUNT(*) AS Total_Impressions "
            + "FROM impressionLog i2 "
            + "JOIN userData u ON u.ID = i2.ID "
            + "WHERE 1=1 ";
    impressionCountSQL = applyCommonFilter(impressionCountSQL, filterDTO, "i2", "Date", "u");

    String finalSQL =
        "SELECT cost.time, ((cost.total_cost * 1.0 / impression.Total_Impressions) * 1000) AS CPM "
            + "FROM ("
            + impressionSQL
            + ") cost JOIN ("
            + impressionCountSQL
            + ") impression ON cost.time = impression.time ORDER BY cost.time";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "CPM");
    String label = granularityLabel(filterDTO);
    JFreeChart chart = ChartFactory.createLineChart("CPM Per " + label, label, "CPM", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public JFreeChart filteredBounceRateChart(FilterCriteriaDTO filterDTO) {
    String bounceSQL =
        applyGranularitySelect(filterDTO, "s", "EntryDate")
            + ", COUNT(*) AS Total_Bounces "
            + "FROM serverLog s "
            + "JOIN userData u ON u.ID = s.ID "
            + "WHERE 1=1 "
            + "AND "
            + applyBounceDef();
    bounceSQL = applyCommonFilter(bounceSQL, filterDTO, "s", "EntryDate", "u");

    String clickSQL =
        applyGranularitySelect(filterDTO, "c", "Date")
            + ", COUNT(*) AS Total_Clicks "
            + "FROM clickLog c "
            + "JOIN userData u ON u.ID = c.ID ";

    clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "u");

    String finalSQL =
        "SELECT bounces.time, "
            + "(bounces.Total_Bounces * 1.0 / NULLIF(clicks.Total_Clicks, 0)) AS Bounce_Rate "
            + "FROM ("
            + bounceSQL
            + ") bounces "
            + "JOIN ("
            + clickSQL
            + ") clicks "
            + "ON bounces.time = clicks.time "
            + "ORDER BY bounces.time";
    logger.info(finalSQL);
    DefaultCategoryDataset dataset = getCategoryDataset(finalSQL, "Bounce Rate");
    String label = granularityLabel(filterDTO);
    JFreeChart chart =
        ChartFactory.createLineChart("Bounce Rate Per " + label, label, "Bounce Rate", dataset);
    configXAxisLabels(chart, filterDTO);
    return chart;
  }

  public static JFreeChart clickCostHistogram(int numBins) {
    HistogramDataset histogramDataset =
        getHistogramDataset("SELECT clickCost FROM clickLog", "Click Cost Distribution", numBins);
    return ChartFactory.createHistogram(
        "Histogram of Click Costs", "Cost Intervals", "Frequency", histogramDataset);
  }

  public static JFreeChart impressionsChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Impressions FROM impressionLog GROUP BY Day ORDER BY Day",
            "Impressions By Day");
    JFreeChart chart = ChartFactory.createLineChart("Impressions Per Day", "Day", "Impressions", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart clicksChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Clicks FROM clickLog GROUP BY Day ORDER BY Day",
            "Clicks");
    JFreeChart chart = ChartFactory.createLineChart("Clicks Per Day", "Day", "Clicks", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart uniquesChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(DISTINCT ID) AS Total_Unique_IDs FROM impressionLog GROUP BY Day ORDER BY Day",
            "Uniques");
    JFreeChart chart = ChartFactory.createLineChart("Uniques Per Day", "Day", "Uniques", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart bouncesChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', EntryDate) AS Day, COUNT(*) AS Total_Bounces FROM serverLog s WHERE "
                + applyBounceDef()
                + " GROUP BY Day ORDER BY Day",
            "Bounces");
    JFreeChart chart =  ChartFactory.createLineChart("Bounces Per Day", "Day", "Bounces", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart conversionsChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT strftime('%Y-%m-%d', EntryDate) AS Day, COUNT(*) AS Total_Conversions FROM serverLog WHERE Conversion = 'Yes' GROUP BY Day ORDER BY Day",
            "Conversions");
    JFreeChart chart =  ChartFactory.createLineChart("Conversions Per Day", "Day", "Conversions", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart totalCostChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT Day, SUM(Total_Cost) AS Daily_Cost FROM ("
                + "SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(ImpressionCost) AS Total_Cost FROM impressionLog GROUP BY Day "
                + "UNION ALL "
                + "SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(clickCost) AS Total_Cost FROM clickLog GROUP BY Day"
                + ") GROUP BY Day ORDER BY Day",
            "Total Cost");
    JFreeChart chart =  ChartFactory.createLineChart("Total Cost Per Day", "Day", "Total Cost", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart ctrChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT imp.Day, (cli.Total_Clicks * 100.0 / imp.Total_Impressions) AS CTR "
                + "FROM (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Impressions FROM impressionLog GROUP BY Day) imp "
                + "INNER JOIN (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Clicks FROM clickLog GROUP BY Day) cli "
                + "ON imp.Day = cli.Day ORDER BY imp.Day",
            "CTR");
    JFreeChart chart =  ChartFactory.createLineChart("CTR Per Day", "Day", "CTR", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart cpaChart() {
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
    JFreeChart chart =  ChartFactory.createLineChart("CPA Per Day", "Day", "CPA", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart cpcChart() {
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
    JFreeChart chart =  ChartFactory.createLineChart("CPC Per Day", "Day", "CPC", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart cpmChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT c_imp.Day, (c_imp.Total_Cost/imp.Total_Impressions) * 1000 AS CPM "
                + "FROM (SELECT strftime('%Y-%m-%d', Date) AS Day, SUM(ImpressionCost) AS Total_Cost FROM impressionLog GROUP BY Day) AS c_imp "
                + "INNER JOIN (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Impressions FROM impressionLog GROUP BY Day) AS imp "
                + "ON c_imp.Day = imp.Day ORDER BY c_imp.Day",
            "CPM");
    JFreeChart chart =  ChartFactory.createLineChart("CPM Per Day", "Day", "CPM", dataset);
    showDefaultFilters(chart);
    return chart;
  }

  public static JFreeChart bounceRateChart() {
    DefaultCategoryDataset dataset =
        getCategoryDataset(
            "SELECT bounce.Day, (bounce.Total_Bounces * 1.0 / cli.Total_Clicks) AS Bounce_Rate "
                + "FROM (SELECT strftime('%Y-%m-%d', EntryDate) AS Day, COUNT(*) AS Total_Bounces FROM serverLog s WHERE "
                + applyBounceDef()
                + " GROUP BY Day) bounce "
                + "INNER JOIN (SELECT strftime('%Y-%m-%d', Date) AS Day, COUNT(*) AS Total_Clicks FROM clickLog GROUP BY Day) cli "
                + "ON bounce.Day = cli.Day ORDER BY bounce.Day",
            "Bounce Rate");
    JFreeChart chart =  ChartFactory.createLineChart("Bounce Rate Per Day", "Day", "Bounce Rate", dataset);
    showDefaultFilters(chart);
    return chart;
  }
}
