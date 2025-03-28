package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.*;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MetricsController extends Navigator {
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

    this.metricsDTO = calculateMetricsService.getMetrics(null);
    if (this.metricsDTO == null) {
      logger.warn("MetricsDTO is null. Creating a new empty MetricsDTO.");
      this.metricsDTO = new MetricsDTO();
    }
    setMetrics(metricsDTO);
  }

  public void setMetrics(MetricsDTO metricsDTO) {
    if (metricsDTO == null) return;
    impressionsLabel.setText(String.format("%.0f", metricsDTO.getImpressions()));
    clicksLabel.setText(String.format("%.0f", metricsDTO.getClicks()));
    uniquesLabel.setText(String.format("%.0f", metricsDTO.getUniques()));
    bouncesLabel.setText(String.format("%.0f", metricsDTO.getBounces()));
    conversionsLabel.setText(String.format("%.0f", metricsDTO.getConversions()));
    totalLabel.setText(String.format("£%.2f", metricsDTO.getTotalCost() / 100));
    ctrLabel.setText(String.format("%.2f%%", metricsDTO.getCtr() * 100));
    cpaLabel.setText(String.format("£%.2f", metricsDTO.getCpa() / 100));
    cpcLabel.setText(String.format("£%.2f", metricsDTO.getCpc() / 100));
    cpmLabel.setText(String.format("£%.2f", metricsDTO.getCpm() / 100));
    bounceRateLabel.setText(String.format("%.2f%%", metricsDTO.getBounceRate() * 100));
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
    operationLogger.log("Metrics filter chosen, displaying filter options");
    try {
      logger.info("Filter button clicked");
      FXMLLoader filterLoader = UIManager.createFxmlLoader("/fxml/FilterSelectionModal.fxml");
      filterLoader.load();

      FilterSelectionController controller = filterLoader.getController();
      controller.init("Metrics", this, null);

      UIManager.showModalStage("Metrics Filtering", filterLoader, false);
    } catch (IOException e) {
      logger.error("Error loading metrics filter:", e);
    }
  }

  @FXML
  private void saveAsPDF() throws IOException {
    if (metricsDTO == null) {
      logger.error("Cannot export to PDF: metricsDTO is null.");
      return;
    }

    String filePath = ExportService.askForPDFFilename();
    if (filePath != null) {
      ExportService.dashboardToPDF(metricsDTO, filePath);
      logger.info("Metrics exported successfully to PDF.");
    }
  }

  @FXML
  private void saveAsCSV() throws IOException {
    String filePath = ExportService.askForCSVFilename();
    ExportService.dashboardToCSV(metricsDTO, filePath);
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
  }

  public void disableForViewer() {
    logger.info("disableForViewer called");
    boolean status = User.getRole().equals("Viewer");

    filterButton.setDisable(status);
    pdfButton.setDisable(status);
    csvButton.setDisable(status);
  }
}
