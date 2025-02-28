package com.university.grp20.model;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartGeneratorModel {
    ChartDatasetGetter datasetGetter = new ChartDatasetGetter();

    public JFreeChart impressionsChart() {
        DefaultCategoryDataset impressionsDataset = new ChartDatasetGetter().getDataset(
                "SELECT strftime('%Y-%m-%d', Date) AS day, " +
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
                "SELECT strftime('%Y-%m-%d', Date) AS day, " +
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
                "SELECT strftime('%Y-%m-%d', Date) AS day, " +
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
                "SELECT strftime('%Y-%m-%d', \"Entry Date\") AS day, " +
                        "COUNT(*) AS clicks_by_day " +
                        "FROM serverLog " +
                        "WHERE \"Pages Viewed\" = 1 " +
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
                "SELECT strftime('%Y-%m-%d', \"Entry Date\") AS day, " +
                        "COUNT(*) AS clicks_by_day " +
                        "FROM serverLog " +
                        "WHERE \"Conversion\" = 'Yes' " +
                        "GROUP BY day " +
                        "ORDER BY day",
                ""
        );
        return ChartFactory.createLineChart("Conversions Per Day",
                "Day",
                "Conversions",
                conversionsDataset);
    }

    public JFreeChart totalCostChart() {
        DefaultCategoryDataset totalCostDataset = new ChartDatasetGetter().getDataset(
                "",
                ""
        );
        return ChartFactory.createLineChart("Total Cost Per Day",
                "Day",
                "Total Cost",
                totalCostDataset);
    }

    public JFreeChart ctrChart() {
        DefaultCategoryDataset ctrDataset = new ChartDatasetGetter().getDataset(
                "",
                ""
        );
        return ChartFactory.createLineChart("CTR Per Day",
                "Day",
                "CTR",
                ctrDataset);
    }

    public JFreeChart cpaChart() {
        DefaultCategoryDataset cpaDataset = new ChartDatasetGetter().getDataset(
                "",
                ""
        );
        return ChartFactory.createLineChart("CPA Per Day",
                "Day",
                "CPA",
                cpaDataset);
    }

    public JFreeChart cpcChart() {
        DefaultCategoryDataset cpcDataset = new ChartDatasetGetter().getDataset(
                "",
                ""
        );
        return ChartFactory.createLineChart("CPC Per Day",
                "Day",
                "CPC",
                cpcDataset);
    }

    public JFreeChart cpmChart() {
        DefaultCategoryDataset cpmDataset = new ChartDatasetGetter().getDataset(
                "",
                ""
        );
        return ChartFactory.createLineChart("CPM Per Day",
                "Day",
                "CPM",
                cpmDataset);
    }

    public JFreeChart bounceRateChart() {
        DefaultCategoryDataset bounceRateDataset = new ChartDatasetGetter().getDataset(
                "",
                ""
        );
        return ChartFactory.createLineChart("Bounce Rate Per Day",
                "Day",
                "Bounce Rate",
                bounceRateDataset);
    }
}
