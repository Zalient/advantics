package com.university.grp20.controller.settings;

import com.university.grp20.model.ExportLogService;
import com.university.grp20.model.OperationLogger;
import javafx.fxml.FXML;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportSettingsController {
  private final ExportLogService exportLogService = new ExportLogService();
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

  @FXML
  private void exportOperationsPdf() {
    String logFileName = OperationLogger.getLogFileName();
    String timestamp = LocalDateTime.now().format(formatter);
    String filePath = "operationLogs/exported_log_" + timestamp + ".pdf";
    exportLogService.exportLogToPDF(logFileName, filePath);
  }

  @FXML
  private void exportOperationsCsv() {
    String logFileName = OperationLogger.getLogFileName();
    String timestamp = LocalDateTime.now().format(formatter);
    String filePath = "operationLogs/exported_log_" + timestamp + ".csv";
    exportLogService.exportLogToCSV(logFileName, filePath);
  }
}
