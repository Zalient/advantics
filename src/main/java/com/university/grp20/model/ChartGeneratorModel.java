package com.university.grp20.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;

public class ChartGeneratorModel {
    private final Logger logger = LogManager.getLogger(ChartGeneratorModel.class);

    private String applyCommonFilter(String baseSQL, ChartFilterDTO filterDTO, String dateAlias, String dateColumn, String impressionAlias, boolean hasImpression)
    {
        StringBuilder sb = new StringBuilder(baseSQL);

        if (filterDTO.getStartDate() != null) {
            sb.append(" AND ")
                    .append(dateAlias).append(".").append(dateColumn)
                    .append(">= '").append(filterDTO.getStartDate()).append("'");
        }
        if (filterDTO.getEndDate() != null) {
            sb.append(" AND ")
                    .append(dateAlias).append(".").append(dateColumn)
                    .append("<= '").append(filterDTO.getEndDate()).append("'");
        }
        if (hasImpression && impressionAlias != null && !impressionAlias.isEmpty()) {
            if (filterDTO.getGender() != null) {
                sb.append(" AND ")
                        .append(impressionAlias).append(".Gender = '")
                        .append(filterDTO.getGender()).append("'");
            }
            if (filterDTO.getAgeRanges() != null && !filterDTO.getAgeRanges().isEmpty()) {
                sb.append(" AND ").append(impressionAlias).append(".Age IN (");
                for (int i = 0; i < filterDTO.getAgeRanges().size(); i++) {
                    String age = filterDTO.getAgeRanges().get(i);
                    if (age.equals("Below 25")) {
                        age = "<25";
                    } else if (age.equals("Above 54")) {
                        age = ">54";
                    }
                    sb.append("'").append(age).append("'");

                    if (i < filterDTO.getAgeRanges().size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append(")");
            }
            if (filterDTO.getIncomes() != null && !filterDTO.getIncomes().isEmpty()) {
                sb.append(" AND ").append(impressionAlias).append(".Income IN (");
                for (int i = 0; i < filterDTO.getIncomes().size(); i++) {
                    sb.append("'").append(filterDTO.getIncomes().get(i)).append("'");
                    if (i < filterDTO.getIncomes().size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }
            if (filterDTO.getContexts() != null && !filterDTO.getContexts().isEmpty()) {
                sb.append(" AND ").append(impressionAlias).append(".Context IN (");
                for (int i = 0; i < filterDTO.getContexts().size(); i++) {
                    sb.append("'").append(filterDTO.getContexts().get(i)).append("'");
                    if (i < filterDTO.getContexts().size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }
            if (filterDTO.getTimeGranularity() == null || filterDTO.getTimeGranularity().isEmpty()) {
                sb.append(" GROUP BY STRFTIME('%Y-%m-%d', ").append(dateAlias).append(".").append(dateColumn).append(") ");
            } else if (filterDTO.getTimeGranularity().equals("Per Day")) {
                sb.append(" GROUP BY STRFTIME('%Y-%m-%d', ").append(dateAlias).append(".").append(dateColumn).append(") ");
            } else if (filterDTO.getTimeGranularity().equals("Per Week")) {
                sb.append(" GROUP BY STRFTIME('%Y-%W', ").append(dateAlias).append(".").append(dateColumn).append(") ");
            }
        } return sb.toString();
    }

    public JFreeChart getFilteredChart(String metricType, ChartFilterDTO filterDTO) {
        return switch (metricType) {
            case "Impressions" -> filteredImpressionsChart(filterDTO);
            case "Clicks" -> filteredClicksChart(filterDTO);
            case "Uniques" -> filteredUniquesChart(filterDTO);
            case "Bounces" -> filteredBouncesChart(filterDTO);
            case "Conversions" -> filteredConversionsChart(filterDTO);
            case "Total Cost" -> filteredTotalCostChart(filterDTO);
            case "CTR" -> filteredCTRChart(filterDTO);
            case "CPA" -> filteredCPAChart(filterDTO);
            case "CPC" -> filteredCPCChart(filterDTO);
            case "CPM" -> filteredCPMChart(filterDTO);
            case "Bounce Rate" -> filteredBounceRateChart(filterDTO);
            default -> {
                System.out.println("Unknown metricType: " + metricType + ". Returning null chart.");
                yield null;
            }
        };
    }

    public JFreeChart filteredImpressionsChart(ChartFilterDTO filterDTO) {
        String baseSQL =
                "SELECT strftime('%Y-%m-%d', Date) AS day, COUNT(*) AS impressions_by_day " +
                        "FROM impressionLog i " +
                        "WHERE 1=1 ";
        String finalSQL = applyCommonFilter(baseSQL, filterDTO,
                "i", "Date",
                "i", true);
        finalSQL += "ORDER BY day";
        logger.info(finalSQL);
        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "Impressions");
        return ChartFactory.createLineChart("Impressions Per Day", "Day", "Impressions", dataset);
    }

    public JFreeChart filteredClicksChart(ChartFilterDTO filterDTO) {
        String baseSQL =
                "SELECT strftime('%Y-%m-%d', c.Date) AS day, COUNT(*) AS clicks_by_day " +
                        "FROM clickLog c " +
                        "JOIN impressionLog i ON c.ID = i.ID " +
                        "WHERE 1=1 ";
        String finalSQL = applyCommonFilter(baseSQL, filterDTO,
                "c", "Date",
                "i", true);
        finalSQL += "ORDER BY day";
        logger.info(finalSQL);
        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "Clicks");
        return ChartFactory.createLineChart("Clicks Per Day", "Day", "Clicks", dataset);
    }

    public JFreeChart filteredUniquesChart(ChartFilterDTO filterDTO) {
        String baseSQL =
                "SELECT strftime('%Y-%m-%d', Date) AS day, COUNT(DISTINCT i.ID) AS uniques_by_day " +
                        "FROM impressionLog i " +
                        "WHERE 1=1 ";
        String finalSQL = applyCommonFilter(baseSQL, filterDTO,
                "i", "Date",
                "i", true);
        finalSQL += "ORDER BY day";
        logger.info(finalSQL);
        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "Uniques");
        return ChartFactory.createLineChart("Uniques Per Day", "Day", "Uniques", dataset);
    }

    public JFreeChart filteredBouncesChart(ChartFilterDTO filterDTO) {
        String baseSQL =
                "SELECT strftime('%Y-%m-%d', EntryDate) AS day, COUNT(*) AS bounces_by_day " +
                        "FROM serverLog s " +
                        "JOIN impressionLog i ON s.ID = i.ID " +
                        "WHERE s.PagesViewed = 1 ";
        String finalSQL = applyCommonFilter(baseSQL, filterDTO,
                "s", "EntryDate",
                "i", true);
        finalSQL += "ORDER BY day";
        logger.info(finalSQL);
        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "Bounces");
        return ChartFactory.createLineChart("Bounces Per Day", "Day", "Bounces", dataset);
    }

    public JFreeChart filteredConversionsChart(ChartFilterDTO filterDTO) {
        String baseSQL =
                "SELECT strftime('%Y-%m-%d', EntryDate) AS day, COUNT(*) AS clicks_by_day " +
                        "FROM serverLog s " +
                        "JOIN impressionLog i ON s.ID = i.ID " +
                        "WHERE s.Conversion = 'Yes' ";
        String finalSQL = applyCommonFilter(baseSQL, filterDTO,
                "s", "EntryDate",
                "i", true);
        finalSQL += "ORDER BY day";
        logger.info(finalSQL);
        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "Conversions");
        return ChartFactory.createLineChart("Conversions Per Day", "Day", "Conversions", dataset);
    }

    public JFreeChart filteredTotalCostChart(ChartFilterDTO filterDTO) {
        String impressionSQL = "SELECT STRFTIME('%d', i.Date) AS day, SUM(i.ImpressionCost) AS total_amount " +
                "FROM impressionLog i " +
                "WHERE 1=1 ";
        impressionSQL = applyCommonFilter(impressionSQL, filterDTO, "i", "Date",
                "i", true);
        impressionSQL += " GROUP BY day";
        String clickSQL =
                "SELECT STRFTIME('%d', c.Date) AS day, SUM(c.ClickCost) AS total_amount " +
                        "FROM clickLog c " +
                        "JOIN impressionLog i2 ON c.ID = i2.ID " +
                        "WHERE 1=1 ";
        clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date",
                "i2", true);
        clickSQL += " GROUP BY day";
        String finalSQL = "SELECT day, SUM(total_amount) AS combined_total " +
                "FROM (" + impressionSQL + " UNION ALL " + clickSQL + ") AS daily_total_cost " +
                "GROUP BY day ORDER by day ";

        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "Total Cost");
        return ChartFactory.createLineChart("Total Cost Per Day", "Day", "Total Cost", dataset);
    }

    public JFreeChart filteredCTRChart(ChartFilterDTO filterDTO) {
        String impressionSQL =
                "SELECT STRFTIME('%d', i.Date) AS day, COUNT(*) AS Num_Of_Imp " +
                        "FROM impressionLog i " +
                        "WHERE 1=1 ";
        impressionSQL = applyCommonFilter(impressionSQL, filterDTO,
                "i", "Date", "i", true);
        impressionSQL += " GROUP BY Day";

        String clickSQL =
                "SELECT STRFTIME('%d', c.Date) AS Day, COUNT(*) AS Num_Of_Clicks " +
                        "FROM clickLog c " +
                        "JOIN impressionLog i2 ON c.ID = i2.ID " +
                        "WHERE 1=1 ";
        clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "i2", true);
        clickSQL += " GROUP BY Day";

        String finalSQL =
                "SELECT imp.Day, (cli.Num_Of_Clicks * 100.0 / imp.Num_Of_Imp) AS ctr " +
                        "FROM ( " + impressionSQL + " ) imp " +
                        "JOIN ( " + clickSQL + " ) cli ON imp.Day = cli.Day " +
                        "ORDER BY imp.Day ";

        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "CTR");
        return ChartFactory.createLineChart("CTR Per Day", "Day", "CTR", dataset);
    }

    public JFreeChart filteredCPAChart(ChartFilterDTO filterDTO) {
        String impressionSQL =
                "SELECT strftime('%d', i.Date) AS Day_Of_Month, " +
                        "SUM(i.ImpressionCost) AS total_cost " +
                        "FROM impressionLog i " +
                        "WHERE 1=1 ";
        impressionSQL = applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "i", true);
        impressionSQL += " GROUP BY Day_Of_Month";

        String clickSQL =
                "SELECT STRFTIME('%d', c.Date) AS Day_Of_Month, " +
                        "SUM(c.ClickCost) AS total_cost " +
                        "FROM clickLog c " +
                        "JOIN impressionLog i2 ON c.ID = i2.ID " +
                        "WHERE 1=1 ";
        clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "i2", true);
        clickSQL += " GROUP BY Day_Of_Month";

        String unionCost =
                "SELECT Day_Of_Month, SUM(total_cost) AS Daily_Cost " +
                        "FROM ( " + impressionSQL + " UNION ALL " + clickSQL + " ) " +
                        "GROUP BY Day_Of_Month";
        String conversionSQL =
                "SELECT strftime('%d', s.EntryDate) AS Day_Of_Month, " +
                        "COUNT(*) AS Total_Conversions " +
                        "FROM serverLog s " +
                        "JOIN impressionLog i3 ON s.ID = i3.ID " +
                        "WHERE s.Conversion = 'Yes' ";
        conversionSQL = applyCommonFilter(conversionSQL, filterDTO,
                "s", "EntryDate", "i3", true);
        conversionSQL += " GROUP BY Day_Of_Month";

        String finalSQL =
                "SELECT cost.Day_Of_Month, " +
                        "(cost.Daily_Cost * 1.0/ conv.Total_Conversions) AS CPA " +
                        "FROM ( " + unionCost + " ) cost " +
                        "JOIN ( " + conversionSQL + " ) conv ON cost.Day_Of_Month = conv.Day_Of_Month " +
                        "ORDER BY cost.Day_Of_Month ";

        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "CPA");
        return ChartFactory.createLineChart("CPA Per Day", "Day", "CPA", dataset);
    }

    public JFreeChart filteredCPCChart(ChartFilterDTO filterDTO) {
        String impressionSQL =
                "SELECT STRFTIME('%d', i.Date) AS Day_Of_Month, " +
                        "SUM(i.ImpressionCost) AS total_cost " +
                        "FROM impressionLog i " +
                        "WHERE 1=1 ";
        impressionSQL = applyCommonFilter(impressionSQL, filterDTO, "i", "Date", "i", true);
        impressionSQL += " GROUP BY Day_Of_Month";

        String clickSQL =
                "SELECT STRFTIME('%d', c.Date) AS Day_Of_Month, " +
                        "SUM(c.ClickCost) AS total_cost " +
                        "FROM clickLog c " +
                        "JOIN impressionLog i2 ON c.ID = i2.ID " +
                        "WHERE 1=1 ";
        clickSQL = applyCommonFilter(clickSQL, filterDTO, "c", "Date", "i2", true);
        clickSQL += " GROUP BY Day_Of_Month";

        String unionCost =
                "SELECT Day_Of_Month, SUM(total_cost) AS Daily_Cost " +
                        "FROM ( " + impressionSQL + " UNION ALL " + clickSQL + " ) " +
                        "GROUP BY Day_Of_Month";

        String totalClickSQL =
                "SELECT STRFTIME('%d', c2.Date) AS Day_Of_Month, COUNT(*) AS Total_Clicks " +
                        "FROM clickLog c2 " +
                        "JOIN impressionLog i3 ON c2.ID = i3.ID " +
                        "WHERE 1=1 ";
        totalClickSQL = applyCommonFilter(totalClickSQL, filterDTO,
                "c2", "Date", "i3", true);
        totalClickSQL += "GROUP BY Day_Of_Month";

        String finalSQL =
                "SELECT cost.Day_Of_Month, " +
                        "(COALESCE(cost.Daily_Cost, 0) * 1.0 / NULLIF(clicks.Total_Clicks, 0)) AS CPC " +
                        "FROM ( " + unionCost + " ) cost " +
                        "JOIN ( " + totalClickSQL + " ) clicks ON cost.Day_Of_Month = clicks.Day_Of_Month " +
                        "ORDER BY cost.Day_Of_Month";

        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "CPC");
        return ChartFactory.createLineChart("CPC Per Day", "Day", "CPC", dataset);
    }

    public JFreeChart filteredCPMChart(ChartFilterDTO filterDTO) {
        String impressionSQL =
                "SELECT STRFTIME('%d', i.Date) AS Day_Of_Month, " +
                        "SUM(i.ImpressionCost) AS cost_part " +
                        "FROM impressionLog i " +
                        "WHERE 1=1 ";
        impressionSQL = applyCommonFilter(impressionSQL, filterDTO,
                "i", "Date", "i", true);
        impressionSQL += " GROUP BY Day_Of_Month";

        String clickSQL =
                "SELECT STRFTIME('%d', c.Date) AS Day_Of_Month, " +
                        "SUM(c.ClickCost) AS cost_part " +
                        "FROM clickLog c " +
                        "JOIN impressionLog i2 ON c.ID = i2.ID " +
                        "WHERE 1=1 ";
        clickSQL = applyCommonFilter(clickSQL, filterDTO,
                "c", "Date", "i2", true);
        clickSQL += " GROUP BY Day_Of_Month";

        String unionCost =
                "SELECT Day_Of_Month, SUM(cost_part) AS Daily_Cost " +
                        "FROM ( " + impressionSQL + " UNION ALL " + clickSQL + " ) " +
                        "GROUP BY Day_Of_Month";

        String impressionCountSQL =
                "SELECT STRFTIME('%d', i3.Date) AS Day_Of_Month, COUNT(*) AS Total_Impressions " +
                        "FROM impressionLog i3 " +
                        "WHERE 1=1 ";
        impressionCountSQL = applyCommonFilter(impressionCountSQL, filterDTO,
                "i3", "Date", "i3", true);
        impressionCountSQL += "GROUP BY Day_Of_Month";

        String finalSQL =
                "SELECT cost.Day_Of_Month, " +
                        "((cost.Daily_Cost * 1.0/ impression.Total_Impressions) * 1000) AS CPM " +
                        "FROM ( " + unionCost + " ) cost " +
                        "JOIN ( " + impressionCountSQL + " ) impression ON cost.Day_Of_Month = impression.Day_Of_Month " +
                        "ORDER BY cost.Day_Of_Month";

        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "CPM");
        return ChartFactory.createLineChart("CPM Per Day", "Day", "CPM", dataset);
    }

    public JFreeChart filteredBounceRateChart(ChartFilterDTO filterDTO) {
        String baseSQL =
                "SELECT STRFTIME('%d', s.EntryDate) AS day, COUNT(*) AS bounces_by_day " +
                        "FROM serverLog s " +
                        "JOIN impressionLog i ON s.ID = i.ID " +
                        "WHERE s.PagesViewed = 1 ";
        String finalSQL = applyCommonFilter(baseSQL, filterDTO,
                "s", "EntryDate",
                "i", true);
        finalSQL += "GROUP BY day ORDER BY day";

        DefaultCategoryDataset dataset = new ChartDatasetGetter().getDataset(finalSQL, "Bounces");
        return ChartFactory.createLineChart("Bounces Per Day", "Day", "Bounces", dataset);
    }




    public JFreeChart impressionsChart() {
        DefaultCategoryDataset impressionsDataset = new ChartDatasetGetter().getDataset(
                "SELECT strftime('%d', Date) AS day, " +
                        "COUNT(*) AS impressions_by_day " +
                        "FROM impressionLog " +
                        "GROUP BY day " +
                        "ORDER BY day",
                "Impressions By Day"
        );
        return ChartFactory.createLineChart("Impressions Per Day",
                "Day",
                "Impressions",
                impressionsDataset);
    }

    public JFreeChart clicksChart() {
        DefaultCategoryDataset clicksDataset = new ChartDatasetGetter().getDataset(
                "SELECT STRFTIME('%d', Date) AS day, " +
                        "COUNT(*) AS clicks_by_day " +
                        "FROM clickLog " +
                        "GROUP BY day " +
                        "ORDER BY day",
                "Clicks"
        );
        return ChartFactory.createLineChart("Clicks Per Day",
                "Day",
                "Clicks",
                clicksDataset);
    }

    public JFreeChart uniquesChart() {
        DefaultCategoryDataset uniquesDataset = new ChartDatasetGetter().getDataset(
                "SELECT STRFTIME('%d', Date) AS day, " +
                        "COUNT(DISTINCT ID) AS uniques_by_day " +
                        "FROM impressionLog " +
                        "GROUP BY day " +
                        "ORDER BY day",
                "Uniques"
        );
        return ChartFactory.createLineChart("Uniques Per Day",
                "Day",
                "Uniques",
                uniquesDataset);
    }

    public JFreeChart bouncesChart() {
        DefaultCategoryDataset bouncesDataset = new ChartDatasetGetter().getDataset(
                "SELECT STRFTIME('%d', EntryDate) AS day, " +
                        "COUNT(*) AS clicks_by_day " +
                        "FROM serverLog " +
                        "WHERE PagesViewed = 1 " +
                        "GROUP BY day " +
                        "ORDER BY day",
                "Bounces"
        );
        return ChartFactory.createLineChart("Bounces Per Day",
                "Day",
                "Bounces",
                bouncesDataset);
    }

    public JFreeChart conversionsChart() {
        DefaultCategoryDataset conversionsDataset = new ChartDatasetGetter().getDataset(
                "SELECT STRFTIME('%d', EntryDate) AS day, " +
                        "COUNT(*) AS clicks_by_day " +
                        "FROM serverLog " +
                        "WHERE Conversion = 'Yes' " +
                        "GROUP BY day " +
                        "ORDER BY day",
                "Conversions"
        );
        return ChartFactory.createLineChart("Conversions Per Day",
                "Day",
                "Conversions",
                conversionsDataset);
    }

    public JFreeChart totalCostChart() {
        DefaultCategoryDataset totalCostDataset = new ChartDatasetGetter().getDataset(
                "SELECT day, SUM(total_amount) AS combined_total " +
                        "FROM (" +
                        "SELECT STRFTIME('%d', Date) AS day, SUM(ImpressionCost) AS total_amount " +
                        "FROM impressionLog " +
                        "GROUP BY day " +
                        "UNION ALL " +
                        "SELECT STRFTIME('%d', Date) AS day, SUM(clickCost) AS total_amount " +
                        "FROM clickLog " +
                        "GROUP BY day " +
                        ") AS daily_total_cost " +
                        "GROUP BY day " +
                        "ORDER by day ",
                "Total Cost"
        );
        return ChartFactory.createLineChart("Total Cost Per Day",
                "Day",
                "Total Cost",
                totalCostDataset);
    }

    public JFreeChart ctrChart() {
        DefaultCategoryDataset ctrDataset = new ChartDatasetGetter().getDataset(
                "SELECT imp.Day, " +
                        "(cli.Num_Of_Clicks * 100.0 / imp.Num_Of_Imp) AS ctr " +
                        "FROM (" +
                        "SELECT STRFTIME('%d', Date) AS Day, COUNT(*) AS Num_Of_Imp " +
                        "FROM impressionLog " +
                        "GROUP BY Day) imp " +
                        "INNER JOIN (" +
                        "SELECT STRFTIME('%d', Date) AS Day, " +
                        "COUNT(*) AS Num_Of_Clicks " +
                        "FROM clickLog GROUP BY Day) cli " +
                        "ON imp.Day = cli.Day " +
                        "ORDER BY imp.Day;",
                "CTR"
        );
        return ChartFactory.createLineChart("CTR Per Day",
                "Day",
                "CTR",
                ctrDataset);
    }

    public JFreeChart cpaChart() {
        DefaultCategoryDataset cpaDataset = new ChartDatasetGetter().getDataset(
                "SELECT total.Day_Of_Month, (total.Daily_Cost / conv.Total_Conversions) AS CPA " +
                        "FROM ( " +
                        "SELECT Day_Of_Month, SUM(Total_Cost) AS Daily_Cost " +
                        "FROM ( " +
                        "SELECT strftime('%d', Date) AS Day_Of_Month, SUM(ImpressionCost) AS Total_Cost " +
                        "FROM impressionLog " +
                        "GROUP BY Day_Of_Month " +

                        "UNION ALL " +

                        "SELECT strftime('%d', Date) AS Day_Of_Month, SUM(ClickCost) AS Total_Cost " +
                        "FROM clickLog " +
                        "GROUP BY Day_Of_Month" +
                        ") " +
                        "GROUP BY Day_Of_Month) " +
                        "AS total " +

                        "INNER JOIN( " +
                        "SELECT strftime('%d', EntryDate) AS Day_Of_Month, COUNT(*) AS Total_Conversions " +
                        "FROM serverLog " +
                        "WHERE Conversion = 'Yes' " +
                        "GROUP BY Day_Of_Month) " +
                        "AS conv " +

                        "ON total.Day_Of_Month = conv.Day_Of_Month " +
                        "ORDER BY total.Day_Of_Month ",
                "CPA"
        );
        return ChartFactory.createLineChart("CPA Per Day",
                "Day",
                "CPA",
                cpaDataset);
    }

    public JFreeChart cpcChart() {
        DefaultCategoryDataset cpcDataset = new ChartDatasetGetter().getDataset(
                "SELECT total.Day_Of_Month, (total.Daily_Cost / cli.Total_Clicks) AS CPC " +
                        "FROM ( " +
                        "SELECT Day_Of_Month, SUM(Total_Cost) AS Daily_Cost " +
                        "FROM ( " +
                        "SELECT strftime('%d', Date) AS Day_Of_Month, SUM(ImpressionCost) AS Total_Cost " +
                        "FROM impressionLog " +
                        "GROUP BY Day_Of_Month " +
                        "UNION ALL " +
                        "SELECT strftime('%d', Date) AS Day_Of_Month, SUM(ClickCost) AS Total_Cost " +
                        "FROM clickLog " +
                        "GROUP BY Day_Of_Month " +
                        ") " +
                        "GROUP BY Day_Of_Month " +
                        ") total " +
                        "INNER JOIN ( " +
                        "SELECT strftime('%d', Date) AS Day_Of_Month, COUNT(*) AS Total_Clicks " +
                        "FROM clickLog " +
                        "GROUP BY Day_Of_Month " +
                        ") cli " +
                        "ON total.Day_Of_Month = cli.Day_Of_Month " +
                        "ORDER BY total.Day_Of_Month ",
                "CPC"
        );
        return ChartFactory.createLineChart("CPC Per Day",
                "Day",
                "CPC",
                cpcDataset);
    }

    public JFreeChart cpmChart() {
        DefaultCategoryDataset cpmDataset = new ChartDatasetGetter().getDataset(
                "SELECT c_imp.Day_Of_Month, (c_imp.Total_Cost/imp.Total_Impressions) * 1000 AS CPM " +
                        "FROM(" +
                        "SELECT strftime('%d', Date) AS Day_Of_Month, SUM(ImpressionCost) AS Total_Cost " +
                        "FROM impressionLog " +
                        "GROUP BY Day_Of_Month " +
                        ") AS c_imp " +
                        "INNER JOIN ( " +
                        "SELECT strftime('%d', Date) AS Day_Of_Month, COUNT(*) AS Total_Impressions " +
                        "FROM impressionLog " +
                        "GROUP BY Day_Of_Month " +
                        ") AS imp " +
                        "ON c_imp.Day_Of_Month = imp.Day_Of_Month " +
                        "ORDER BY c_imp.Day_Of_Month ",
                ""
        );
        return ChartFactory.createLineChart("CPM Per Day",
                "Day",
                "CPM",
                cpmDataset);
    }

    public JFreeChart bounceRateChart() {
        DefaultCategoryDataset bounceRateDataset = new ChartDatasetGetter().getDataset(
                "SELECT bounce.Day_Of_Month, (bounce.Total_Bounces * 1.0/cli.Total_Clicks) AS Bounce_Rate " +
                        "FROM ( " +
                        "SELECT strftime('%d', EntryDate) AS Day_Of_Month, COUNT(*) AS Total_Bounces " +
                        "FROM serverLog " +
                        "WHERE PagesViewed = 1 " +
                        "GROUP BY Day_Of_Month " +
                        ") bounce " +
                        "INNER JOIN( " +
                        "SELECT strftime('%d', Date) AS Day_Of_Month, COUNT(*) AS Total_Clicks " +
                        "FROM clickLog " +
                        "GROUP BY Day_Of_Month " +
                        ") cli " +
                        "ON bounce.Day_Of_Month = cli.Day_Of_Month " +
                        "ORDER BY bounce.Day_Of_Month ",
                "Bounce Rate"
        );
        return ChartFactory.createLineChart("Bounce Rate Per Day",
                "Day",
                "Bounce Rate",
                bounceRateDataset);
    }

    public JFreeChart clickCostHistogram(int numBins) {
        HistogramDataset histogramDataset = new ChartDatasetGetter().getHistogramDataset(
                "SELECT clickCost FROM clickLog",
                "Click Cost Distribution",
                numBins);

        return ChartFactory.createHistogram(
                "Histogram of Click Costs",
                "Cost Intervals",
                "Frequency",
                histogramDataset);
    }
}