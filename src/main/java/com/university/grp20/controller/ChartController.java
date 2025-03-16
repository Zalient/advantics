package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.ExportService;
import com.university.grp20.model.GenerateChartService;
import java.io.IOException;
import java.util.Optional;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import java.awt.Color;

public class ChartController {

  @FXML private FlowPane addChartFlowPane;

  private final Logger logger = LogManager.getLogger(ChartController.class);

  @FXML
  private void showMetrics() {
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/MetricsScene.fxml"));
  }

  @FXML
  private void showFileSelection() {
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/FileSelectionScene.fxml"), false);
  }

  @FXML
  private void showFilterSelection(ChartViewer chartViewer) {
    try {
      FXMLLoader loader = UIManager.createFXMLLoader("/fxml/FilterSelectionModal.fxml");
      loader.load();

      FilterSelectionController controller = loader.getController();
      controller.setFilterMode(FilterSelectionController.FilterMode.CHART);
      controller.setChartViewer(chartViewer);

      UIManager.showModal(loader, false);
    } catch (IOException e) {
      logger.error("Error loading chart filter: " + e.getMessage());
    }
  }

  @FXML
  private void addImpressionsChart() {
    JFreeChart chart = new GenerateChartService().impressionsChart();
    addChart(chart, "Impressions");
  }

  @FXML
  private void addClicksChart() {
    JFreeChart chart = new GenerateChartService().clicksChart();
    addChart(chart, "Clicks");
  }

  @FXML
  private void addUniquesChart() {
    JFreeChart chart = new GenerateChartService().uniquesChart();
    addChart(chart, "Uniques");
  }

  @FXML
  private void addBouncesChart() {
    JFreeChart chart = new GenerateChartService().bouncesChart();
    addChart(chart, "Bounces");
  }

  @FXML
  private void addConversionsChart() {
    JFreeChart chart = new GenerateChartService().conversionsChart();
    addChart(chart, "Conversions");
  }

  @FXML
  private void addTotalCostChart() {
    JFreeChart chart = new GenerateChartService().totalCostChart();
    addChart(chart, "Total Cost");
  }

  @FXML
  private void addCTRChart() {
    JFreeChart chart = new GenerateChartService().ctrChart();
    addChart(chart, "CTR");
  }

  @FXML
  private void addCPAChart() {
    JFreeChart chart = new GenerateChartService().cpaChart();
    addChart(chart, "CPA");
  }

  @FXML
  private void addCPCChart() {
    JFreeChart chart = new GenerateChartService().cpcChart();
    addChart(chart, "CPC");
  }

  @FXML
  private void addCPMChart() {
    JFreeChart chart = new GenerateChartService().cpmChart();
    addChart(chart, "CPM");
  }

  @FXML
  private void addBounceRateChart() {
    JFreeChart chart = new GenerateChartService().bounceRateChart();
    addChart(chart, "Bounce Rate");
  }

  @FXML
  private void binSizePrompt() {
    Dialog<Integer> numBinsDialog = new Dialog<>();
    numBinsDialog.setTitle("Set Number Of Bins");

    TextField numBinsField = new TextField();
    numBinsField.setPromptText("Enter Number Of Bins");
    VBox binInput = new VBox(numBinsField);
    numBinsDialog.getDialogPane().setContent(binInput);

    ButtonType applyButton = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    numBinsDialog.getDialogPane().getButtonTypes().setAll(applyButton, cancelButton);

    numBinsDialog.setResultConverter(
        dialogButton -> {
          if (dialogButton == applyButton) {
            String input = numBinsField.getText();
            try {
              return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setContentText("Input is not an integer, wrong type");
              alert.showAndWait();
            }
          }
          return null;
        });

    Optional<Integer> numBinsRes = numBinsDialog.showAndWait();
    numBinsRes.ifPresent(this::addHistogram);
  }

  public void addChart(JFreeChart chart, String metricType) {
    ChartViewer chartViewer = new ChartViewer(chart);
    chartViewer.prefWidthProperty().bind(addChartFlowPane.widthProperty().divide(2).subtract(15));
    chartViewer.prefHeightProperty().bind(addChartFlowPane.widthProperty().divide(2).subtract(15));
    chartViewer.setUserData(metricType);

    chartViewer.addEventHandler(ScrollEvent.SCROLL, Event::consume);

    Button filterButton = new Button("Filter");
    filterButton.setOnAction(e -> showFilterSelection(chartViewer));

    Button exportPDFButton = new Button("Export as PDF");
    exportPDFButton.setOnAction(e -> {
        try{
          ExportService.chartToPDF(chartViewer.getChart());
        } catch (IOException ex){
          ex.printStackTrace();
        }
    });

    Button exportCSVButton = new Button("Export as CSV");
    exportCSVButton.setOnAction(e -> {
        try {
            ExportService.chartToCSV(chartViewer.getChart());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    });

    HBox buttonBox = new HBox(filterButton, exportPDFButton, exportCSVButton);
    buttonBox.setSpacing(10);
    VBox chartBox = new VBox(buttonBox, chartViewer);
    addChartFlowPane.getChildren().add(chartBox);
  }

  private void addHistogram(int numBins) {
    JFreeChart chart = new GenerateChartService().clickCostHistogram(numBins);
    addChart(chart, "Click Cost Histogram");

    XYPlot plot = (XYPlot) chart.getPlot();
    XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
    renderer.setBarPainter(new StandardXYBarPainter());
    renderer.setDrawBarOutline(true);
    renderer.setDefaultOutlinePaint(Color.BLACK);
    renderer.setSeriesPaint(0, new Color(173, 216, 230));
  }
}
