package com.university.grp20.controller;

import com.university.grp20.model.FileProcessor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class FileUpload {
  private static final Logger logger = LogManager.getLogger(FileUpload.class);

  private FileProcessor fileProcessor = new FileProcessor();

  private FileChooser fileChooser = new FileChooser();

  @FXML
  private Button impressionUpload;

  @FXML
  private Button clickUpload;

  @FXML
  private Button serverUpload;

  @FXML
  private Button next;

  @FXML
  private ProgressBar fileProgressBar;

  @FXML
  private Label progressLabel;

  @FXML
  private void handleImpressionUpload() {
    fileChooser.setTitle("Impression Log File Upload");
    File impressionFile = fileChooser.showOpenDialog(impressionUpload.getScene().getWindow());

    if (impressionFile != null) {
      logger.info("Impression Log File selected with path " + impressionFile.getPath());
      fileProcessor.setImpressionLog(impressionFile);
      impressionUpload.setStyle("-fx-background-color: #40cf23;");
    }
    else {
      logger.info("File upload aborted");
    }
  }

  @FXML
  private void handleClickUpload() {
    fileChooser.setTitle("Click Log File Upload");
    File clickFile = fileChooser.showOpenDialog(clickUpload.getScene().getWindow());

    if (clickFile != null) {
      logger.info("Click Log File selected with path " + clickFile.getPath());
      fileProcessor.setClickLog(clickFile);
      clickUpload.setStyle("-fx-background-color: #40cf23;");
    }
    else {
      logger.info("File upload aborted");
    }
  }

  @FXML
  private void handleServerUpload() {
    fileChooser.setTitle("Server Log File Upload");
    File serverFile = fileChooser.showOpenDialog(serverUpload.getScene().getWindow());

    if (serverFile != null) {
      logger.info("Server Log File selected with path " + serverFile.getPath());
      fileProcessor.setServerLog(serverFile);
      serverUpload.setStyle("-fx-background-color: #40cf23;");
    }
    else {
      logger.info("File upload aborted");
    }
  }

  @FXML
  private void handleNext(javafx.event.ActionEvent event) throws InterruptedException {
    logger.info("next clicked");

    fileProcessor.setOnUploadStart(this::updateProgressBar);
    fileProcessor.setOnUploadLabelStart(this::updateProgressLabel);

    Boolean ready = fileProcessor.isReady();

    if (ready) {
      impressionUpload.setDisable(true);
      clickUpload.setDisable(true);
      serverUpload.setDisable(true);
      next.setDisable(true);

      Thread insertData = new Thread(() -> {
        try {

          fileProcessor.insertAllData();


          logger.info("3 file paths done");

          Platform.runLater (() -> {
            progressLabel.setText("Calculating Metrics...");
            fileProgressBar.setProgress(1.0);
          });

          // Give scene time to update
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          Platform.runLater(() -> {
            try {
              FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardPage.fxml"));
              Parent root = loader.load();
              Stage stage = (Stage) next.getScene().getWindow();
              stage.setScene(new Scene(root));
              stage.setTitle("Advertising Dashboard");
              stage.show();
            } catch (IOException e) {
              logger.info("Error reading FXML file");
            }
          });




        } catch (Exception e) {
          impressionUpload.setDisable(false);
          clickUpload.setDisable(false);
          serverUpload.setDisable(false);
          next.setDisable(false);
          updateProgressBar(0.0);
          serverUpload.setStyle("-fx-background-color: #ffffff");
          clickUpload.setStyle("-fx-background-color: #ffffff");
          impressionUpload.setStyle("-fx-background-color: #ffffff");
          throw new RuntimeException(e);
        }
      });

      insertData.start();



    }
    else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText("You have not uploaded all 3 files.");
      alert.showAndWait();
    }
  }

  public void updateProgressBar(Double progress) {
    logger.info("Updating progress to " + progress);

    Platform.runLater(() -> {
      fileProgressBar.setProgress(progress);});

  }

  public void updateProgressLabel(String text) {
    logger.info("Updating label to " + text);

    Platform.runLater(() -> {
      progressLabel.setText(text);});

  }
}