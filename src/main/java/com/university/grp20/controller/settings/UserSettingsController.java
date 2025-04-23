package com.university.grp20.controller.settings;

import com.university.grp20.UIManager;
import com.university.grp20.model.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class UserSettingsController {
  @FXML private TextField newUsernameField;
  @FXML private TextField newPasswordField;
  @FXML private ComboBox<String> newRoleMenu;
  @FXML private ComboBox<String> currentUsersMenu;
  @FXML private ComboBox<String> changeRoleMenu;
  @FXML private Button editUser;
  @FXML private Button addUser;
  private final LoginService loginService = new LoginService();
  @FXML private PasswordField changePasswordField;

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

    if (!enteredUsername.isEmpty() && !enteredPassword.isEmpty() && newRoleMenu.getValue() != null) {
      if (!loginService.doesUserExist(newUsernameField.getText())) {

        boolean addUserSuccess = loginService.addUser(enteredUsername, enteredPassword, selectedRole);

        if (addUserSuccess) {
          UIManager.showAlert("Confirmation", "New user \"" + enteredUsername + "\" was successfully added to the database");

          newUsernameField.setText("");
          newPasswordField.setText("");
          newRoleMenu.setValue(null);

          currentUsersMenu.getItems().add(enteredUsername);
        } else {
          UIManager.showError("Error adding user");
        }
      } else {
        UIManager.showError("Username" + enteredUsername + " already in use");
      }
    }
    else {
      UIManager.showError("Some user detail fields are empty, please check again");
    }
  }

  @FXML
  private void editUser() {
    String updatedPassword = changePasswordField.getText();
    String selectedUser = currentUsersMenu.getValue();
    String selectedRole = changeRoleMenu.getValue();

    if (selectedUser == null || selectedUser.isEmpty()) {
      UIManager.showError("User not selected, please select a user to edit");
      return;
    }
    if ((updatedPassword == null || updatedPassword.isEmpty()) && (selectedRole == null || selectedRole.isEmpty())) {
      UIManager.showError("Password field is empty and no new role is selected");
      return;
    }
    if (updatedPassword != null && !updatedPassword.isEmpty()) {
      boolean passwordUpdateSuccess = loginService.changeUserPassword(selectedUser, updatedPassword);
      if (passwordUpdateSuccess) {
        UIManager.showAlert("Success", "Password updated for user: " + selectedUser);
      } else {
        UIManager.showError("Error updating password");
      }
    }
    if (selectedRole != null && !selectedRole.isEmpty()) {
      boolean roleUpdateSuccess = loginService.changeUserRole(selectedUser, selectedRole);
      if (roleUpdateSuccess) {
        UIManager.showAlert("Success", "Role updated to " + selectedRole + " for user: " + selectedUser);
      } else {
        UIManager.showError("Failed to update role");
      }
    }
    changePasswordField.setPromptText("New Password");
    changePasswordField.setText(null);
    currentUsersMenu.getSelectionModel().clearSelection();
    currentUsersMenu.setPromptText("Select User");
    changeRoleMenu.getSelectionModel().clearSelection();
    changeRoleMenu.setPromptText("New Role");
  }
}
