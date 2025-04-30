package com.university.grp20.controller.settings;

import com.university.grp20.UIManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class StyleSettingsController {
  @FXML private ComboBox<String> themeComboBox;
  @FXML private ComboBox<String> fontComboBox;

  @FXML
  public void initialize() {
    themeComboBox.setItems(
        FXCollections.observableArrayList(
            "Default", "Dark", "High Contrast", "Colourblind", "Purple"));
    fontComboBox.setItems(
        FXCollections.observableArrayList(
            "Default",
            "Arial",
            "Cambria",
            "Roboto",
            "Candara",
            "Comic Sans MS",
            "Calibri",
            "Times New Roman"));

    themeComboBox
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                UIManager.setTheme(newValue);
              }
            });

    fontComboBox
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                UIManager.setFont(newValue);
              }
            });
  }
}
