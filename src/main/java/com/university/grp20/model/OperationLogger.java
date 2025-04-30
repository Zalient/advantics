package com.university.grp20.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OperationLogger {
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
  private static String logFileName;
  private final Logger logger = LogManager.getLogger(OperationLogger.class);

  public void initialize() {
    String timestamp = LocalDateTime.now().format(formatter);
    logFileName = "operationLogs/" + "userLog_" + timestamp + ".txt";

    try {
      new java.io.File("operationLogs/").mkdirs();

      try (PrintWriter pw = new PrintWriter(new FileWriter(logFileName, true))) {
        pw.println("Session Started: " + timestamp);
      }
    } catch (IOException e) {
      logger.error("Error initialising OperationLogger: " + e);
    }
    log("Operation logger initialized");
  }

  public void log(String action) {
    if (logFileName == null) {
      logger.info("logFileName not initialized, skipping logging.");
      return;
    }
    try (FileWriter fw = new FileWriter(logFileName, true);
         PrintWriter pw = new PrintWriter(fw)) {

      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      pw.println("[" + timestamp + "] " + action);
    } catch (IOException e) {
      logger.error("Error logging operation " + action + ": " + e);
    }
  }

  public static String getLogFileName() {
    return logFileName;
  }
}
