package com.university.grp20.controller;

import com.university.grp20.model.OperationLogger;
import com.university.grp20.model.ExportLogService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.university.grp20.UIManager;
import com.university.grp20.model.LoginService;
import com.university.grp20.model.OperationLogger;
import com.university.grp20.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class SettingsController {

    private ExportLogService exportLogService = new ExportLogService();
    private OperationLogger operationLogger = new OperationLogger();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private final Logger logger = LogManager.getLogger(MetricsController.class);

    @FXML private TextField addUsernameField;
    @FXML private TextField addPasswordField;
    @FXML private ComboBox selectRoleMenu;
    @FXML private Button addUserButton;
    @FXML private HBox userManagementTitleBox;
    @FXML private GridPane userManagementGridPane;
    @FXML private ScrollPane settingsScrollPane;
    @FXML private GridPane userEditGridPane;
    @FXML private ComboBox selectUserMenu;
    @FXML private TextField newPasswordField;
    @FXML private ComboBox selectNewRoleMenu;
    @FXML private Label currentPasswordLabel;
    @FXML private Label currentRoleLabel;
    @FXML private RadioButton pagesViewedOpt;
    @FXML private RadioButton timeSpentOpt;
    @FXML private Button bounceChooser;
    @FXML private ToggleGroup bounceGroup;
    @FXML private HBox exportLogHBox;
    @FXML private HBox metricsTitleBox;
    @FXML private VBox metricsVBox;

    private LoginService loginService = new LoginService();
    private OperationLogger OperationLogger = new OperationLogger();

    @FXML
    private void initialize() {
      operationLogger.log("Settings page clicked and displayed");
      selectRoleMenu.getItems().addAll("Viewer", "Editor", "Admin");
      selectNewRoleMenu.getItems().addAll("Viewer", "Editor", "Admin");
      bounceGroup = new ToggleGroup();
      pagesViewedOpt.setToggleGroup(bounceGroup);
      timeSpentOpt.setToggleGroup(bounceGroup);

      ArrayList<String> userList = loginService.getAllUsers();

      // Add existing users in the database to the selectUserMenu
      for (String user : userList) {
        logger.info("Found user: " + user);
        selectUserMenu.getItems().add(user);
      }

      // If user isn't an admin them remove all of the admin only settings
      if (!User.getRole().equals("Admin")) {
          VBox content = (VBox) settingsScrollPane.getContent();
          content.getChildren().removeAll(userManagementTitleBox,userManagementGridPane,userEditGridPane, exportLogHBox);
      }

      // If user is a viewer remove metrics settings
      if (User.getRole().equals("Viewer")) {
        VBox content = (VBox) settingsScrollPane.getContent();
        content.getChildren().removeAll(metricsTitleBox, metricsVBox);
      }
    }


    @FXML
    private void handleAddUser () {
      logger.info("roleMenu had selected " + selectRoleMenu.getValue());

      String enteredUsername = addUsernameField.getText();
      String enteredPassword = addPasswordField.getText();

      if (!enteredUsername.isEmpty() && !enteredPassword.isEmpty() && selectRoleMenu.getValue() != null) {
        if (!loginService.doesUserExist(addUsernameField.getText())) {

            boolean bSuccessful = loginService.addUser(enteredUsername, enteredPassword, selectRoleMenu.getValue().toString());

            if (bSuccessful) {
              Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
              alert.setTitle("Confirmation");
              alert.setHeaderText(null);
              alert.setContentText("New user \"" + enteredUsername + "\" was successfully added to the database");
              alert.showAndWait();

              addUsernameField.setText("");
              addPasswordField.setText("");
              selectRoleMenu.getSelectionModel().clearSelection();

              selectUserMenu.getItems().add(enteredUsername);

            } else {
              showError("Something went wrong when adding the user to the database.");
            }




        } else {
          showError("A user with that username already exists in the database");
        }
      } else {
        showError("You have not filled out all the user detail fields..");
      }

    }

    @FXML
    private void updateEditUserLabels() {
      if (selectUserMenu.getValue() != null) {
        currentPasswordLabel.setText("Current Password: " + loginService.getUserPassword(selectUserMenu.getValue().toString()));
        currentRoleLabel.setText("Current Role: " + loginService.getUserRole(selectUserMenu.getValue().toString()));
      }

    }

    @FXML
    private void handleUserChange() {

      String enteredPassword = newPasswordField.getText();

      if (selectUserMenu.getValue() != null) {

        String selectedUser = selectUserMenu.getValue().toString();

        if (!enteredPassword.isEmpty()) {
          boolean passwordUpdateSuccess = loginService.changeUserPassword(selectedUser, enteredPassword);

          if (!passwordUpdateSuccess) {
            showError("Something went wrong with updating the user's password");
          }
        }

        if (selectNewRoleMenu.getValue() != null) {
          String selectedRole = selectNewRoleMenu.getValue().toString();

          boolean roleUpdateSuccess = loginService.changeUserRole(selectedUser, selectedRole);

          if (!roleUpdateSuccess) {
            showError("Something went wrong with updating the user's role");
          }
        }

        newPasswordField.setText("");
        selectUserMenu.getSelectionModel().clearSelection();
        selectNewRoleMenu.getSelectionModel().clearSelection();
        currentPasswordLabel.setText("Current Password: ");
        currentRoleLabel.setText("Current Role: ");

      } else {
        showError("You have not selected a user to edit");
      }
    }

    @FXML
    private void handleBack() {
      UIManager.switchScene(UIManager.createFXMLLoader("/fxml/MetricsScene.fxml"));
    }

    private void showError(String errorMessage) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText(errorMessage);
      alert.showAndWait();
    }

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
