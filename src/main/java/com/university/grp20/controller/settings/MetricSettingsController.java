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

  @FXML private CheckBox impressionsCheckbox;
  @FXML private CheckBox clicksCheckbox;
  @FXML private CheckBox uniquesCheckbox;
  @FXML private CheckBox conversionsCheckbox;
  @FXML private CheckBox bouncesCheckbox;
  @FXML private CheckBox bounceRateCheckbox;
  @FXML private CheckBox ctrCheckbox;
  @FXML private CheckBox cpaCheckbox;
  @FXML private CheckBox cpmCheckbox;
  @FXML private CheckBox cpcCheckbox;
  @FXML private CheckBox totalCostCheckbox;

  @FXML private Button applyVisibilityButton;

  @FXML
  private void initialize() {
    bounceGroup = new ToggleGroup();
    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
    String bounceType = globalSettings.getBounceType();
    String bounceValue = globalSettings.getBounceValue();
    bounceDefLabel.setText("Current Setting: " + bounceType + " : " + bounceValue);
    pagesViewedButton.setToggleGroup(bounceGroup);
    timeSpentButton.setToggleGroup(bounceGroup);
    initializeCheckboxes();
  }

  private void initializeCheckboxes() {
    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();

    impressionsCheckbox.setSelected(globalSettings.isMetricVisible("impressions"));
    clicksCheckbox.setSelected(globalSettings.isMetricVisible("clicks"));
    uniquesCheckbox.setSelected(globalSettings.isMetricVisible("uniques"));
    conversionsCheckbox.setSelected(globalSettings.isMetricVisible("conversions"));
    bouncesCheckbox.setSelected(globalSettings.isMetricVisible("bounces"));
    bounceRateCheckbox.setSelected(globalSettings.isMetricVisible("bounceRate"));
    ctrCheckbox.setSelected(globalSettings.isMetricVisible("ctr"));
    cpaCheckbox.setSelected(globalSettings.isMetricVisible("cpa"));
    cpmCheckbox.setSelected(globalSettings.isMetricVisible("cpm"));
    cpcCheckbox.setSelected(globalSettings.isMetricVisible("cpc"));
    totalCostCheckbox.setSelected(globalSettings.isMetricVisible("totalCost"));
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

  @FXML
  private void applyMetricVisibility() {
    operationLogger.log("Metric visibility apply button clicked");

    GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();

    globalSettings.setMetricVisibility("impressions", impressionsCheckbox.isSelected());
    globalSettings.setMetricVisibility("clicks", clicksCheckbox.isSelected());
    globalSettings.setMetricVisibility("uniques", uniquesCheckbox.isSelected());
    globalSettings.setMetricVisibility("bounces", bouncesCheckbox.isSelected());
    globalSettings.setMetricVisibility("conversions", conversionsCheckbox.isSelected());
    globalSettings.setMetricVisibility("totalCost", totalCostCheckbox.isSelected());
    globalSettings.setMetricVisibility("ctr", ctrCheckbox.isSelected());
    globalSettings.setMetricVisibility("cpa", cpaCheckbox.isSelected());
    globalSettings.setMetricVisibility("cpc", cpcCheckbox.isSelected());
    globalSettings.setMetricVisibility("cpm", cpmCheckbox.isSelected());
    globalSettings.setMetricVisibility("bounceRate", bounceRateCheckbox.isSelected());

    Alert successAlert = new Alert(Alert.AlertType.CONFIRMATION);
    operationLogger.log("Metric visibility settings updated");
    successAlert.setTitle("Confirmation");
    successAlert.setHeaderText(null);
    successAlert.setContentText("Metric visibility settings successfully updated");
    successAlert.showAndWait();

    if (metricsController != null) {
      metricsController.updateMetricVisibility();
    }
  }
 /**
  public void init(MetricsController metricsController) {
    this.metricsController = metricsController;
  }
  **/
}
