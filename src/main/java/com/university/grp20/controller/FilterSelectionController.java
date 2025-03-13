package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.CalculateMetricsService;
import com.university.grp20.model.FilterCriteriaDTO;
import com.university.grp20.model.GenerateChartService;
import com.university.grp20.model.MetricsDTO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

public class FilterSelectionController {
  public enum FilterMode {
    METRICS,
    CHART
  }
  @FXML private CheckComboBox<String> ageGroupSelector, contextSelector, incomeSelector;
  @FXML private RadioButton maleRadioButton, femaleRadioButton;
  @FXML private ToggleGroup genderGroup;
  @FXML private ComboBox<String> granularityChooser;
  @FXML private DatePicker startDatePicker, endDatePicker;
  @FXML private Button applyChangesButton;
  @FXML private Label chartNameLabel;
  private final Logger logger = LogManager.getLogger(FilterSelectionController.class);
  private FilterMode filterMode;
  private MetricsController metricsController;
  private ChartViewer chartViewer;

  public void setFilterMode(FilterMode mode) {
    this.filterMode = mode;
    applyFilterModeUI();
  }

  public void setMetricsController(MetricsController metricsController) {
    this.metricsController = metricsController;
  }

  public void setChartViewer(ChartViewer chartViewer) {
    this.chartViewer = chartViewer;
  }

  @FXML
  private void initialize() {
    genderGroup = new ToggleGroup();
    maleRadioButton.setToggleGroup(genderGroup);
    femaleRadioButton.setToggleGroup(genderGroup);
    maleRadioButton.setUserData("Male");
    femaleRadioButton.setUserData("Female");

    ageGroupSelector.getItems().addAll("Below 25", "25-34", "35-44", "45-55", "Above 55");
    incomeSelector.getItems().addAll("Low", "Medium", "High");
    contextSelector
        .getItems()
        .addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
  }

  @FXML
  private void applyChanges() {
    applyChangesButton.setDisable(true);
    FilterCriteriaDTO filterCriteriaDTO = buildFilterCriteria();

    if (filterMode == FilterMode.METRICS) {
      logger.info("Calculating filtered metrics");
      Task<MetricsDTO> task =
          new Task<>() {
            @Override
            protected MetricsDTO call() {
              CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
              return calculateMetricsService.getMetrics(filterCriteriaDTO);
            }
          };

      task.setOnSucceeded(
          e -> {
            metricsController.setMetrics(task.getValue());
            UIManager.closeModal();
            logger.info("Testing - clicks: " + task.getValue().getClicks());
          });

      task.setOnFailed(
          e -> logger.error("Error calculating metrics: " + task.getException().getMessage()));

      new Thread(task).start();
    } else if (filterMode == FilterMode.CHART) {
      GenerateChartService generateChartService = new GenerateChartService();
      String metricType =
          (chartViewer != null && chartViewer.getUserData() != null)
              ? chartViewer.getUserData().toString()
              : "";
      JFreeChart newFilteredChart =
          generateChartService.getFilteredChart(metricType, filterCriteriaDTO);

      if (newFilteredChart != null && chartViewer != null) {
        chartViewer.setChart(newFilteredChart);
      }

      UIManager.closeModal();
    }
  }

  @FXML
  private void quit() {
    UIManager.closeModal();
  }

  private void applyFilterModeUI() {
    if (filterMode == FilterMode.METRICS) {
      granularityChooser.setVisible(false);
      chartNameLabel.setVisible(false);
    }
  }

  private FilterCriteriaDTO buildFilterCriteria() {
    FilterCriteriaDTO filterCriteriaDTO = new FilterCriteriaDTO();
    filterCriteriaDTO.setAgeRanges(ageGroupSelector.getCheckModel().getCheckedItems());
    filterCriteriaDTO.setIncomes(incomeSelector.getCheckModel().getCheckedItems());
    filterCriteriaDTO.setContexts(contextSelector.getCheckModel().getCheckedItems());

    if (granularityChooser.getValue() != null) {
      filterCriteriaDTO.setTimeGranularity(granularityChooser.getValue());
    }
    if (startDatePicker.getValue() != null) {
      filterCriteriaDTO.setStartDate(startDatePicker.getValue());
    }
    if (endDatePicker.getValue() != null) {
      filterCriteriaDTO.setEndDate(endDatePicker.getValue());
    }

    Toggle selectedToggle = genderGroup.getSelectedToggle();
    filterCriteriaDTO.setGender(
        selectedToggle != null ? selectedToggle.getUserData().toString() : null);

    return filterCriteriaDTO;
  }
}
