package com.university.grp20.controller;

import com.university.grp20.model.OperationLogger;
import com.university.grp20.model.ExportLogService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SettingsController {

    private ExportLogService exportLogService = new ExportLogService();
    private OperationLogger operationLogger = new OperationLogger();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @FXML
    public void handleExportLogToPDF(ActionEvent event) {
        String logFileName = operationLogger.getLogFileName();
        String timestamp = LocalDateTime.now().format(formatter);
        //Gives the timestamp in the filename once exported
        String filePath = "operationLogs/exported_log_" + timestamp + ".pdf";
        exportLogService.exportLogToPDF(logFileName, filePath);
        showAlert("Success", "Log exported to PDF successfully.\nSaved at: " + filePath);
    }

    @FXML
    public void handleExportLogToCSV(ActionEvent event) {
        String logFileName = operationLogger.getLogFileName();
        String timestamp = LocalDateTime.now().format(formatter);
        //Gives the timestamp in the filename once exported
        String filePath = "operationLogs/exported_log_" + timestamp + ".csv";
        exportLogService.exportLogToCSV(logFileName, filePath);
        showAlert("Success", "Log exported to CSV successfully.\nSaved at: " + filePath);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
