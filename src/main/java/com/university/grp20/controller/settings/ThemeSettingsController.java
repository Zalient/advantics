package com.university.grp20.controller.settings;

import com.university.grp20.UIManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ThemeSettingsController {

    @FXML
    private ComboBox<String> themeComboBox;

    @FXML
    public void initialize() {
        themeComboBox.setItems(FXCollections.observableArrayList(
                "Default Mode", "Dark Mode", "High Contrast Mode"
        ));

        themeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                UIManager.setTheme(newValue);
            }
        });
    }

}
