package com.university.grp20.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBHelper {
  private static final int BATCH_SIZE = 15000;
  private static HikariDataSource dataSource;
  private static Connection testConnection = null;

  public static void changeDatabase(String campaignName) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:sqlite:./" + campaignName + ".db");
    config.setDriverClassName("org.sqlite.JDBC");
    config.setMaximumPoolSize(10);
    config.setConnectionTestQuery("SELECT 1");
    config.setPoolName("HikariCP-Pool");

    dataSource = new HikariDataSource(config);
  }

  public static Connection getConnection(String campaignName) throws SQLException {
    if (testConnection != null) {
      return testConnection;
    }
    changeDatabase(campaignName);
    return dataSource.getConnection();
  }

  public static void useTestConnection(Connection conn) {
    testConnection = conn;
  }

  public static void resetTestConnection() {
    testConnection = null;
  }

  public static ResultSet executeQuery(Connection conn, String sql, Object... params)
      throws SQLException {
    PreparedStatement pstmt = conn.prepareStatement(sql);
    for (int i = 0; i < params.length; i++) {
      pstmt.setObject(i + 1, params[i]);
    }
    return pstmt.executeQuery();
  }

  public static int executeUpdate(Connection conn, String sql, Object... params)
      throws SQLException {
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) {
        pstmt.setObject(i + 1, params[i]);
      }
      return pstmt.executeUpdate();
    }
  }

  public static int executeBatchUpdate(Connection conn, String sql, List<Object[]> paramList)
      throws SQLException {
    int totalAffected = 0;
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      for (int i = 0; i < paramList.size(); i++) {
        Object[] params = paramList.get(i);
        for (int j = 0; j < params.length; j++) {
          pstmt.setObject(j + 1, params[j]);
        }
        pstmt.addBatch();
        if ((i + 1) % BATCH_SIZE == 0 || (i + 1) == paramList.size()) {
          int[] batchResults = pstmt.executeBatch();
          conn.commit();
          totalAffected += batchResults.length;
        }
      }
    }
    return totalAffected;
  }

  public static int getBatchSize() {
    return BATCH_SIZE;
  }

  public static LocalDate fetchMinDate() {
    String query = "SELECT MIN(Date)" + " FROM impressionLog";
    try (Connection conn = getConnection(campaignName)) {
      return fetchDate(conn, query);
    } catch (SQLException e) {
      throw new RuntimeException("Unable to obtain DB connection", e);
    }
  }

  public static LocalDate fetchMaxDate() {
    String query = "SELECT MAX(Date)" + " FROM impressionLog";
    try (Connection conn = getConnection(campaignName)) {
      return fetchDate(conn, query);
    } catch (SQLException e) {
      throw new RuntimeException("Unable to obtain DB connection", e);
    }
  }

  private static LocalDate fetchDate(Connection conn, String query) {
    try (ResultSet rs = executeQuery(conn, query)) {
      if (rs.next()) {
        return rs.getDate(1).toLocalDate();
      }
      throw new RuntimeException("No rows returned: " + query);
    } catch (SQLException e) {
      throw new RuntimeException("Error executing query: " + query, e);
    }
  }
}
