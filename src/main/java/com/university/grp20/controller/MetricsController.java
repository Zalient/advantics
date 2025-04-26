package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.*;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MetricsController extends Navigator {
  @FXML private Button resetDashButton;
  @FXML private Label impressionsLabel;
  @FXML private Label clicksLabel;
  @FXML private Label uniquesLabel;
  @FXML private Label bouncesLabel;
  @FXML private Label conversionsLabel;
  @FXML private Label totalLabel;
  @FXML private Label ctrLabel;
  @FXML private Label cpaLabel;
  @FXML private Label cpcLabel;
  @FXML private Label cpmLabel;
  @FXML private Label bounceRateLabel;
  @FXML private Button filterButton;
  @FXML private Button pdfButton;
  @FXML private Button csvButton;
  private final Logger logger = LogManager.getLogger(MetricsController.class);
  private final OperationLogger operationLogger = new OperationLogger();
  private MetricsDTO metricsDTO;
  private final CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
  @FXML private Button helpButton;



  @FXML
  private void initialize() {
    calculateMetricsService.setOnFilterStart(
        progress -> {
          /* no-op */
        });
    calculateMetricsService.setOnFilterLabelStart(
        text -> {
          /* no-op */
        });
    metricsDTO = calculateMetricsService.fetchMetrics(null);
    setMetrics(metricsDTO);
  }

  public void setMetrics(MetricsDTO metricsDTO) {
    if (metricsDTO == null) return;
    impressionsLabel.setText(String.format("%.0f", metricsDTO.impressions()));
    clicksLabel.setText(String.format("%.0f", metricsDTO.clicks()));
    uniquesLabel.setText(String.format("%.0f", metricsDTO.uniques()));
    bouncesLabel.setText(String.format("%.0f", metricsDTO.bounces()));
    conversionsLabel.setText(String.format("%.0f", metricsDTO.conversions()));
    totalLabel.setText(String.format("£%.2f", metricsDTO.totalCost() / 100));
    ctrLabel.setText(String.format("%.2f%%", metricsDTO.ctr() * 100));
    cpaLabel.setText(String.format("£%.2f", metricsDTO.cpa() / 100));
    cpcLabel.setText(String.format("£%.2f", metricsDTO.cpc() / 100));
    cpmLabel.setText(String.format("£%.2f", metricsDTO.cpm() / 100));
    bounceRateLabel.setText(String.format("%.2f%%", metricsDTO.bounceRate() * 100));
  }

  @FXML
  private void showFileSelection() {
    if (User.getRole().equals("Viewer")) {
      UIManager.switchContent(parentPane, UIManager.createFxmlLoader("/fxml/LoginPane.fxml"));
    } else {
      UIManager.switchContent(
          parentPane, UIManager.createFxmlLoader("/fxml/FileSelectionPane.fxml"));
    }
  }

  @FXML
  private void showMetricsFilter() {
    try {
      logger.info("Filter button clicked");
      FXMLLoader filterLoader = UIManager.createFxmlLoader("/fxml/FilterSelectionPopup.fxml");
      filterLoader.load();
      operationLogger.log("Metrics filter button clicked, displaying filter options");
      FXMLLoader loader = UIManager.createFxmlLoader("/fxml/FilterSelectionPopup.fxml");
      loader.load();

      FilterSelectionController controller = filterLoader.getController();
      controller.init("Metrics", this, null);

      UIManager.showModalStage("Metrics Filtering", filterLoader, false);
    } catch (IOException e) {
      logger.error("Error loading metrics filter:", e);
    }
  }

  @FXML
  private void saveAsPDF() throws IOException {
    operationLogger.log("Metrics report save as PDF button clicked");
    if (metricsDTO == null) {
      logger.error("Cannot export to PDF: metricsDTO is null.");
      return;
    }

    String filePath = ExportService.askForPDFFilename();
    if (filePath != null) {
      ExportService.dashboardToPDF(metricsDTO, filePath);
      logger.info("Metrics exported successfully to PDF.");
      operationLogger.log("Metrics report saved to PDF at " + filePath);
      UIManager.showAlert("Success", "Metrics report saved to PDF at " + filePath);
    } else {
      operationLogger.log("Metrics report not saved as PDF");
    }
  }

  @FXML
  private void saveAsCSV() throws IOException {
    operationLogger.log("Metrics report save as CSV button clicked");
    String filePath = ExportService.askForCSVFilename();
    ExportService.dashboardToCSV(metricsDTO, filePath);
    if (filePath != null) {
      operationLogger.log("Metrics report saved to CSV at " + filePath);
      UIManager.showAlert("Success", "Metrics report saved to CSV at " + filePath);
    } else {
      operationLogger.log("Metrics report not saved as CSV");
    }
  }

  @FXML
  private void showCharts() {
    UIManager.switchContent(parentPane, UIManager.createFxmlLoader("/fxml/ChartsPane.fxml"), true);
    operationLogger.log("Charts page chosen, displaying charts page");
  }

  @FXML
  private void handleSettingsLoad() {
    UIManager.switchContent(
        parentPane, UIManager.createFxmlLoader("/fxml/settings/SettingsPane.fxml"));
    operationLogger.log("Settings button clicked, displaying settings page");
  }

  public void disableForViewer() {
    logger.info("disableForViewer called");
    boolean status = User.getRole().equals("Viewer");

    filterButton.setDisable(status);
    pdfButton.setDisable(status);
    csvButton.setDisable(status);
  }

  @FXML
  private void handleResetClick(){
    operationLogger.log("Reset dashboard button clicked");
    MetricsDTO defaultMetrics = calculateMetricsService.fetchMetrics(null);
    setMetrics(defaultMetrics);
    UIManager.showAlert("Success", "Dashboard successfully reset to default");
  }
}
