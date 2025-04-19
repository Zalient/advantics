package com.university.grp20.controller.settings;

import com.university.grp20.UIManager;
import com.university.grp20.model.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class UserSettingsController {
  @FXML private TextField newUsernameField;
  @FXML private TextField newPasswordField;
  @FXML private ComboBox<String> newRoleMenu;
  @FXML private ComboBox<String> currentUsersMenu;
  @FXML private ComboBox<String> changeRoleMenu;
  private final LoginService loginService = new LoginService();

  @FXML
  private void initialize() {
    List<String> roles = List.of("Viewer", "Editor", "Admin");
    newRoleMenu.getItems().addAll(roles);
    changeRoleMenu.getItems().addAll(roles);

    ArrayList<String> userList = loginService.getAllUsers();
    for (String user : userList) {
      currentUsersMenu.getItems().add(user);
    }
  }

  @FXML
  private void addUser() {
    String enteredUsername = newUsernameField.getText();
    String enteredPassword = newPasswordField.getText();
    String selectedRole = newRoleMenu.getValue();

    if (!enteredUsername.isEmpty()
        && !enteredPassword.isEmpty()
        && changeRoleMenu.getValue() != null) {
      if (!loginService.doesUserExist(newUsernameField.getText())) {

        boolean addUserSuccess =
            loginService.addUser(enteredUsername, enteredPassword, selectedRole);

        if (addUserSuccess) {
          UIManager.showAlert(
              "Confirmation",
              "New user \"" + enteredUsername + "\" was successfully added to the database");

          newUsernameField.setText("");
          newPasswordField.setText("");
          newRoleMenu.getSelectionModel().clearSelection();

          currentUsersMenu.getItems().add(enteredUsername);
        } else {
          UIManager.showError("Error adding user");
        }
      } else {
        UIManager.showError("Username is taken");
      }
    }
    else {
      UIManager.showError("Please fill in the user detail fields");
    }
  }

  @FXML
  private void editUser() {
    String enteredPassword = newPasswordField.getText();
    String selectedUser = currentUsersMenu.getValue();
    boolean passwordUpdateSuccess = loginService.changeUserPassword(selectedUser, enteredPassword);

    if (newRoleMenu.getValue() != null) {

      if (!passwordUpdateSuccess) {
        UIManager.showError("Error changing user's password");
      }

      String selectedRole = changeRoleMenu.getValue();
      boolean roleUpdateSuccess = loginService.changeUserRole(selectedUser, selectedRole);

      if (!roleUpdateSuccess) {
        UIManager.showError("Error changing user's role");
      }

      newPasswordField.setText("");
      currentUsersMenu.getSelectionModel().clearSelection();
      changeRoleMenu.getSelectionModel().clearSelection();
      }
    else {
      UIManager.showError("Please select a user to edit");
    }
  }
}
