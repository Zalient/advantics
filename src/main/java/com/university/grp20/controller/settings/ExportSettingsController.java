package com.university.grp20.controller.settings;

import com.university.grp20.UIManager;
import com.university.grp20.model.ExportLogService;
import com.university.grp20.model.OperationLogger;
import javafx.fxml.FXML;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportSettingsController {
  private final ExportLogService exportLogService = new ExportLogService();
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
  OperationLogger operationLogger = new OperationLogger();

  @FXML
  private void exportOperationsPdf() {
    operationLogger.log("Export user operation to PDF button clicked");
    String logFileName = OperationLogger.getLogFileName();
    String timestamp = LocalDateTime.now().format(formatter);
    String filePath = "operationLogs/exported_log_" + timestamp + ".pdf";
    exportLogService.exportLogToPDF(logFileName, filePath);
    operationLogger.log( "Successfully exported as PDF to" + filePath);
    UIManager.showAlert("Success", "Log exported to PDF\nSaved at: " + filePath);
  }

  @FXML
  private void exportOperationsCsv() {
    operationLogger.log("Export user operation to CSV button clicked");
    String logFileName = OperationLogger.getLogFileName();
    String timestamp = LocalDateTime.now().format(formatter);
    String filePath = "operationLogs/exported_log_" + timestamp + ".csv";
    exportLogService.exportLogToCSV(logFileName, filePath);
    operationLogger.log( "Successfully exported as CSV to" + filePath);
    UIManager.showAlert("Success", "Log exported to CSV\nSaved at: " + filePath);
  }
}
