package com.university.grp20.model;

import com.university.grp20.controller.FileProgressBarListener;
import com.university.grp20.controller.FileProgressLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileProcessor
{
  private static final Logger logger = LogManager.getLogger(FileProcessor.class);

  private File impressionLog ;
  private File clickLog ;
  private File serverLog ;

  private int batchCount = 15000;

  private FileProgressBarListener fileProgressBarListener;

  private FileProgressLabel fileProgressLabel;

  public void setImpressionLog(File newImpressionLog) {
    this.impressionLog = newImpressionLog;
  }

  public void setClickLog(File newClickLog) {
    this.clickLog = newClickLog;
  }

  public void setServerLog(File newServerLog) {
    this.serverLog = newServerLog;
  }

  public boolean isReady() {
    return impressionLog != null && clickLog != null && serverLog != null;
  }

  public Connection connectDatabase() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:./statsDatabase.db");
      setupDatabase(conn);
      logger.info("Database connected");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return conn;
  }

  public void setupDatabase (Connection conn) {
    try {
      PreparedStatement statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS impressionLog (" +
              "impressionID INTEGER, " +
              "Date DATETIME, " +
              "ID LONG, " +
              "Gender TEXT, " +
              "Age TEXT, " +
              "Income TEXT, " +
              "Context TEXT, " +
              "ImpressionCost REAL);");
      statement.executeUpdate();
      logger.info("Created impressionLog table");

      statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS clickLog (" +
              "clickID INTEGER, " +
              "Date DATETIME, " +
              "ID LONG, " +
              "ClickCost REAL);");
      statement.executeUpdate();
      logger.info("Created clickLog table");

      statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS serverLog (" +
              "serverID INTEGER, " +
              "EntryDate DATETIME, " +
              "ID LONG, " +
              "ExitDate DATETIME, " +
              "PagesViewed INTEGER, " +
              "Conversion TEXT);");
      statement.executeUpdate();
      logger.info("Created serverLog table");


    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  public void insertImpression(Connection conn) {
    try {
      int lineCount = 0;
      int columnCounter = 1;
      String line = "";
      String[] columnData;

      fileProgressLabel.labelText("Uploading impression file");

      PreparedStatement statement = conn.prepareStatement("DELETE FROM impressionLog");
      statement.executeUpdate();
      logger.info("Deleted existing rows in impression log");

      BufferedReader bufferedReader = new BufferedReader(new FileReader(impressionLog));

      while ((line = bufferedReader.readLine()) != null) {
        lineCount++;
      }

      logger.info(lineCount + " lines found in impression file");

      try {
        bufferedReader.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      statement = conn.prepareStatement("INSERT INTO impressionLog (impressionID, Date, ID, Gender, Age, Income, Context, ImpressionCost) values (?, ?, ?, ?, ?, ?, ?, ?)");
      bufferedReader = new BufferedReader(new FileReader(impressionLog));

      // Skip the first line that just has column names
      bufferedReader.readLine();

      while ((line = bufferedReader.readLine()) != null) {
        columnData = line.split(",");

        statement.setInt(1, columnCounter); // PRIMARY KEY
        statement.setString(2, columnData[0]); // Date
        statement.setLong(3, Long.parseLong(columnData[1])); // ID
        statement.setString(4, columnData[2]); // Gender
        statement.setString(5, columnData[3]); // Age
        statement.setString(6, columnData[4]); // Income
        statement.setString(7, columnData[5]); // Context
        statement.setDouble(8, Double.parseDouble(columnData[6])); // Impression Cost

        statement.addBatch();
        columnCounter++;

        if (columnCounter % batchCount == 0) {
          //REMOVE IF ABANDON
          fileProgressBarListener.FileProgressBar(((double) columnCounter / (double) lineCount));

          statement.executeBatch();
          conn.commit();
          logger.info("Inserted batch of impression log rows");
        }
      }
      statement.executeBatch();
      conn.commit();
      fileProgressBarListener.FileProgressBar(1.0);

      try {
        bufferedReader.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      statement.executeBatch();
      conn.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void insertClick(Connection conn) throws IOException, SQLException {
    int lineCount = 0;
    int columnCounter = 1;
    String line = "";
    String[] columnData;

    fileProgressLabel.labelText("Uploading click file");

    PreparedStatement statement = conn.prepareStatement("DELETE FROM clickLog");
    statement.executeUpdate();
    logger.info("Deleted existing rows in click log");

    BufferedReader bufferedReader = new BufferedReader(new FileReader(clickLog));

    while ((line = bufferedReader.readLine()) != null) {
      lineCount++;
    }

    logger.info(lineCount + " lines found in click file");

    try {
      bufferedReader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    statement = conn.prepareStatement("INSERT INTO clickLog (clickID, Date, ID, ClickCost) values (?, ?, ?, ?)");
    bufferedReader = new BufferedReader(new FileReader(clickLog));

    // Skip the first line that just has column names
    bufferedReader.readLine();

    while ((line = bufferedReader.readLine()) != null) {
      columnData = line.split(",");

      statement.setInt(1, columnCounter); // PRIMARY KEY
      statement.setString(2, columnData[0]); // Date
      statement.setLong(3, Long.parseLong(columnData[1])); // ID
      statement.setDouble(4, Double.parseDouble(columnData[2])); // ClickCost

      statement.addBatch();
      columnCounter++;

      if (columnCounter % batchCount == 0) {
        fileProgressBarListener.FileProgressBar(((double) columnCounter / (double) lineCount));

        statement.executeBatch();
        conn.commit();
        logger.info("Inserted batch of click log rows");
      }
    }
    statement.executeBatch();
    conn.commit();
    fileProgressBarListener.FileProgressBar(1.0);

    try {
      bufferedReader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void insertServer(Connection conn) throws SQLException, IOException {
    int lineCount = 0;
    int columnCounter = 1;
    String line = "";
    String[] columnData;

    fileProgressLabel.labelText("Uploading server file");

    PreparedStatement statement = conn.prepareStatement("DELETE FROM serverLog");
    statement.executeUpdate();
    logger.info("Deleted existing rows in server log");

    BufferedReader bufferedReader = new BufferedReader(new FileReader(serverLog));

    while ((line = bufferedReader.readLine()) != null) {
      lineCount++;
    }

    logger.info(lineCount + " lines found in click file");

    try {
      bufferedReader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    statement = conn.prepareStatement("INSERT INTO serverLog (serverID, EntryDate, ID, ExitDate, PagesViewed, Conversion) values (?, ?, ?, ?, ?, ?)");
    bufferedReader = new BufferedReader(new FileReader(serverLog));

    // Skip the first line that just has column names
    bufferedReader.readLine();

    while ((line = bufferedReader.readLine()) != null) {
      columnData = line.split(",");

      statement.setInt(1, columnCounter); // PRIMARY KEY
      statement.setString(2, columnData[0]); // EntryDate
      statement.setLong(3, Long.parseLong(columnData[1])); // ID
      statement.setString(4, columnData[2]); // ExitDate
      statement.setInt(5, Integer.parseInt(columnData[3])); // PagesViewed
      statement.setString(6, columnData[4]); // Conversion

      statement.addBatch();
      columnCounter++;

      if (columnCounter % batchCount == 0) {
        fileProgressBarListener.FileProgressBar(((double) columnCounter / (double) lineCount));

        statement.executeBatch();
        conn.commit();
        logger.info("Inserted batch of server log rows");
      }
    }
    statement.executeBatch();
    conn.commit();
    fileProgressBarListener.FileProgressBar(1.0);

    try {
      bufferedReader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void insertAllData() {
    logger.info("Attempting connection") ;

    Connection conn = null;
    BufferedReader bufferedReader = null;
    String line;
    String[] columnData;
    PreparedStatement statement;
    int columnCounter = 1;

    try {

      conn = connectDatabase();

      conn.setAutoCommit(false);

      insertImpression(conn);

      insertClick(conn);

      insertServer(conn);

      conn.setAutoCommit(true);

    } catch (SQLException | IOException e) {
      System.out.println(e.getMessage());
    } finally {
      if (conn != null) {
        try {
          //Attempt to close the connection to the database
          conn.close();
          logger.info("Connection to database has been closed");
        } catch (SQLException e) {
          System.out.println(e.getMessage());
        }
      }
    }

  }

  public void setOnUploadStart(FileProgressBarListener listener) {
    this.fileProgressBarListener = listener;
  }

  public void setOnUploadLabelStart(FileProgressLabel listener) {
    this.fileProgressLabel = listener;
  }
}