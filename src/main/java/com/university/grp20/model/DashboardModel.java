package com.university.grp20.model;

import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.sql.*;

public class DashboardModel {
    public double rawMetricGetter(String query) {
        double result = 0.0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + new File("databases/test.db").getAbsolutePath());
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet queryRes = statement.executeQuery()) {

            if (queryRes.next()) {
                result = queryRes.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
