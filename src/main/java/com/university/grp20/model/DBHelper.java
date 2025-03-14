package com.university.grp20.model;

import java.sql.*;
import java.util.List;

public class DBHelper {
  private static final String URL = "jdbc:sqlite:./statsDatabase.db";
  private static final int BATCH_SIZE = 15000;

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL);
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
}
