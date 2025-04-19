package com.university.grp20.controller.settings;

import com.university.grp20.controller.MetricsController;
import com.university.grp20.model.CalculateMetricsService;
import com.university.grp20.model.GenerateChartService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;

public class MetricSettingsController {
  @FXML private RadioButton pagesViewedButton;
  @FXML private RadioButton timeSpentButton;
  @FXML private TextField bounceValueField;
  private MetricsController metricsController;
  private final CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
  private ToggleGroup bounceChoiceGroup;

  @FXML
  private void initialize() {
    bounceChoiceGroup = new ToggleGroup();
    pagesViewedButton.setToggleGroup(bounceChoiceGroup);
    timeSpentButton.setToggleGroup(bounceChoiceGroup);
  }

  @FXML
  private void handleBounceChoice() {
    bounceValueField.setDisable(false);
  }

  @FXML
  private void applyBounceDef() {
    RadioButton selectedBounce = (RadioButton) bounceChoiceGroup.getSelectedToggle();
    String bounceValue = bounceValueField.getText();

    GenerateChartService.setBounceType(selectedBounce.getText());
    GenerateChartService.setBounceValue(bounceValue);
    calculateMetricsService.setBounceType(selectedBounce.getText());
    calculateMetricsService.setBounceValue(bounceValue);
  }

  public void init(MetricsController metricsController) {
    this.metricsController = metricsController;
  }
}
