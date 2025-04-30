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

public class ViewerFileSelectionController extends Navigator {

  @FXML private Button logoutButton;

  @FXML private Label roleLabel;

  @FXML private VBox uploadedCampaignVBox;
  @FXML private Label selectCampaignLabel;

  private static final Logger logger = LogManager.getLogger(ViewerFileSelectionController.class);
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
          campaignButton.getStyleClass().add("custom-button");
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
      selectCampaignLabel.setText("No Campaigns Found");
    }

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
}
