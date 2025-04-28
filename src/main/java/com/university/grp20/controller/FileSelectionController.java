package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.FileImportService;
import com.university.grp20.model.OperationLogger;
import com.university.grp20.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
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
  @FXML private TextField campaignNameTextField;
  @FXML private VBox uploadedCampaignVBox;
  @FXML private Label selectCampaignLabel;

  private static final Logger logger = LogManager.getLogger(FileSelectionController.class);
  private final FileImportService fileImportService = new FileImportService();
  private final FileChooser fileChooser = new FileChooser();
  private final OperationLogger operationLogger = new OperationLogger();

  private Set<String> existingCampaigns = new HashSet<>();

  String campaignName = "";

  @FXML
  public void initialize() {
    Platform.runLater(
        () -> {
          logger.info("Initialising file upload controller");

          logger.info("Role is: " + User.getRole());
          roleLabel.setText("Role: " + User.getRole());

          updateUploadedCampaigns();

          fileImportService.setOnUploadStart(this::updateProgressBar);
          fileImportService.setOnUploadLabelStart(this::updateProgressLabel);
          fileImportService.setOnFileError(this::handleFileUploadError);
        });
  }

  public void updateUploadedCampaigns() {
    System.out.println("Updating uploaded campaigns");
    File currentWorkingDirectory = new File(System.getProperty("user.dir"));

    File[] campaignFiles = currentWorkingDirectory.listFiles(((dir, name) -> name.toLowerCase().endsWith(".db")));

    if (campaignFiles != null) {
      for (File file : campaignFiles) {
        if (!file.getName().equals("users.db")) {
          System.out.println("Found campaign file: " + file.getName());
          String campaignName = file.getName().replace(".db", "");
          existingCampaigns.add(campaignName);
          Button campaignButton = new Button(campaignName);
          campaignButton.getStyleClass().add("blue-button");
          campaignButton.setMaxWidth(Double.MAX_VALUE);
          campaignButton.setStyle("-fx-font-size: 18px;");
          campaignButton.setOnAction(e -> {
            handleSkip(campaignName);
          });
          uploadedCampaignVBox.getChildren().add(campaignButton);
        }
      }
    }

    if (existingCampaigns.isEmpty()) {
      selectCampaignLabel.setText("No campaigns uploaded");
    }

  }

  @FXML
  private void startImport() {
    operationLogger.log("Next button clicked, attempting import");
    if (fileImportService.isReady() && !campaignNameTextField.getText().isEmpty() && !existingCampaigns.contains(campaignNameTextField.getText())) {
      fileImportService.setOnUploadStart(this::updateProgressBar);
      fileImportService.setOnUploadLabelStart(this::updateProgressLabel);
      nextButton.setDisable(true);
      impressionLogButton.setDisable(true);
      clickLogButton.setDisable(true);
      serverLogButton.setDisable(true);
      logoutButton.setDisable(true);
      campaignNameTextField.setDisable(true);
      campaignName = campaignNameTextField.getText();
      Thread importDataThread =
          new Thread(
              () -> {
                try {
                  fileImportService.runFullImport(campaignName);
                  Platform.runLater(
                      () -> {
                        importProgressLabel.setText("Calculating Metrics...");
                        importProgressBar.setProgress(1.0);
                      });
                  Thread.sleep(100);
                  Platform.runLater(() -> {
                    //boolean useCache = campaignName.equals(User.getSelectedCampaign());
                    User.setSelectedCampaign(campaignName);
                    boolean useCache = true;
                    UIManager.switchContent(
                            parentPane,
                            UIManager.createFxmlLoader("/fxml/MetricsPane.fxml"),
                            useCache
                    );
                  });
                } catch (Exception e) {
                  Platform.runLater(this::resetUI);
                  throw new RuntimeException(
                      "An error occurred during file processing: " + e.getMessage());
                }
              });
      importDataThread.start();
    } else if (!fileImportService.isReady()){
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText("You have not uploaded the 3 required log files.");
      alert.showAndWait();
    } else if (existingCampaigns.contains(campaignNameTextField.getText())) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText("A campaign already exists with that name.");
      alert.showAndWait();
    } else if (campaignNameTextField.getText().isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText("You haven't entered a name for the campaign.");
      alert.showAndWait();
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

  private void handleSkip(String campaignName) {
    operationLogger.log("Preuploaded campaign button clicked: " + campaignName);
    System.out.println("Previously selected campaign: " + User.getSelectedCampaign());
    System.out.println("Selecting: " + campaignName);

    //boolean useCache = campaignName.equals(User.getSelectedCampaign());
    boolean useCache = true;
    System.out.println("Using cache? " + useCache);
    User.setSelectedCampaign(campaignName);
    if (true) { // debug : fileImportService.isDataLoaded()
      UIManager.switchContent(
          parentPane, UIManager.createFxmlLoader("/fxml/MetricsPane.fxml"), useCache);

    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText(
          "You cannot Skip if data has not already been uploaded into the database.");
      alert.showAndWait();
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
          alert.showAndWait();
        });
  }

  private void resetUI() {
    impressionLogButton.setDisable(false);
    clickLogButton.setDisable(false);
    serverLogButton.setDisable(false);
    nextButton.setDisable(true);
    logoutButton.setDisable(false);
    campaignNameTextField.setDisable(false);
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

    fileImportService.deleteInsertedData(campaignName);
    fileImportService.setImpressionLog(null);
    fileImportService.setServerLog(null);
    fileImportService.setClickLog(null);
    fileImportService.setCampaignStartDate("");
    campaignNameTextField.clear();
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
