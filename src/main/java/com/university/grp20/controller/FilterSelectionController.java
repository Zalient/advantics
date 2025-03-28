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

public class FilterSelectionController extends Navigator {
  @FXML private CheckComboBox<String> ageGroupSelector, contextSelector, incomeSelector;
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

    ageGroupSelector.getItems().addAll("<25", "25-34", "35-44", "45-54", ">54");
    incomeSelector.getItems().addAll("Low", "Medium", "High");
    contextSelector
        .getItems()
        .addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
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
    Stage stage = (Stage) parentPane.getScene().getWindow();
    CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
    calculateMetricsService.setOnFilterStart(this::updateProgressBar);
    calculateMetricsService.setOnFilterLabelStart(this::updateProgressLabel);
    applyChangesButton.setDisable(true);
    FilterCriteriaDTO filterCriteriaDTO = buildFilterCriteria();
    operationLogger.log("Filters chosen and applied");

    if (filterMode == FilterMode.METRICS) {
      logger.info("Calculating filtered metrics");
      Task<MetricsDTO> task =
          new Task<>() {
            @Override
            protected MetricsDTO call() {
              return calculateMetricsService.getMetrics(filterCriteriaDTO);
            }
          };

      task.setOnSucceeded(
          e -> {
            metricsController.setMetrics(task.getValue());
            stage.close();
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
        String filterApplied =
            "Time granularity: "
                + filterCriteriaDTO.getTimeGranularity()
                + "   "
                + "Start Date: "
                + (filterCriteriaDTO.getStartDate() != null
                    ? filterCriteriaDTO.getStartDate().toString()
                    : " ")
                + "   "
                + "End Date: "
                + (filterCriteriaDTO.getEndDate() != null
                    ? filterCriteriaDTO.getEndDate().toString()
                    : " ")
                + "   "
                + "\n"
                + "Gender: "
                + (filterCriteriaDTO.getGender() != null
                    ? filterCriteriaDTO.getGender()
                    : "All Gender")
                + "   "
                + "Age Ranges: "
                + (filterCriteriaDTO.getAgeRanges() != null
                    ? filterCriteriaDTO.getAgeRanges().toString()
                    : "All Age Range")
                + "   "
                + "Income: "
                + (filterCriteriaDTO.getIncomes() != null
                    ? filterCriteriaDTO.getIncomes().toString()
                    : "All Income Levels")
                + "   "
                + "Contexts: "
                + (filterCriteriaDTO.getContexts() != null
                    ? filterCriteriaDTO.getContexts().toString()
                    : "All Context");

        TextTitle subtitle = new TextTitle(filterApplied);
        subtitle.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        subtitle.setPaint(new Color(0, 0, 140));
        newFilteredChart.addSubtitle(subtitle);
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
