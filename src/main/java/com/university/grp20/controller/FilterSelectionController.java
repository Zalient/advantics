package com.university.grp20.controller;

import com.university.grp20.model.*;
import java.awt.*;
import java.util.Objects;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.title.TextTitle;
import java.util.List;
import java.time.LocalDate;

public class FilterSelectionController extends Navigator {
  @FXML private CheckComboBox<String> ageGroupSelector, contextSelector, incomeSelector, dayOfWeekSelector;
  @FXML private ComboBox<String> timeOfDaySelector;
  @FXML private RadioButton maleRadioButton, femaleRadioButton;
  @FXML private ComboBox<String> granularityChooser;
  @FXML private DatePicker startDatePicker, endDatePicker;
  @FXML private Button applyChangesButton;
  @FXML private Label chartNameLabel;
  @FXML private ProgressBar filterProgressBar;
  @FXML private Label filterProgressLabel;
  private ToggleGroup genderGroup;
  private final Logger logger = LogManager.getLogger(FilterSelectionController.class);
  private FilterMode filterMode;
  private MetricsController metricsController;
  private ChartViewer chartViewer;
  private final OperationLogger operationLogger = new OperationLogger();

  @FXML
  private void initialize() {
    genderGroup = new ToggleGroup();
    maleRadioButton.setToggleGroup(genderGroup);
    femaleRadioButton.setToggleGroup(genderGroup);
    maleRadioButton.setUserData("Male");
    femaleRadioButton.setUserData("Female");

    dayOfWeekSelector.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
    timeOfDaySelector.getItems().addAll("Morning 06:00 - 11:59", "Afternoon 12:00 - 17:59", "Night 18:00 - 05:59");
    ageGroupSelector.getItems().addAll("<25", "25-34", "35-44", "45-54", ">54");
    incomeSelector.getItems().addAll("Low", "Medium", "High");
    contextSelector.getItems().addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
    Platform.runLater(
        () -> {
          ageGroupSelector.getStyleClass().add("blue-check-combo-box");
          incomeSelector.getStyleClass().add("blue-check-combo-box");
          contextSelector.getStyleClass().add("blue-check-combo-box");
        });
  }

  public void init(
      String filterMode, MetricsController metricsController, ChartViewer chartViewer) {
    if (Objects.equals(filterMode, "Metrics")) initMetrics(metricsController);
    else if (Objects.equals(filterMode, "Chart")) initChart(chartViewer);
  }

  @FXML
  private void applyChanges() {
    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
    Stage stage = (Stage) parentPane.getScene().getWindow();
    CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
    calculateMetricsService.setOnFilterStart(this::updateProgressBar);
    calculateMetricsService.setOnFilterLabelStart(this::updateProgressLabel);
    applyChangesButton.setDisable(true);
    FilterCriteriaDTO filterCriteriaDTO = buildFilterCriteria();
    operationLogger.log("Apply changes button clicked");

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
            "Time granularity: " + filterCriteriaDTO.timeGranularity() + "   " +
                    "Start Date: " + (filterCriteriaDTO.startDate() != null ? filterCriteriaDTO.startDate().toString() : " ") + "   " +
                    "End Date: " + (filterCriteriaDTO.endDate() != null ? filterCriteriaDTO.endDate().toString() : " ") + "   " +
                    "\n" +
                    "Gender: " + (filterCriteriaDTO.gender() != null ? filterCriteriaDTO.gender() : "All Gender") + "   " +
                    "Age Ranges: " + (filterCriteriaDTO.ageRanges() != null ? filterCriteriaDTO.ageRanges().toString() : "All Age Range") + "   " +
                    "Income: " + (filterCriteriaDTO.incomes() != null ? filterCriteriaDTO.incomes().toString() : "All Income Levels") + "   " +
                    "Contexts: " + (filterCriteriaDTO.contexts() != null ? filterCriteriaDTO.contexts().toString() : "All Context") + "   " +
                    "\n" +
                    "Bounce Type: " + globalSettings.getBounceType() + "   " +
                    "Bounce Value: " + globalSettings.getBounceValue() + "   ";

        TextTitle subtitle = new TextTitle(filterApplied);
        subtitle.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        subtitle.setPaint(new Color(0, 0, 140));
        newFilteredChart.addSubtitle(subtitle);
        operationLogger.log("Filters chosen: " + filterApplied);
      }

      if (newFilteredChart != null && chartViewer != null) {
        chartViewer.setChart(newFilteredChart);
      }
      stage.close();
    }
  }

  private void initMetrics(MetricsController metricsController) {
    this.metricsController = metricsController;
    setFilterMode(FilterMode.METRICS);
  }

  private void initChart(ChartViewer chartViewer) {
    this.chartViewer = chartViewer;
    setFilterMode(FilterMode.CHART);
  }

  private void setFilterMode(FilterMode mode) {
    this.filterMode = mode;
    applyFilterModeUI();
    }

  private Task<MetricsDTO> getMetricsDTOTask(
      CalculateMetricsService calculateMetricsService, FilterCriteriaDTO filterCriteriaDTO) {
    Stage stage = (Stage) parentPane.getScene().getWindow();
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
          stage.close();
          logger.info("Testing - clicks: " + task.getValue().clicks());
        });

    task.setOnFailed(
        e -> logger.error("Error calculating metrics: " + task.getException().getMessage()));
    return task;
  }

  private void applyFilterModeUI() {
    if (filterMode == FilterMode.METRICS) {
      granularityChooser.setVisible(false);
      chartNameLabel.setVisible(false);
      dayOfWeekSelector.setVisible(false);
      timeOfDaySelector.setVisible(false);
    }
    if (filterMode == FilterMode.CHART) {
      filterProgressBar.setVisible(false);
      filterProgressLabel.setVisible(false);
    }
  }

  private FilterCriteriaDTO buildFilterCriteria() {
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

    LocalDate startDate = startDatePicker.getValue();
    LocalDate endDate = endDatePicker.getValue();
    String timeGranularity = granularityChooser.getValue();
    Toggle selectedToggle = genderGroup.getSelectedToggle();
    String gender = selectedToggle != null ? selectedToggle.getUserData().toString() : null;

    List<String> daysOfWeek =
        dayOfWeekSelector.getCheckModel().getCheckedItems() != null
            ? List.copyOf(dayOfWeekSelector.getCheckModel().getCheckedItems())
            : List.of();

    List<String> timeOfDay =
        timeOfDaySelector.getValue() != null
            ? List.of(timeOfDaySelector.getValue())
            : List.of();

    logger.info("Building filter criteria with:");
    logger.info("Age Ranges: " + ageRanges);
    logger.info("Incomes: " + incomes);
    logger.info("Contexts: " + contexts);
    logger.info("Time Granularity: " + timeGranularity);
    logger.info("Start Date: " + startDate);
    logger.info("End Date: " + endDate);
    logger.info("Gender: " + gender);
    logger.info("Days Of Week: " + daysOfWeek);
    logger.info("Times Of Day: " + timeOfDay);

    return new FilterCriteriaDTO(
        ageRanges, incomes, contexts, timeGranularity, gender, startDate, endDate, daysOfWeek, timeOfDay);
  }

  public void updateProgressBar(Double progress) {
    logger.info("Updating progress to " + progress);
    Platform.runLater(() -> filterProgressBar.setProgress(progress));
  }

  public void updateProgressLabel(String text) {
    logger.info("Updating progress label to " + text);
    Platform.runLater(() -> filterProgressLabel.setText(text));
  }

  private enum FilterMode {
    METRICS,
    CHART
  }
}

