package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.title.TextTitle;

import java.awt.*;

import java.time.LocalDate;
import java.util.List;

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
  @FXML private ProgressBar filterProgressBar;
  @FXML private Label filterProgressLabel;
  private final Logger logger = LogManager.getLogger(FilterSelectionController.class);
  private FilterMode filterMode;
  private MetricsController metricsController;
  private ChartViewer chartViewer;
  private final OperationLogger operationLogger = new OperationLogger();

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

    ageGroupSelector.getItems().addAll("<25", "25-34", "35-44", "45-54", ">54");
    incomeSelector.getItems().addAll("Low", "Medium", "High");
    contextSelector
        .getItems()
        .addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
  }

  @FXML
  private void applyChanges() {
    CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
    calculateMetricsService.setOnFilterStart(this::updateProgressBar);
    calculateMetricsService.setOnFilterLabelStart(this::updateProgressLabel);
    applyChangesButton.setDisable(true);
    FilterCriteriaDTO filterCriteriaDTO = buildFilterCriteria();
    operationLogger.log("Filters chosen and applied");

    if (filterMode == FilterMode.METRICS) {
      logger.info("Calculating filtered metrics");
      Task<MetricsDTO> task = getMetricsDTOTask(calculateMetricsService, filterCriteriaDTO);

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
        String filterApplied =
            "Time granularity: "
                + filterCriteriaDTO.timeGranularity()
                + "   "
                + "Start Date: "
                + (filterCriteriaDTO.startDate() != null
                    ? filterCriteriaDTO.startDate().toString()
                    : " ")
                + "   "
                + "End Date: "
                + (filterCriteriaDTO.endDate() != null
                    ? filterCriteriaDTO.endDate().toString()
                    : " ")
                + "   "
                + "\n"
                + "Gender: "
                + (filterCriteriaDTO.gender() != null ? filterCriteriaDTO.gender() : "All Gender")
                + "   "
                + "Age Ranges: "
                + (filterCriteriaDTO.ageRanges() != null
                    ? filterCriteriaDTO.ageRanges().toString()
                    : "All Age Range")
                + "   "
                + "Income: "
                + (filterCriteriaDTO.incomes() != null
                    ? filterCriteriaDTO.incomes().toString()
                    : "All Income Levels")
                + "   "
                + "Contexts: "
                + (filterCriteriaDTO.contexts() != null
                    ? filterCriteriaDTO.contexts().toString()
                    : "All Context");

        TextTitle subtitle = new TextTitle(filterApplied);
        subtitle.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        subtitle.setPaint(new Color(0, 0, 140));
        newFilteredChart.addSubtitle(subtitle);
      }

      if (newFilteredChart != null && chartViewer != null) {
        chartViewer.setChart(newFilteredChart);
      }

      UIManager.closeModal();
    }
  }

  private Task<MetricsDTO> getMetricsDTOTask(
      CalculateMetricsService calculateMetricsService, FilterCriteriaDTO filterCriteriaDTO) {
    Task<MetricsDTO> task =
        new Task<>() {
          @Override
          protected MetricsDTO call() {
            return calculateMetricsService.fetchMetrics(filterCriteriaDTO);
          }
        };

    task.setOnSucceeded(
        e -> {
          metricsController.setMetrics(task.getValue());
          UIManager.closeModal();
          logger.info("Testing - clicks: " + task.getValue().clicks());
        });

    task.setOnFailed(
        e -> logger.error("Error calculating metrics: " + task.getException().getMessage()));
    return task;
  }

  @FXML
  private void quit() {
    operationLogger.log("Quit button selected");
    UIManager.closeModal();
  }

  private void applyFilterModeUI() {
    if (filterMode == FilterMode.METRICS) {
      granularityChooser.setVisible(false);
      chartNameLabel.setVisible(false);
    }
    if (filterMode == FilterMode.CHART) {
      filterProgressBar.setVisible(false);
      filterProgressLabel.setVisible(false);
    }
  }

  private FilterCriteriaDTO buildFilterCriteria() {
    // Create immutable copies of the UI list selections
    List<String> ageRanges =
        ageGroupSelector.getCheckModel().getCheckedItems() != null
            ? List.copyOf(ageGroupSelector.getCheckModel().getCheckedItems())
            : List.of();
    List<String> incomes =
        incomeSelector.getCheckModel().getCheckedItems() != null
            ? List.copyOf(incomeSelector.getCheckModel().getCheckedItems())
            : List.of();
    List<String> contexts =
        contextSelector.getCheckModel().getCheckedItems() != null
            ? List.copyOf(contextSelector.getCheckModel().getCheckedItems())
            : List.of();

    // Get other filter values from the UI controls.
    LocalDate startDate = startDatePicker.getValue();
    LocalDate endDate = endDatePicker.getValue();
    String timeGranularity = granularityChooser.getValue();
    Toggle selectedToggle = genderGroup.getSelectedToggle();
    String gender = selectedToggle != null ? selectedToggle.getUserData().toString() : null;

    // Log the filter criteria for debugging.
    logger.info("Building filter criteria with:");
    logger.info("Age Ranges: " + ageRanges);
    logger.info("Incomes: " + incomes);
    logger.info("Contexts: " + contexts);
    logger.info("Time Granularity: " + timeGranularity);
    logger.info("Start Date: " + startDate);
    logger.info("End Date: " + endDate);
    logger.info("Gender: " + gender);

    // Create and return the immutable FilterCriteriaDTO.
    return new FilterCriteriaDTO(
        ageRanges, incomes, contexts, timeGranularity, gender, startDate, endDate);
  }

  public void updateProgressBar(Double progress) {
    logger.info("Updating progress to " + progress);
    Platform.runLater(() -> filterProgressBar.setProgress(progress));
  }

  public void updateProgressLabel(String text) {
    logger.info("Updating progress label to " + text);
    Platform.runLater(() -> filterProgressLabel.setText(text));
  }
}
