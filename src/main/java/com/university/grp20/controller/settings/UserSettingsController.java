package com.university.grp20.controller.settings;

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
  @FXML private TextField changePasswordField;
  @FXML private ComboBox<String> changeRoleMenu;
  @FXML private Button addUserButton;
  @FXML private Button editUserButton;
  private boolean newUsernameEntered;
  private boolean newPasswordEntered;
  private boolean newRoleSelected;
  private boolean userSelected;
  private boolean passwordChanged;
  private boolean roleChanged;
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

    initUserInfoListeners();
  }

  @FXML
  private void addUser() {
    String enteredUsername = newUsernameField.getText();
    String enteredPassword = newPasswordField.getText();
    String selectedRole = newRoleMenu.getValue();

    if (!loginService.doesUserExist(newUsernameField.getText())) {

      boolean addUserSuccess = loginService.addUser(enteredUsername, enteredPassword, selectedRole);

      if (addUserSuccess) {
        showAlert(
            "Confirmation",
            "New user \"" + enteredUsername + "\" was successfully added to the database");

        newUsernameField.setText("");
        newPasswordField.setText("");
        newRoleMenu.getSelectionModel().clearSelection();

        currentUsersMenu.getItems().add(enteredUsername);
      } else {
        showError("Error adding user");
      }
    } else {
      showError("Username taken");
    }
  }

  @FXML
  private void editUser() {
    String enteredPassword = newPasswordField.getText();

    String selectedUser = currentUsersMenu.getValue();

    boolean passwordUpdateSuccess = loginService.changeUserPassword(selectedUser, enteredPassword);

    if (!passwordUpdateSuccess) {
      showError("Error changing user's password");
    }

    String selectedRole = changeRoleMenu.getValue();

    boolean roleUpdateSuccess = loginService.changeUserRole(selectedUser, selectedRole);

    if (!roleUpdateSuccess) {
      showError("Error changing user's role");
    }

    newPasswordField.setText("");
    currentUsersMenu.getSelectionModel().clearSelection();
    changeRoleMenu.getSelectionModel().clearSelection();
  }

  private void initUserInfoListeners() {
    newUsernameField
        .focusedProperty()
        .addListener(
            (obs, lostFocus, gainedFocus) -> {
              if (lostFocus) {
                newUsernameEntered = !newUsernameField.getText().isEmpty();
                updateAddUserButton();
              }
            });
    newUsernameField.setOnKeyPressed(
        ev -> {
          if (ev.getCode() == KeyCode.ENTER) {
            newUsernameEntered = !newUsernameField.getText().isEmpty();
            updateAddUserButton();
          }
        });

    newPasswordField
        .focusedProperty()
        .addListener(
            (obs, lostFocus, gainedFocus) -> {
              if (lostFocus) {
                newPasswordEntered = !newPasswordField.getText().isEmpty();
                updateAddUserButton();
              }
            });
    newPasswordField.setOnKeyPressed(
        ev -> {
          if (ev.getCode() == KeyCode.ENTER) {
            newPasswordEntered = !newPasswordField.getText().isEmpty();
            updateAddUserButton();
          }
        });

    newRoleMenu
        .focusedProperty()
        .addListener(
            (obs, lostFocus, gainedFocus) -> {
              if (lostFocus) {
                newRoleSelected =
                    newRoleMenu.getValue() != null && !newRoleMenu.getValue().isEmpty();
                updateAddUserButton();
              }
            });

    currentUsersMenu
        .focusedProperty()
        .addListener(
            (obs, lostFocus, gainedFocus) -> {
              if (lostFocus) {
                userSelected =
                    currentUsersMenu.getValue() != null && !currentUsersMenu.getValue().isEmpty();
                updateEditUserButton();
              }
            });

    changePasswordField
        .focusedProperty()
        .addListener(
            (obs, lostFocus, gainedFocus) -> {
              if (lostFocus) {
                passwordChanged = !changePasswordField.getText().isEmpty();
                updateEditUserButton();
              }
            });
    changePasswordField.setOnKeyPressed(
        ev -> {
          if (ev.getCode() == KeyCode.ENTER) {
            passwordChanged = !changePasswordField.getText().isEmpty();
            updateEditUserButton();
          }
        });

    changeRoleMenu
        .focusedProperty()
        .addListener(
            (obs, lostFocus, gainedFocus) -> {
              if (lostFocus) {
                roleChanged =
                    changeRoleMenu.getValue() != null && !changeRoleMenu.getValue().isEmpty();
                updateEditUserButton();
              }
            });
  }

  private void updateAddUserButton() {
    boolean enable = newUsernameEntered && newPasswordEntered && newRoleSelected;
    addUserButton.setDisable(!enable);
  }

  private void updateEditUserButton() {
    boolean enable = userSelected && passwordChanged && roleChanged;
    editUserButton.setDisable(!enable);
  }

  private void showError(String errorMessage) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error!");
    alert.setHeaderText(null);
    alert.setContentText(errorMessage);
    alert.showAndWait();
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
