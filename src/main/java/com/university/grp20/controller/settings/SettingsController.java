package com.university.grp20.controller.settings;

import com.university.grp20.UIManager;
import com.university.grp20.controller.MetricsController;
import com.university.grp20.controller.Navigator;
import com.university.grp20.model.CalculateMetricsService;
import com.university.grp20.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.util.List;

public class SettingsController extends Navigator {
  @FXML private Button metricSettingsButton;
  @FXML private Button userSettingsButton;
  @FXML private Button exportSettingsButton;
  @FXML private StackPane contentPane;
  private static boolean bounceChanged = false;

  @FXML
  private void initialize() {
    if (User.getRole().equals("Viewer")) {
      deleteButtons(List.of(metricSettingsButton, userSettingsButton, exportSettingsButton));
    } else if (User.getRole().equals("Editor")) {
      deleteButtons(List.of(userSettingsButton, exportSettingsButton));
    }

    displayThemeSettings();
  }

  @FXML
  private void displayMetrics() {
    if (bounceChanged) {
      UIManager.switchContent(parentPane, UIManager.createFxmlLoader("/fxml/MetricsPane.fxml"));
    }
    UIManager.switchContent(parentPane, UIManager.createFxmlLoader("/fxml/MetricsPane.fxml"), true);
  }

  @FXML
  private void displayMetricSettings() {
    UIManager.switchContent(
        contentPane, UIManager.createFxmlLoader("/fxml/settings/MetricSettingsPane.fxml"));
  }

  @FXML
  private void displayUserSettings() {
    UIManager.switchContent(
        contentPane, UIManager.createFxmlLoader("/fxml/settings/UserSettingsPane.fxml"));
  }

  @FXML
  private void displayExportSettings() {
    UIManager.switchContent(
        contentPane, UIManager.createFxmlLoader("/fxml/settings/ExportSettingsPane.fxml"));
  }

  @FXML
  private void displayThemeSettings() {}

  @FXML
  private void displayLanguageSettings() {}

  private void deleteButtons(List<Button> buttons) {
    for (Button button : buttons) {
      button.setVisible(false);
      button.setManaged(false);
    }
  }
}
