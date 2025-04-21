package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MetricsController {
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

  public MetricsDTO metricsDTO;

  @FXML
  private void initialize() {
    CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
    calculateMetricsService.setOnFilterStart(
        progress -> {
          /* no-op */
        });
    calculateMetricsService.setOnFilterLabelStart(
        text -> {
          /* no-op */
        });
    setMetrics(calculateMetricsService.fetchMetrics(null));
  }

  public void setMetrics(MetricsDTO metricsDTO) {
    if (metricsDTO == null) return;
    impressionsLabel.setText(String.format("%.2f", metricsDTO.impressions()) + " impressions");
    clicksLabel.setText(String.format("%.2f", metricsDTO.clicks()) + " clicks");
    uniquesLabel.setText(String.format("%.2f", metricsDTO.uniques()) + " unique IDs");
    bouncesLabel.setText(String.format("%.2f", metricsDTO.bounces()) + " bounces");
    conversionsLabel.setText(String.format("%.2f", metricsDTO.conversions()) + " conversions");
    totalLabel.setText(String.format("%.2f", metricsDTO.totalCost() / 100) + " pounds");
    ctrLabel.setText(String.format("%.2f%%", metricsDTO.ctr() * 100));
    cpaLabel.setText(String.format("%.2f pounds per conversion", metricsDTO.cpa() / 100));
    cpcLabel.setText(String.format("%.2f pence per click", metricsDTO.cpc()));
    cpmLabel.setText(String.format("%.2f pounds per thousand impressions", metricsDTO.cpm() / 100));
    bounceRateLabel.setText(String.format("%.2f bounces per click", metricsDTO.bounceRate()));
  }

  @FXML
  private void showFileSelection() {
    if (User.getRole().equals("Viewer")) {
      UIManager.switchScene(UIManager.createFXMLLoader("/fxml/LoginScene.fxml"), false);
      operationLogger.log("Back button clicked, returning to login page");
    } else {
      UIManager.switchScene(UIManager.createFXMLLoader("/fxml/FileSelectionScene.fxml"), false);
      operationLogger.log("Back button clicked, returning to file upload page");
    }
  }

  @FXML
  private void showMetricsFilter() {
    try {
      logger.info("Filter button clicked");
      operationLogger.log("Metrics filter button clicked, displaying filter options");
      FXMLLoader loader = UIManager.createFXMLLoader("/fxml/FilterSelectionModal.fxml");
      loader.load();

      FilterSelectionController controller = loader.getController();
      controller.setFilterMode(FilterSelectionController.FilterMode.METRICS);
      controller.setMetricsController(this);

      UIManager.showModal(loader, false);
    } catch (IOException e) {
      logger.error("Error loading metrics filter: " + e.getMessage());
    }
  }

  @FXML
  private void saveAsPDF() throws IOException {
    operationLogger.log("Save as PDF button clicked");
    if (metricsDTO == null) {
      logger.error("Cannot export to PDF: metricsDTO is null.");
      return;
    }

    String filePath = ExportService.askForPDFFilename();
    if (filePath != null) {
      ExportService.dashboardToPDF(metricsDTO, filePath);
      logger.info("Metrics exported successfully to PDF.");
      operationLogger.log("Metrics report saved to PDF at " + filePath);
    } else {
      operationLogger.log("Metrics report not saved");
    }
  }

  @FXML
  private void saveAsCSV() throws IOException {
    operationLogger.log("Save as CSV button clicked");
    String filePath = ExportService.askForCSVFilename();
    ExportService.dashboardToCSV(metricsDTO, filePath);
    if (filePath != null) {
      operationLogger.log("Metrics report saved to CSV at " + filePath);
    } else {
      operationLogger.log("Metrics report not saved");
    }
  }

  @FXML
  private void showCharts() {
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/ChartsScene.fxml"));
    operationLogger.log("Charts button clicked, displaying charts page");
  }

  @FXML
  private void handleSettingsLoad() {
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/SettingsScene.fxml"), false);
    operationLogger.log("Settings button clicked, displaying settings page");
  }

  public void disableForViewer() {
    logger.info("disableForViewer called");
    boolean status = User.getRole().equals("Viewer");

    filterButton.setDisable(status);
    pdfButton.setDisable(status);
    csvButton.setDisable(status);
  }
}
