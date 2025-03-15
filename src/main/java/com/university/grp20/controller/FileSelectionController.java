package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.FileImportService;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.university.grp20.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileSelectionController {
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

  private static final Logger logger = LogManager.getLogger(FileSelectionController.class);
  private final FileImportService fileImportService = new FileImportService();
  private final FileChooser fileChooser = new FileChooser();

  @FXML
  public void initialize() {
    Platform.runLater(() -> {
      logger.info("Initialising file upload controller");

      logger.info("Role is: " + User.getRole());
      roleLabel.setText("Role: " + User.getRole());

      fileImportService.setOnUploadStart(this::updateProgressBar);
      fileImportService.setOnUploadLabelStart(this::updateProgressLabel);

    });
  }

  @FXML
  private void startImport() {
    fileImportService.setOnUploadStart(this::updateProgressBar);
    fileImportService.setOnUploadLabelStart(this::updateProgressLabel);
    nextButton.setDisable(true);
    impressionLogButton.setDisable(true);
    clickLogButton.setDisable(true);
    serverLogButton.setDisable(true);
    logoutButton.setDisable(true);
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
                        UIManager.switchScene(
                            UIManager.createFXMLLoader("/fxml/MetricsScene.fxml")));
              } catch (Exception e) {
                Platform.runLater(this::resetUI);
                throw new RuntimeException(
                    "An error occurred during file processing: " + e.getMessage());
              }
            });
    importDataThread.start();
  }

  @FXML
  private void handleImpressionUpload() {
    selectFile(
        "Select Impression Log File", fileImportService::setImpressionLog, impressionLogButton);
  }

  @FXML
  private void handleClickUpload() {
    selectFile("Select Click Log File", fileImportService::setClickLog, clickLogButton);
  }

  @FXML
  private void handleServerUpload() {
    selectFile("Select Server Log File", fileImportService::setServerLog, serverLogButton);
  }

  @FXML
  private void handleLogout() {
    // Reset all the attributes of the user class
    User.logOut();

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginScene.fxml"));
      Parent root = loader.load();
      Stage stage = (Stage) nextButton.getScene().getWindow();
      stage.setScene(new Scene(root));
      stage.setTitle("Advertising Dashboard");
      stage.show();
    } catch (IOException e) {
      logger.info("Error reading FXML file");
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

  private void resetUI() {
    impressionLogButton.setDisable(false);
    clickLogButton.setDisable(false);
    serverLogButton.setDisable(false);
    nextButton.setDisable(false);
    logoutButton.setDisable(false);
    updateProgressBar(0.0);
    impressionLogButton.setStyle("-fx-background-color: #D9D9D9");
    clickLogButton.setStyle("-fx-background-color: #D9D9D9");
    serverLogButton.setStyle("-fx-background-color: #D9D9D9");
    importProgressLabel.setText("");
    importProgressBar.setProgress(0);
  }

  public void updateProgressBar(Double progress) {
    logger.info("Updating progress to " + progress);
    Platform.runLater(() -> importProgressBar.setProgress(progress));
  }

  public void updateProgressLabel(String text) {
    logger.info("Updating progress label to " + text);
    Platform.runLater(() -> importProgressLabel.setText(text));
  }
}
