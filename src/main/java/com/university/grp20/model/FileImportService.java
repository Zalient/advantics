package com.university.grp20.model;

import com.university.grp20.controller.FileProgressBarListener;
import com.university.grp20.controller.FileProgressLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileImportService {
  private final Logger logger = LogManager.getLogger(FileImportService.class);
  private File impressionLog;
  private File clickLog;
  private File serverLog;
  private FileProgressBarListener fileProgressBarListener;
  private FileProgressLabel fileProgressLabel;

  public boolean isReady() {
    return impressionLog != null && clickLog != null && serverLog != null;
  }

  @FunctionalInterface
  private interface LineParser {
    Object[] parse(String[] columns, int counter);
  }

  private void processFile(
      File file,
      String label,
      String deleteSql,
      String insertSql,
      LineParser parser,
      Connection conn) {
    try {
      DBHelper.executeUpdate(conn, deleteSql);
      logger.info("Deleted existing rows for table associated with file: " + file.getName());
    } catch (SQLException e) {
      throw new RuntimeException(
          "Error clearing table for file " + file.getName() + ": " + e.getMessage(), e);
    }

    fileProgressLabel.labelText(label);

    long totalBytes = file.length();
    long bytesRead = 0;

    List<Object[]> batchParams = new ArrayList<>();
    int counter = 1;
    final int updateThreshold = DBHelper.getBatchSize();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String header = reader.readLine();
      if (header != null) {
        bytesRead += header.length() + 1;
      }

      String line;
      while ((line = reader.readLine()) != null) {
        bytesRead += line.length() + 1;
        String[] columns = line.split(",");
        Object[] params = parser.parse(columns, counter);
        batchParams.add(params);
        counter++;

        if (batchParams.size() >= updateThreshold) {
          DBHelper.executeBatchUpdate(conn, insertSql, batchParams);
          batchParams.clear();
          double progress = Math.min((double) bytesRead / totalBytes, 1.0);
          fileProgressBarListener.fileProgressBar(progress);
        }
      }
      if (!batchParams.isEmpty()) {
        DBHelper.executeBatchUpdate(conn, insertSql, batchParams);
      }
      fileProgressBarListener.fileProgressBar(1.0);
    } catch (IOException | SQLException e) {
      throw new RuntimeException(
          "Error processing file " + file.getName() + ": " + e.getMessage(), e);
    }
  }

  private void importImpressionLog(Connection conn) {
    String deleteSql = "DELETE FROM impressionLog";
    String insertSql =
        "INSERT INTO impressionLog (impressionID, Date, ID, Gender, Age, Income, Context, ImpressionCost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    processFile(
        impressionLog,
        "Importing impression log...",
        deleteSql,
        insertSql,
        (columns, counter) ->
            new Object[] {
              counter,
              columns[0],
              Long.parseLong(columns[1]),
              columns[2],
              columns[3],
              columns[4],
              columns[5],
              Double.parseDouble(columns[6])
            },
        conn);
  }

  private void importClickLog(Connection conn) {
    String deleteSql = "DELETE FROM clickLog";
    String insertSql = "INSERT INTO clickLog (clickID, Date, ID, ClickCost) VALUES (?, ?, ?, ?)";
    processFile(
        clickLog,
        "Importing click log...",
        deleteSql,
        insertSql,
        (columns, counter) ->
            new Object[] {
              counter, columns[0], Long.parseLong(columns[1]), Double.parseDouble(columns[2])
            },
        conn);
  }

  private void importServerLog(Connection conn) {
    String deleteSql = "DELETE FROM serverLog";
    String insertSql =
        "INSERT INTO serverLog (serverID, EntryDate, ID, ExitDate, PagesViewed, Conversion) VALUES (?, ?, ?, ?, ?, ?)";
    processFile(
        serverLog,
        "Importing server log...",
        deleteSql,
        insertSql,
        (columns, counter) ->
            new Object[] {
              counter,
              columns[0],
              Long.parseLong(columns[1]),
              columns[2],
              Integer.parseInt(columns[3]),
              columns[4]
            },
        conn);
  }

  private void createTables(Connection conn) {
    try {
      DBHelper.executeUpdate(
          conn,
          "CREATE TABLE IF NOT EXISTS impressionLog ("
              + "impressionID INTEGER, "
              + "Date DATETIME, "
              + "ID LONG, "
              + "Gender TEXT, "
              + "Age TEXT, "
              + "Income TEXT, "
              + "Context TEXT, "
              + "ImpressionCost REAL);");
      logger.info("Created impressionLog table");

      DBHelper.executeUpdate(
          conn,
          "CREATE TABLE IF NOT EXISTS clickLog ("
              + "clickID INTEGER, "
              + "Date DATETIME, "
              + "ID LONG, "
              + "ClickCost REAL);");
      logger.info("Created clickLog table");

      DBHelper.executeUpdate(
          conn,
          "CREATE TABLE IF NOT EXISTS serverLog ("
              + "serverID INTEGER, "
              + "EntryDate DATETIME, "
              + "ID LONG, "
              + "ExitDate DATETIME, "
              + "PagesViewed INTEGER, "
              + "Conversion TEXT);");
      logger.info("Created serverLog table");

      conn.commit();
    } catch (SQLException e) {
      throw new RuntimeException("Error creating tables: " + e.getMessage());
    }
  }

  public void runFullImport() {
    try (Connection conn = DBHelper.getConnection()) {
      conn.setAutoCommit(false);
      createTables(conn);
      importImpressionLog(conn);
      importClickLog(conn);
      importServerLog(conn);
      conn.setAutoCommit(true);
      logger.info("Data import complete");
    } catch (SQLException e) {
      throw new RuntimeException("Error during import: " + e.getMessage(), e);
    }
  }

  public void setOnUploadStart(FileProgressBarListener listener) {
    this.fileProgressBarListener = listener;
  }

  public void setOnUploadLabelStart(FileProgressLabel listener) {
    this.fileProgressLabel = listener;
  }

  public void setImpressionLog(File newImpressionLog) {
    this.impressionLog = newImpressionLog;
  }

  public void setClickLog(File newClickLog) {
    this.clickLog = newClickLog;
  }

  public void setServerLog(File newServerLog) {
    this.serverLog = newServerLog;
  }
}
