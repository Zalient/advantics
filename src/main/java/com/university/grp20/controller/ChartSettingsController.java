package com.university.grp20.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.controlsfx.control.CheckComboBox;

public class ChartSettingsController {

    @FXML
    private CheckComboBox<String> ageRangeChooser, contextChooser, incomeChooser;
    @FXML
    private RadioButton maleOpt, femaleOpt;
    @FXML
    private ComboBox granularityChooser;


    @FXML
    private void initialize() {

        ToggleGroup genderGroup = new ToggleGroup();
        maleOpt.setToggleGroup(genderGroup);
        femaleOpt.setToggleGroup(genderGroup);
        ageRangeChooser.getItems().addAll("Below 25", "25-34","35-44", "45-55", "Above 55");
        incomeChooser.getItems().addAll("Low","Medium","High");
        contextChooser.getItems().addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
    }
}
