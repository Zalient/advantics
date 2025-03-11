package com.university.grp20.controller;

import com.university.grp20.model.FilterDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.util.Objects;

public class MetricsEditorController {

    @FXML private CheckComboBox<String> ageRangeChooser, contextChooser, incomeChooser;
    @FXML private RadioButton maleOpt, femaleOpt;
    @FXML private ToggleGroup genderGroup;

    @FXML
    private void initialize() {
        genderGroup = new ToggleGroup();
        maleOpt.setToggleGroup(genderGroup);
        femaleOpt.setToggleGroup(genderGroup);
        maleOpt.setUserData("Male");
        femaleOpt.setUserData("Female");

        ageRangeChooser.getItems().addAll("Below 25", "25-34", "35-44", "45-55", "Above 55");
        incomeChooser.getItems().addAll("Low", "Medium", "High");
        contextChooser
                .getItems()
                .addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
    }

    @FXML
    private void applyChanges(ActionEvent event) throws IOException {
        // 1) Build the FilterDTO from user inputs
        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setAgeRanges(ageRangeChooser.getCheckModel().getCheckedItems());
        filterDTO.setIncomes(incomeChooser.getCheckModel().getCheckedItems());
        filterDTO.setContexts(contextChooser.getCheckModel().getCheckedItems());
        Toggle selectedToggle = genderGroup.getSelectedToggle();
        filterDTO.setGender(selectedToggle != null ? selectedToggle.getUserData().toString() : null);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardPage.fxml"));

        Parent root = loader.load();

        DashController dashController = loader.getController();
        dashController.setFilterDTO(filterDTO);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}