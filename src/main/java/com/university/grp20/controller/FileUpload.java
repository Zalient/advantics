package com.university.grp20.controller;

import com.university.grp20.model.FileProcessor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
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
  private void handleImpressionUpload() {
    fileChooser.setTitle("Impression Log File Upload");
    File impressionFile = fileChooser.showOpenDialog(impressionUpload.getScene().getWindow());

    if (impressionFile != null) {
      logger.info("Impression Log File selected with path " + impressionFile.getPath());
      fileProcessor.setImpressionLog(impressionFile);
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
    }
    else {
      logger.info("File upload aborted");
    }
  }

  @FXML
  private void handleNext(javafx.event.ActionEvent event) {
    logger.info("next clicked");

    Boolean ready = fileProcessor.isReady();

    if (ready) { //replace true w/ ready
      logger.info ("3 file paths done");

      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) next.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Advertising Dashboard");
        stage.show();
      } catch (IOException e) {
        logger.info("Error reading FXML file") ;
      }
    }
    else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText("You have not upload all 3 files.");
      alert.showAndWait();
    }
  }
}
