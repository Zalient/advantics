package com.university.grp20.model;

import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.sql.*;

public class ChartDatasetGetter {
    public DefaultCategoryDataset getDataset(String query, String metricLine){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + new File("databases/test.db").getAbsolutePath());
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
}