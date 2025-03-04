package com.university.grp20.model;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;

public class ChartGeneratorModel {
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
