package com.university.grp20.controller;

import com.university.grp20.model.ChartFilterDTO;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

public class ChartSettingsController {

    @FXML
    private CheckComboBox<String> ageRangeChooser, contextChooser, incomeChooser;
    @FXML
    private RadioButton maleOpt, femaleOpt;
    private ToggleGroup genderGroup;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private ComboBox granularityChooser;

    @FXML
    private Button applyChangeButton;

    private ChartViewer chartViewer;
    private ChartController chartController;

    public void initChartViewer(ChartViewer chartViewer, ChartController chartController) {
        this.chartViewer = chartViewer;
        this.chartController = chartController;
    }

    @FXML
    private void initialize() {

        genderGroup = new ToggleGroup();
        maleOpt.setToggleGroup(genderGroup);
        femaleOpt.setToggleGroup(genderGroup);
        maleOpt.setUserData("Male");
        femaleOpt.setUserData("Female");

        ageRangeChooser.getItems().addAll("Below 25", "25-34","35-44", "45-55", "Above 55");
        incomeChooser.getItems().addAll("Low","Medium","High");
        contextChooser.getItems().addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");

        applyChangeButton.setOnAction(event -> {
            ChartFilterDTO filterDTO = new ChartFilterDTO();

            filterDTO.setAgeRanges(ageRangeChooser.getCheckModel().getCheckedItems());
            filterDTO.setIncomes(incomeChooser.getCheckModel().getCheckedItems());
            filterDTO.setContexts(contextChooser.getCheckModel().getCheckedItems());
            Toggle selectedToggle = genderGroup.getSelectedToggle();
            filterDTO.setGender(selectedToggle != null ? selectedToggle.getUserData().toString() : null);
            filterDTO.setStartDate(startDatePicker.getValue());
            filterDTO.setEndDate(endDatePicker.getValue());

            String metricType = (String)chartViewer.getUserData();
            JFreeChart newFilteredChart = chartController.generateChart.getFilteredChart(metricType, filterDTO);

            if (newFilteredChart != null) {
                chartViewer.setChart(newFilteredChart);
            }

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.close();
        });
    }

}