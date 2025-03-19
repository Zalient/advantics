package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.LoginService;
import com.university.grp20.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginController {
  private static final Logger logger = LogManager.getLogger(LoginController.class);

  private LoginService loginService = new LoginService();

  @FXML
  private TextField usernameInputBox;

  @FXML
  private TextField passwordInputBox;

  @FXML
  private Button loginButton;

  @FXML
  public void initialize() {
    logger.info("Initialising login screen");
    loginService.setOnLogin(this::processLogin);

  }

  @FXML
  private void handleLogin() {
    logger.info("Login button pressed");

    String enteredUsername = usernameInputBox.getText();
    String enteredPassword = passwordInputBox.getText();

    if (!enteredUsername.isEmpty() && !enteredPassword.isEmpty()) {
      loginService.login(usernameInputBox.getText(), passwordInputBox.getText());
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);

      if (enteredUsername.isEmpty() && enteredPassword.isEmpty()) {
        alert.setContentText("You have not entered a username or password.");
      } else if (!enteredUsername.isEmpty() && enteredPassword.isEmpty()) {
        alert.setContentText("You have not entered a password.");
      } else {
        alert.setContentText("You have not entered a username.");
      }

      alert.showAndWait();
    }
  }

  /**
   * Called by the listener in the "Login" class
   *
   * @param loginStatus Is "Valid" is password matches or "Invalid" if it doesn't
   */
  public void processLogin(String loginStatus) {
    logger.info("Login Status is " + loginStatus);

    if (loginStatus.equals("Valid")) {
      String role = User.getRole();
      logger.info("Role is " + role);

      if (role.equals("Admin") || role.equals("Editor")) {

          UIManager.switchScene(UIManager.createFXMLLoader("/fxml/FileSelectionScene.fxml"), false);

      } else if (role.equals("Viewer")) {
        if (loginService.isDataLoaded()) {
            UIManager.switchScene(UIManager.createFXMLLoader("/fxml/MetricsScene.fxml"));

        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error!");
          alert.setHeaderText(null);
          alert.setContentText("You do not have stored campaign data. Please contact an administrator or editor to upload this for you.");
          alert.showAndWait();
        }
      } else {
        logger.info("Invalid role passed to the login controller");
      }
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);

      if (loginStatus.equals("Invalid")) {
        alert.setContentText("Your password was invalid.");
      } else if (loginStatus.equals("Missing")) {
        alert.setContentText("That username doesn't exist in the database. Please contact an administrator.");
      }

      alert.showAndWait();

    }

  }

}
