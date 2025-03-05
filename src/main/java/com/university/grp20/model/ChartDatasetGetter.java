package com.university.grp20.model;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;

import java.io.File;
import java.sql.*;
import java.util.*;

public class ChartDatasetGetter {
    public DefaultCategoryDataset getDataset(String query, String metricLine){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:./statsDatabase.db");
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet queryRes = statement.executeQuery();

            while (queryRes.next()) {
                String x = queryRes.getString(1);
                double y = queryRes.getDouble(2);
                dataset.addValue(y, metricLine, String.valueOf(x));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataset;
    }

    public HistogramDataset getHistogramDataset(String query, String metricLine, int binSize){
        List<Double> clickCosts = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:./statsDatabase.db");
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet queryRes = statement.executeQuery();

            while (queryRes.next()) {
                clickCosts.add(queryRes.getDouble(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        double[] clickCostsArray = new double[clickCosts.size()];
        for (int i = 0; i < clickCosts.size(); i++) {
            clickCostsArray[i] = clickCosts.get(i);
        }
        HistogramDataset histogramDataset = new HistogramDataset();
        histogramDataset.addSeries(metricLine, clickCostsArray, binSize);
        return histogramDataset;
    }
}