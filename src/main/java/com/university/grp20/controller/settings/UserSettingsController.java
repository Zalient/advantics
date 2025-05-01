package com.university.grp20.controller.settings;

import com.university.grp20.UIManager;
import com.university.grp20.model.LoginService;
import com.university.grp20.model.OperationLogger;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class UserSettingsController {
  @FXML private TextField newUsernameField;
  @FXML private TextField newPasswordField;
  @FXML private ComboBox<String> newRoleMenu;
  @FXML private ComboBox<String> currentUsersMenu;
  @FXML private ComboBox<String> changeRoleMenu;
  private final LoginService loginService = new LoginService();
  @FXML private PasswordField changePasswordField;
  OperationLogger operationLogger = new OperationLogger();

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
    operationLogger.log("Add User apply button clicked");
    String enteredUsername = newUsernameField.getText();
    String enteredPassword = newPasswordField.getText();
    String selectedRole = newRoleMenu.getValue();

    operationLogger.log("Entered values of Username :" + enteredUsername + " Password :" + enteredPassword + " Role :" + selectedRole);
    if (!enteredUsername.isEmpty() && !enteredPassword.isEmpty() && newRoleMenu.getValue() != null) {
      if (!loginService.doesUserExist(newUsernameField.getText())) {

        boolean addUserSuccess = loginService.addUser(enteredUsername, enteredPassword, selectedRole);

        if (addUserSuccess) {
          UIManager.showAlert("Confirmation", "New user \"" + enteredUsername + "\" was successfully added to the database");
          operationLogger.log("New user: " + enteredUsername + " was successfully added to the database");

          newUsernameField.setText("");
          newPasswordField.setText("");
          newRoleMenu.setValue(null);

          currentUsersMenu.getItems().add(enteredUsername);
        } else {
          UIManager.showError("Error adding user");
          operationLogger.log("Error adding user");
        }
      } else {
        UIManager.showError("Username " + enteredUsername + " already in use");
        operationLogger.log("Error as Username: " + enteredUsername + " already in use");
      }
    }
    else {
      UIManager.showError("Some user detail fields are empty, please check again");
      operationLogger.log("Some user detail fields were empty on application");
    }
  }

  @FXML
  private void editUser() {
    operationLogger.log("Add User apply button clicked");
    String updatedPassword = changePasswordField.getText();
    String selectedUser = currentUsersMenu.getValue();
    String selectedRole = changeRoleMenu.getValue();

    operationLogger.log("Entered values of Username :" + selectedUser + " Password :" + updatedPassword + " Role :" + selectedRole);
    if (selectedUser == null || selectedUser.isEmpty()) {
      UIManager.showError("User not selected, please select a user to edit");
      operationLogger.log("User not selected for editing");
      return;
    }
    if ((updatedPassword == null || updatedPassword.isEmpty()) && (selectedRole == null || selectedRole.isEmpty())) {
      UIManager.showError("Password field is empty and no new role is selected");
      operationLogger.log("Password field was empty and no new role was selected");
      return;
    }
    if (updatedPassword != null && !updatedPassword.isEmpty()) {
      boolean passwordUpdateSuccess = loginService.changeUserPassword(selectedUser, updatedPassword);
      if (passwordUpdateSuccess) {
        UIManager.showAlert("Success", "Password updated for user: " + selectedUser);
        operationLogger.log("Password updated for user: " + selectedUser + " to " + updatedPassword);
      } else {
        UIManager.showError("Error updating password");
        operationLogger.log("Error updating password");
      }
    }
    if (selectedRole != null && !selectedRole.isEmpty()) {
      boolean roleUpdateSuccess = loginService.changeUserRole(selectedUser, selectedRole);
      if (roleUpdateSuccess) {
        UIManager.showAlert("Success", "Role updated to " + selectedRole + " for user: " + selectedUser);
        operationLogger.log("Role updated to " + selectedRole + " for user: " + selectedUser);
      } else {
        UIManager.showError("Failed to update role");
        operationLogger.log("Failed to update role");
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
