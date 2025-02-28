package com.university.grp20.model;

import com.university.grp20.controller.FileUpload;
import javafx.application.Platform;
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

  public void connect() {
    logger.info("Attempting connection") ;

    Connection conn = null;
    var url = "jdbc:sqlite:" + new File("databases/test.db").getAbsolutePath();
    BufferedReader bufferedReader = null;
    String line;
    String[] columnData;
    PreparedStatement statement;
    int columnCounter = 1;

    try {
      conn = DriverManager.getConnection(url);

      logger.info("Connection to SQLite has been established.");

      statement = conn.prepareStatement("DELETE FROM impressionLog");
      statement.executeUpdate();
      logger.info("Deleted existing rows in impression log");

      statement = conn.prepareStatement("INSERT INTO impressionLog (impressionID, Date, ID, Gender, Age, Income, Context, ImpressionCost) values (?, ?, ?, ?, ?, ?, ?, ?)");
      bufferedReader = new BufferedReader(new FileReader(impressionLog));

      // Skip the first line that just has column names
      bufferedReader.readLine();

      conn.setAutoCommit(false);

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

        if (columnCounter % 15000 == 0) {
          statement.executeBatch();
          conn.commit();
          logger.info("Inserted batch of impression log rows");
        }
      }
      statement.executeBatch();
      conn.commit();

      try {
        bufferedReader.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      statement.executeBatch();
      conn.commit();
      conn.setAutoCommit(true);

      // impression end

      columnCounter = 1;

      statement = conn.prepareStatement("DELETE FROM clickLog");
      statement.executeUpdate();
      logger.info("Deleted existing rows in click log");

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

        if (columnCounter % 15000 == 0) {
          statement.executeBatch();
          conn.commit();
          logger.info("Inserted batch of click log rows");
        }
      }
      statement.executeBatch();
      conn.commit();

      try {
        bufferedReader.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      // click end

      columnCounter = 1;

      statement = conn.prepareStatement("DELETE FROM serverLog");
      statement.executeUpdate();
      logger.info("Deleted existing rows in server log");

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

        if (columnCounter % 15000 == 0) {
          statement.executeBatch();
          conn.commit();
          logger.info("Inserted batch of server log rows");
        }
      }
      statement.executeBatch();
      conn.commit();

      try {
        bufferedReader.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

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
  }


