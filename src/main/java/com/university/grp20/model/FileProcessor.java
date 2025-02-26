package com.university.grp20.model;

import com.university.grp20.controller.FileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.DriverManager;
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

  public static void connect() {
    logger.info("Attempting connection") ;

    // connection string
    var url = "jdbc:sqlite:" + new File("databases/test.db").getAbsolutePath();

    try (var conn = DriverManager.getConnection(url)) {
      System.out.println("Connection to SQLite has been established.");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
