package com.university.grp20.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsController {
  private final Logger logger = LogManager.getLogger(MetricsController.class);

  @FXML private TextField addUsernameField;
  @FXML private TextField addPasswordField;
  @FXML private MenuButton selectRoleMenu;
  @FXML private Button addUserButton;

  @FXML
  private void handleAddUser () {
    if (!addUsernameField.getText().isEmpty() && !addPasswordField.getText().isEmpty()) {
      //Handle adding password
    }

  }
}
