package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import static com.university.grp20.model.DBHelper.executeQuery;
import static com.university.grp20.model.DBHelper.getConnection;

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
  private String campaignName;

  @FXML private Button helpButton;

  private LocalDate startDate;
  private LocalDate endDate;

  @FXML
  private void initialize() {
    genderGroup = new ToggleGroup();
    maleRadioButton.setToggleGroup(genderGroup);
    femaleRadioButton.setToggleGroup(genderGroup);
    maleRadioButton.setUserData("Male");
    femaleRadioButton.setUserData("Female");


    dayOfWeekSelector.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
    timeOfDaySelector.getItems().addAll("Morning 06:00 - 11:59", "Afternoon 12:00 - 17:59", "Night 18:00 - 05:59", "None");
    ageGroupSelector.getItems().addAll("<25", "25-34", "35-44", "45-54", ">54");
    incomeSelector.getItems().addAll("Low", "Medium", "High");
    contextSelector.getItems().addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
    Platform.runLater(
        () -> {
          ageGroupSelector.getStyleClass().add("custom-check-combo-box");
          incomeSelector.getStyleClass().add("custom-check-combo-box");
          contextSelector.getStyleClass().add("custom-check-combo-box");
          dayOfWeekSelector.getStyleClass().add("custom-check-combo-box");
          timeOfDaySelector.getStyleClass().add("custom-combo-box");
        });

    dayOfWeekSelector.setDisable(true);
    timeOfDaySelector.setDisable(true);
    granularityChooser.setOnAction(event -> {
      String chosenGranularity = granularityChooser.getValue();
      if (chosenGranularity.equals("Per Day")) {
        dayOfWeekSelector.setDisable(false);
        timeOfDaySelector.setDisable(true);
      } else if (chosenGranularity.equals("Per Hour")) {
        timeOfDaySelector.setDisable(false);
        dayOfWeekSelector.setDisable(true);
      } else {
        timeOfDaySelector.setDisable(true);
        dayOfWeekSelector.setDisable(true);
      }
    });
  }

  public void init(String filterMode, MetricsController metricsController, ChartViewer chartViewer, String campaignName) {
    if (Objects.equals(filterMode, "Metrics")) {
      initMetrics(metricsController);
    } else if (Objects.equals(filterMode, "Chart")) {
      initChart(chartViewer);
    }
    this.campaignName = campaignName;
    fetchMinDate(campaignName);
    fetchMaxDate(campaignName);
    startDatePicker.setValue(startDate);
    endDatePicker.setValue(endDate);
  }

  //min and max date may need correcting
  public void fetchMinDate(String campaignName) {
    String query = "SELECT MIN(Date)" + " FROM impressionLog";
    try (Connection conn = getConnection(campaignName)) {
      startDate = fetchDate(conn, query);
    } catch (SQLException e) {
      throw new RuntimeException("Unable to obtain DB connection", e);
    }
  }

  public void fetchMaxDate(String campaignName) {
    String query = "SELECT MAX(Date)" + " FROM impressionLog";
    try (Connection conn = getConnection(campaignName)) {
      endDate = fetchDate(conn, query);
    } catch (SQLException e) {
      throw new RuntimeException("Unable to obtain DB connection", e);
    }
  }

  private static LocalDate fetchDate(Connection conn, String query) {
    try (ResultSet rs = executeQuery(conn, query)) {
      if (rs.next()) {
        return rs.getDate(1).toLocalDate();
      }
      throw new RuntimeException("No rows returned: " + query);
    } catch (SQLException e) {
      throw new RuntimeException("Error executing query: " + query, e);
    }
  }


  @FXML
  private void applyChanges() {
    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
    Stage stage = (Stage) parentPane.getScene().getWindow();
    CalculateMetricsService calculateMetricsService = null;
    System.out.println("DEBUG: Filter mode is " + filterMode);
    if (filterMode == FilterMode.METRICS) {
      calculateMetricsService = new CalculateMetricsService(User.getSelectedCampaign());
      System.out.println("DEBUG: Calculating metrics for campaign " + User.getSelectedCampaign());
    } else {
      calculateMetricsService = new CalculateMetricsService(campaignName);
      System.out.println("DEBUG: Calculating metrics for campaign " + campaignName);
    }
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
      GenerateChartService generateChartService = new GenerateChartService(campaignName);
      String metricType =
          (chartViewer != null && chartViewer.getUserData() != null)
              ? chartViewer.getUserData().toString()
              : "";
      JFreeChart newFilteredChart =
          generateChartService.getFilteredChart(metricType, filterCriteriaDTO);

      if (newFilteredChart != null && chartViewer != null) {
        String filterApplied =
            "Time granularity: " + defaultTimeGranularity(filterCriteriaDTO) + "   " +
                    "Start Date: " + (filterCriteriaDTO.startDate() != null ? filterCriteriaDTO.startDate().toString() : " ") + "   " +
                    "End Date: " + (filterCriteriaDTO.endDate() != null ? filterCriteriaDTO.endDate().toString() : " ") + "   " +
                    "\n" +
                    "Gender: " + (filterCriteriaDTO.gender() != null ? filterCriteriaDTO.gender() : "All Gender") + "   " +
                    "Age Ranges: " + (filterCriteriaDTO.ageRanges() != null ? filterCriteriaDTO.ageRanges().toString() : "All Age Range") + "   " +
                    "Income: " + (filterCriteriaDTO.incomes() != null ? filterCriteriaDTO.incomes().toString() : "All Income Levels") + "   " +
                    "Contexts: " + (filterCriteriaDTO.contexts() != null ? filterCriteriaDTO.contexts().toString() : "All Context") + "   " +
                    "\n" +
                    "Bounce Type: " + globalSettings.getBounceType() + "   " +
                    "Bounce Value: " + globalSettings.getBounceValue() + "   " +
                    specialFilterSubtitle(filterCriteriaDTO);

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

  private String defaultTimeGranularity (FilterCriteriaDTO filterDTO){
    if (filterDTO.timeGranularity() == null){
      return "Per Day";
    } else {
      return filterDTO.timeGranularity();
    }
  }

  private String specialFilterSubtitle (FilterCriteriaDTO filterDTO){
    if ("Per Day".equals(filterDTO.timeGranularity()) && filterDTO.daysOfWeek() != null && !filterDTO.daysOfWeek().isEmpty()){
      return "Days Of Week: " + filterDTO.daysOfWeek();
    } else if ("Per Hour".equals(filterDTO.timeGranularity()) && filterDTO.timeOfDay() != null && !filterDTO.timeOfDay().isEmpty()){
      return "Times Of Day: " + filterDTO.timeOfDay();
    }
    return "";
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
      chartNameLabel.setText("Metrics Filtering");
      dayOfWeekSelector.setVisible(false);
      timeOfDaySelector.setVisible(false);
    }
    if (filterMode == FilterMode.CHART) {
      filterProgressBar.setVisible(false);
      filterProgressLabel.setVisible(false);
      chartNameLabel.setText("Chart Filtering");
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

  @FXML
  private void showHelpGuide() {
    FXMLLoader loader = UIManager.createFxmlLoader("/fxml/HelpGuidePane.fxml");
    try {
      loader.load();
      HelpGuideController helpController = loader.getController();
      if (filterMode == FilterMode.METRICS) {
        helpController.setupCarousel("Metric-Filter");
        UIManager.showPopupStage("Metrics Filter Help Guide", loader, false);
        operationLogger.log("Metrics Filter Help Guide Icon clicked");
      } else if (filterMode == FilterMode.CHART) {
        helpController.setupCarousel("Chart-Filter");
        UIManager.showPopupStage("Charts Filter Help Guide", loader, false);
        operationLogger.log("Charts Filter Help Guide Icon clicked");
      }
    } catch (IOException e) {
      logger.error("Failed to open Help Guide", e);
    }
  }
}

