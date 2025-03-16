package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.CalculateMetricsService;
import com.university.grp20.model.MetricsDTO;
import java.io.IOException;

import com.university.grp20.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
  @FXML private Button backButton;
  private final Logger logger = LogManager.getLogger(MetricsController.class);

  @FXML
  private void initialize() {
    CalculateMetricsService calculateMetricsService = new CalculateMetricsService();
    setMetrics(calculateMetricsService.getMetrics(null));


    Platform.runLater(() -> {
      logger.info("Detected role: " + User.getRole());

      if (User.getRole().equals("Viewer")) {
        backButton.setVisible(false);
      } else {
        backButton.setVisible(true);
      }
    });

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
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/FileSelectionScene.fxml"), false);
  }

  @FXML
  private void showMetricsFilter() {
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
  private void showCharts() {
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/ChartsScene.fxml"));
  }
}
