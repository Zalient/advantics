package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.LoginService;
import com.university.grp20.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsController {
  private final Logger logger = LogManager.getLogger(MetricsController.class);

  @FXML private TextField addUsernameField;
  @FXML private TextField addPasswordField;
  @FXML private ComboBox selectRoleMenu;
  @FXML private Button addUserButton;
  @FXML private HBox userManagementTitleBox;
  @FXML private GridPane userManagementGridPane;
  @FXML private ScrollPane settingsScrollPane;

  private LoginService loginService = new LoginService();

  @FXML
  private void initialize() {
    selectRoleMenu.getItems().addAll("Viewer", "Editor", "Admin");

    if (!User.getRole().equals("Admin")) {
        VBox content = (VBox) settingsScrollPane.getContent();
        content.getChildren().removeAll(userManagementTitleBox,userManagementGridPane);

    }
  }


  @FXML
  private void handleAddUser () {
    logger.info("roleMenu had selected " + selectRoleMenu.getValue());

    String enteredUsername = addUsernameField.getText();
    String enteredPassword = addPasswordField.getText();


    if (!enteredUsername.isEmpty() && !enteredPassword.isEmpty() && selectRoleMenu.getValue() != null) {
      if (!loginService.doesUserExist(addUsernameField.getText())) {

          loginService.addUser(enteredUsername, enteredPassword, selectRoleMenu.getValue().toString());
          addUsernameField.setText("");
          addPasswordField.setText("");
          selectRoleMenu.getSelectionModel().clearSelection();



      } else {
        showError("A user with that username already exists in the database");
      }
    } else {
      showError("You have not filled out all the user detail fields..");
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
}
