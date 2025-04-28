package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.FileImportService;
import com.university.grp20.model.OperationLogger;
import com.university.grp20.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class FileSelectionController extends Navigator {
  @FXML private Button impressionLogButton;
  @FXML private Button clickLogButton;
  @FXML private Button serverLogButton;
  @FXML private Button nextButton;
  @FXML private Button logoutButton;
  @FXML private ProgressBar importProgressBar;
  @FXML private Label importProgressLabel;
  @FXML private Label roleLabel;
  @FXML private Label impressionPathLabel;
  @FXML private Label clickPathLabel;
  @FXML private Label serverPathLabel;
  @FXML private Button skipButton;
  @FXML private Button helpButton;

  private static final Logger logger = LogManager.getLogger(FileSelectionController.class);
  private final FileImportService fileImportService = new FileImportService();
  private final FileChooser fileChooser = new FileChooser();
  private final OperationLogger operationLogger = new OperationLogger();

  @FXML
  public void initialize() {
    Platform.runLater(
        () -> {
          logger.info("Initialising file upload controller");

          logger.info("Role is: " + User.getRole());
          roleLabel.setText("Role: " + User.getRole());

          fileImportService.setOnUploadStart(this::updateProgressBar);
          fileImportService.setOnUploadLabelStart(this::updateProgressLabel);
          fileImportService.setOnFileError(this::handleFileUploadError);
        });
  }

  @FXML
  private void startImport() {
    operationLogger.log("Next button clicked, attempting file upload");
    if (fileImportService.isReady()) {
      fileImportService.setOnUploadStart(this::updateProgressBar);
      fileImportService.setOnUploadLabelStart(this::updateProgressLabel);
      nextButton.setDisable(true);
      impressionLogButton.setDisable(true);
      clickLogButton.setDisable(true);
      serverLogButton.setDisable(true);
      logoutButton.setDisable(true);
      skipButton.setDisable(true);
      Thread importDataThread =
          new Thread(
              () -> {
                try {
                  fileImportService.runFullImport();
                  Platform.runLater(
                      () -> {
                        importProgressLabel.setText("Calculating Metrics...");
                        importProgressBar.setProgress(1.0);
                      });
                  Thread.sleep(100);
                  Platform.runLater(
                      () ->
                          UIManager.switchContent(
                              parentPane,
                              UIManager.createFxmlLoader("/fxml/MetricsPane.fxml"),
                              true));
                  operationLogger.log("Upload success, displaying metrics");
                } catch (Exception e) {
                  Platform.runLater(this::resetUI);
                  throw new RuntimeException(
                      "An error occurred during file processing: " + e.getMessage());
                }
              });
      importDataThread.start();
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText("You have not uploaded the 3 required log files.");
      alert.showAndWait();
      operationLogger.log("Upload failed, log files not uploaded");
    }
  }

  @FXML
  private void handleImpressionUpload() {
    selectFile("Select Impression Log File", fileImportService::setImpressionLog, impressionLogButton);
    operationLogger.log("Impression log file chooser displayed");
  }

  @FXML
  private void handleClickUpload() {
    selectFile("Select Click Log File", fileImportService::setClickLog, clickLogButton);
    operationLogger.log("Click log file chooser displayed");
  }

  @FXML
  private void handleServerUpload() {
    selectFile("Select Server Log File", fileImportService::setServerLog, serverLogButton);
    operationLogger.log("Server log file chooser displayed");
  }

  @FXML
  private void handleLogout() {
    // Reset all the attributes of the user class
    User.logOut();
    operationLogger.log("Logout button clicked");

    UIManager.switchContent(parentPane, UIManager.createFxmlLoader("/fxml/LoginPane.fxml"));
  }

  @FXML
  private void handleSkip() {
    operationLogger.log("Skip upload button clicked");
    if (fileImportService.isDataLoaded()) {
      UIManager.switchContent(
          parentPane, UIManager.createFxmlLoader("/fxml/MetricsPane.fxml"), true);
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText(
          "You cannot Skip if data has not already been uploaded into the database.");
      alert.showAndWait();
      operationLogger.log("Skip button failure, data not yet uploaded to database");
    }
  }

  private void selectFile(String title, Consumer<File> fileSetter, Button sourceButton) {
    fileChooser.setTitle(title);
    File file = fileChooser.showOpenDialog(sourceButton.getScene().getWindow());
    if (file != null) {
      logger.info("Selected file path: " + file.getPath());
      fileSetter.accept(file);

      if (sourceButton == impressionLogButton) {
        impressionPathLabel.setText(file.getPath());
      } else if (sourceButton == clickLogButton) {
        clickPathLabel.setText(file.getPath());
      } else if (sourceButton == serverLogButton) {
        serverPathLabel.setText(file.getPath());
      }

      sourceButton.setStyle("-fx-background-color: #40cf23;");
    } else {
      logger.info(title + " aborted");
    }
    if (fileImportService.isReady()) {
      nextButton.setDisable(false);
    }
  }

  private void handleFileUploadError(String errorMessage) {
    Platform.runLater(
        () -> {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error!");
          alert.setHeaderText(null);
          alert.setContentText(errorMessage);
          operationLogger.log("File upload error: " + errorMessage);
          alert.showAndWait();
        });
  }

  private void resetUI() {
    impressionLogButton.setDisable(false);
    clickLogButton.setDisable(false);
    serverLogButton.setDisable(false);
    nextButton.setDisable(true);
    logoutButton.setDisable(false);
    skipButton.setDisable(false);
    impressionPathLabel.setText("File Path");
    clickPathLabel.setText("File Path");
    serverPathLabel.setText("File Path");
    updateProgressBar(0.0);
    importProgressLabel.setText("");
    importProgressBar.setProgress(0);

    // Resets the style of the buttons to use the css file (removes the green background)
    impressionLogButton.setStyle("");
    clickLogButton.setStyle("");
    serverLogButton.setStyle("");

    fileImportService.deleteInsertedData();
    fileImportService.setImpressionLog(null);
    fileImportService.setServerLog(null);
    fileImportService.setClickLog(null);
    fileImportService.setCampaignStartDate("");
  }

  public void updateProgressBar(Double progress) {
    logger.info("Updating progress to " + progress);
    Platform.runLater(() -> importProgressBar.setProgress(progress));
  }

  public void updateProgressLabel(String text) {
    logger.info("Updating progress label to " + text);
    Platform.runLater(() -> importProgressLabel.setText(text));
  }

  @FXML
  private void showHelpGuide() {
    FXMLLoader loader = UIManager.createFxmlLoader("/fxml/HelpGuidePane.fxml");
    try {
      loader.load();
      HelpGuideController helpController = loader.getController();
      helpController.setupCarousel("Upload");
      UIManager.showModalStage("Upload Page Help Guide", loader, false);
      operationLogger.log("Upload Page Help Guide Icon clicked");
    } catch (IOException e) {
      logger.error("Failed to open Help Guide", e);
    }
  }
}
