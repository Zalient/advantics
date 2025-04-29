package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.LoginService;
import com.university.grp20.model.OperationLogger;
import com.university.grp20.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

import java.util.Objects;

public class LoginController extends Navigator {
  @FXML private TextField usernameInputBox;
  @FXML private Button helpButton;

  @FXML private TextField passwordInputBox;
  private static final Logger logger = LogManager.getLogger(LoginController.class);
  private final OperationLogger operationLogger = new OperationLogger();
  private LoginService loginService = new LoginService();
  private String loginStatus;

  @FXML
  public void initialize() {
    logger.info("Initialising login screen");
    loginService.setOnLogin(this::processLogin);
    loginStatus = "Default";

  }

  @FXML
  private void handleLogin() {
    logger.info("Login button pressed");

    String enteredUsername = usernameInputBox.getText();
    operationLogger.log("Username entered: " + enteredUsername);
    String enteredPassword = passwordInputBox.getText();
    operationLogger.log("Password entered: " + enteredPassword);
    operationLogger.log("Login button clicked, attempting login");
    if (!enteredUsername.isEmpty() && !enteredPassword.isEmpty()) {
      loginService.login(usernameInputBox.getText(), passwordInputBox.getText());
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);

      if (enteredUsername.isEmpty() && enteredPassword.isEmpty()) {
        operationLogger.log("Username and password fields are empty");
        alert.setContentText("You have not entered a username or password.");
      } else if (!enteredUsername.isEmpty()) {
        operationLogger.log("Password field is empty");
        alert.setContentText("You have not entered a password.");
      } else {
        operationLogger.log("Username field is empty");
        alert.setContentText("You have not entered a username.");
      }

      alert.showAndWait();
    }
  }

  public void processLogin(String loginStatus) {
    logger.info("Login Status is " + loginStatus);
    this.loginStatus = loginStatus;

    if (loginStatus.equals("Valid")) {
      String role = User.getRole();
      logger.info("Role is " + role);
      operationLogger.log("Successful login: Logged in as " + role);

      if (role.equals("Admin") || role.equals("Editor")) {

        UIManager.switchContent(parentPane, UIManager.createFxmlLoader("/fxml/FileSelectionPane.fxml"));

      } else if (role.equals("Viewer")) {
        if (loginService.isDataLoaded()) {
          UIManager.switchContent(
              parentPane, UIManager.createFxmlLoader("/fxml/MetricsPane.fxml"), true);

        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error!");
          alert.setHeaderText(null);
          alert.setContentText(
              "You do not have stored campaign data. Please contact an administrator or editor to upload this for you.");
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
        operationLogger.log("Login failed: Invalid password entered");
      } else if (loginStatus.equals("Missing")) {
        alert.setContentText(
            "That username doesn't exist in the database. Please contact an administrator.");
        alert.setContentText("That username doesn't exist in the database. Please contact an administrator.");
        operationLogger.log("Login failed: Username does not exist in database");
      }

      alert.showAndWait();
    }
  }

  @FXML
  private void showHelpGuide() {
    FXMLLoader loader = UIManager.createFxmlLoader("/fxml/HelpGuidePane.fxml");
    try {
      loader.load();
      HelpGuideController helpController = loader.getController();
      helpController.setupCarousel("Login");
      UIManager.showModalStage("Login Page Help Guide", loader, false);
      operationLogger.log("Login Page Help Guide Icon clicked");
    } catch (IOException e) {
      logger.error("Failed to open Help Guide", e);
    }
  }

  public String getLoginStatus() {
    return loginStatus;
  }

}
