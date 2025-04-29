package com.university.grp20.controller.settings;

import com.university.grp20.controller.MetricsController;
import com.university.grp20.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MetricSettingsController {
  @FXML private RadioButton pagesViewedButton;
  @FXML private RadioButton timeSpentButton;
  @FXML private TextField bounceValField;
  private MetricsController metricsController;
  private final CalculateMetricsService calculateMetricsService = new CalculateMetricsService(User.getSelectedCampaign());
  private ToggleGroup bounceGroup;
  @FXML private Label bounceDefLabel;
  @FXML private Button applyBounceButton;
  private final OperationLogger operationLogger = new OperationLogger();

  @FXML
  private void initialize() {
    bounceGroup = new ToggleGroup();
    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
    String bounceType = globalSettings.getBounceType();
    String bounceValue = globalSettings.getBounceValue();
    bounceDefLabel.setText("Current Setting: " + bounceType + " : " + bounceValue);
    pagesViewedButton.setToggleGroup(bounceGroup);
    timeSpentButton.setToggleGroup(bounceGroup);
  }

  @FXML
  private void handleBounceChoice() {
    bounceValField.setDisable(false);
    applyBounceButton.setDisable(false);
  }

  @FXML
  private void bounceApply() {
    operationLogger.log("Bounce apply button clicked");
    Alert inputAlert = new Alert(Alert.AlertType.ERROR);
    RadioButton selectedBounce = (RadioButton) bounceGroup.getSelectedToggle();
    String bounceVal = bounceValField.getText();

    if (bounceVal.isEmpty()) {
      operationLogger.log("Bounce value not entered");
      inputAlert.setTitle("Error");
      inputAlert.setHeaderText(null);
      inputAlert.setContentText("Empty input, please enter a value");
      inputAlert.showAndWait();
    } else {
      try {
        int bounceValue = Integer.parseInt(bounceVal);

        if (bounceValue < 0) {
          operationLogger.log("Negative bounce value entered");
          inputAlert.setContentText("Input is a negative number, please enter positive integer");
          inputAlert.showAndWait();
          return;
        }
        GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
        globalSettings.setBounceType(selectedBounce.getText());
        globalSettings.setBounceValue(bounceVal);

        bounceDefLabel.setText("Current Setting: " + selectedBounce.getText() + " = " + bounceVal);

        Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);
        operationLogger.log("Bounce value redefined as: " + selectedBounce.getText() + " = " + bounceVal);
        successAlert.setTitle("Confirmation");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Bounce successfully redefined");
        successAlert.showAndWait();
      } catch (NumberFormatException ex) {
        operationLogger.log("Bounce value entered of wrong type");
        inputAlert.setContentText("Input is not an integer, wrong type");
        inputAlert.showAndWait();
      }
    }
  }

  public void init(MetricsController metricsController) {
    this.metricsController = metricsController;
  }
}
