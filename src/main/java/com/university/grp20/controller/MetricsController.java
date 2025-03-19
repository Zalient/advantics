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
  @FXML
  private Label impressionsLabel;
  @FXML
  private Label clicksLabel;
  @FXML
  private Label uniquesLabel;
  @FXML
  private Label bouncesLabel;
  @FXML
  private Label conversionsLabel;
  @FXML
  private Label totalLabel;
  @FXML
  private Label ctrLabel;
  @FXML
  private Label cpaLabel;
  @FXML
  private Label cpcLabel;
  @FXML
  private Label cpmLabel;
  @FXML
  private Label bounceRateLabel;
  @FXML
  private Button backButton;
  @FXML
  private Button settingsButton;
  @FXML
  private Button filterButton;
  @FXML
  private Button pdfButton;
  @FXML
  private Button csvButton;
  private final Logger logger = LogManager.getLogger(MetricsController.class);
  private final OperationLogger operationLogger = new OperationLogger();


  public MetricsDTO metricsDTO;


  @FXML
  private void initialize() {
    CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
    calculateMetricsService.setOnFilterStart(progress -> { /* no-op */ });
    calculateMetricsService.setOnFilterLabelStart(text -> { /* no-op */ });
    setMetrics(calculateMetricsService.getMetrics(null));



/**
 * Platform.runLater(() -> {
 *       logger.info("Detected role: " + User.getRole());
 *
 *       if (User.getRole().equals("Viewer")) {
 *         backButton.setVisible(false);
 *       } else {
 *         backButton.setVisible(true);
 *       }
 *     });
 */

  }
  
  public void setMetrics(MetricsDTO metricsDTO) {
    if (metricsDTO == null) return;
    impressionsLabel.setText(String.format("%.2f", metricsDTO.getImpressions()) + " impressions");
    clicksLabel.setText(String.format("%.2f", metricsDTO.getClicks()) + " clicks");
    uniquesLabel.setText(String.format("%.2f", metricsDTO.getUniques()) + " unique IDs");
    bouncesLabel.setText(String.format("%.2f", metricsDTO.getBounces()) + " bounces");
    conversionsLabel.setText(String.format("%.2f", metricsDTO.getConversions()) + " conversions");
    totalLabel.setText(String.format("%.2f", metricsDTO.getTotalCost() / 100) + " pounds");
    ctrLabel.setText(String.format("%.2f%%", metricsDTO.getCtr() * 100));
    cpaLabel.setText(String.format("%.2f pounds per conversion", metricsDTO.getCpa() / 100));
    cpcLabel.setText(String.format("%.2f pence per click", metricsDTO.getCpc()));
    cpmLabel.setText(
            String.format("%.2f pounds per thousand impressions", metricsDTO.getCpm() / 100));
    bounceRateLabel.setText(String.format("%.2f bounces per click", metricsDTO.getBounceRate()));
  }

  @FXML
  private void showFileSelection() {
    if (User.getRole().equals("Viewer")) {
      UIManager.switchScene(UIManager.createFXMLLoader("/fxml/LoginScene.fxml"), false);
    } else {
      UIManager.switchScene(UIManager.createFXMLLoader("/fxml/FileSelectionScene.fxml"), false);
    }

  }

  @FXML
  private void showMetricsFilter() {
    operationLogger.log("Metrics filter chosen, displaying filter options");
    try {
      logger.info("Filter button clicked");
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
    String filePath = ExportService.askForPDFFilename();
    ExportService.dashboardToPDF(metricsDTO, filePath);
  }

  @FXML
  private void saveAsCSV() throws IOException {
    String filePath = ExportService.askForCSVFilename();
    ExportService.dashboardToCSV(metricsDTO, filePath);
  }

  @FXML
  private void showCharts() {
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/ChartsScene.fxml"));
    operationLogger.log("Charts page chosen, displaying charts page");
  }

  @FXML
  private void handleSettingsLoad() {
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/SettingsScene.fxml"), false);
  }

  public void disableForViewer() {
    logger.info("disableForViewer called");
    boolean status = User.getRole().equals("Viewer");

    filterButton.setDisable(status);
    pdfButton.setDisable(status);
    csvButton.setDisable(status);


  }
}
