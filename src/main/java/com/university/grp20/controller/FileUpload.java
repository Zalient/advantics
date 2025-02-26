package com.university.grp20.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FileUpload {
  private static final Logger logger = LogManager.getLogger(FileUpload.class);

  @FXML
  private Button impressionUpload;

  @FXML
  private void handleImpressionUpload() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Impression Upload");
    File impressionFile = fileChooser.showOpenDialog(impressionUpload.getScene().getWindow());

    if (impressionFile != null) {
      logger.info("Impression file selected at" + impressionFile.getPath());
    }
    else {
      logger.info("File upload aborted");
    }


  }
}
