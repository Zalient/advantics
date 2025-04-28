package com.university.grp20.controller;

import com.university.grp20.UIManager;
import com.university.grp20.model.ExportService;
import com.university.grp20.model.GenerateChartService;
import com.university.grp20.model.OperationLogger;
import com.university.grp20.model.User;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class ChartsController extends Navigator {
  @FXML private FlowPane addChartFlowPane;
  @FXML private MenuButton addChartButton;
  private final Logger logger = LogManager.getLogger(ChartsController.class);
  private final OperationLogger operationLogger = new OperationLogger();
  @FXML private Button helpButton;


  @FXML
  private void initialize() {}

  @FXML
  MenuItem addImpressionsChartButton,addClicksChartButton, addUniquesChartButton,addBouncesChartButton, addConversionsChartButton, addTotalCostChartButton, addCTRChartButton, addCPAChartButton, addCPCChartButton, addCPMChartButton, addBounceRateChartButton, addClickCostHistogramButton;
  public void disableForViewer() {
    logger.info("disableForViewer called");

    boolean status = User.getRole().equals("Viewer");

    addChartButton.setDisable(status);

    addChartFlowPane
        .getChildren()
        .forEach(
            node -> {
              if (node instanceof VBox vbox) {
                vbox.getChildren()
                    .forEach(
                        vBoxElement -> {
                          if (vBoxElement instanceof HBox foundHBox) {
                            foundHBox
                                .getChildren()
                                .forEach(
                                    foundButton -> {
                                      if (foundButton instanceof Button currentButton) {
                                        String buttonText = currentButton.getText();
                                        if (buttonText.equals("Filter")
                                            || buttonText.equals("Export as PDF")
                                            || buttonText.equals("Export as CSV")) {
                                          currentButton.setDisable(status);
                                        }
                                      }
                                    });
                          }
                        });
              }
            });
  }

  @FXML
  private void exportAllChartsAsPDF() {
    operationLogger.log("exportAllChartsAsPDF button clicked");
    try {
      String filePath = ExportService.askForPDFFilename();
      if (filePath != null) {
        operationLogger.log("export all charts as pdf, filepath: " + filePath);
      } else {
        operationLogger.log("Chart export canceled");
        return;
      }
      ExportService.exportAllChartAsPDF(addChartFlowPane, filePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @FXML
  private void showMetrics() {
    UIManager.switchContent(parentPane, UIManager.createFxmlLoader("/fxml/MetricsPane.fxml"), true);
    operationLogger.log("Metrics button clicked, displaying metrics dashboard");
  }

  @FXML
  private void showFileSelection() {
    if (User.getRole().equals("Viewer")) {
      UIManager.switchContent(parentPane, UIManager.createFxmlLoader("/fxml/LoginPane.fxml"));
      operationLogger.log("Back button clicked, returned to login page");
    } else {
      UIManager.switchContent(
          parentPane, UIManager.createFxmlLoader("/fxml/FileSelectionPane.fxml"));
      operationLogger.log("Back button clicked, returned to file upload page");
    }
  }

  @FXML
  private void showFilterSelection(ChartViewer chartViewer) {
    if (!User.getRole().equals("Viewer")) {
      operationLogger.log("Charts filter button clicked, displaying filter options");
      try {
        FXMLLoader filterLoader = UIManager.createFxmlLoader("/fxml/FilterSelectionPopup.fxml");
        filterLoader.load();

        FilterSelectionController filterController = filterLoader.getController();
        filterController.init("Chart", null, chartViewer);

        UIManager.showModalStage("Filter Selection", filterLoader);
      } catch (IOException e) {
        logger.error("Error loading chart filter: " + e.getMessage());
      }
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText(
          "Viewers cannot filter charts. Please contact an editor or administrator.");
      alert.showAndWait();
    }
  }

  @FXML
  private void addImpressionsChart() {
    JFreeChart chart = GenerateChartService.impressionsChart();
    addChart(chart, "Impressions");
  }

  @FXML
  private void addClicksChart() {
    JFreeChart chart = GenerateChartService.clicksChart();
    addChart(chart, "Clicks");
  }

  @FXML
  private void addUniquesChart() {
    JFreeChart chart = GenerateChartService.uniquesChart();
    addChart(chart, "Uniques");
  }

  @FXML
  private void addBouncesChart() {
    JFreeChart chart = GenerateChartService.bouncesChart();
    addChart(chart, "Bounces");
  }

  @FXML
  private void addConversionsChart() {
    JFreeChart chart = GenerateChartService.conversionsChart();
    addChart(chart, "Conversions");
  }

  @FXML
  private void addTotalCostChart() {
    JFreeChart chart = GenerateChartService.totalCostChart();
    addChart(chart, "Total Cost");
  }

  @FXML
  private void addCTRChart() {
    JFreeChart chart = GenerateChartService.ctrChart();
    addChart(chart, "CTR");
  }

  @FXML
  private void addCPAChart() {
    JFreeChart chart = GenerateChartService.cpaChart();
    addChart(chart, "CPA");
  }

  @FXML
  private void addCPCChart() {
    JFreeChart chart = GenerateChartService.cpcChart();
    addChart(chart, "CPC");
  }

  @FXML
  private void addCPMChart() {
    JFreeChart chart = GenerateChartService.cpmChart();
    addChart(chart, "CPM");
  }

  @FXML
  private void addBounceRateChart() {
    JFreeChart chart = GenerateChartService.bounceRateChart();
    addChart(chart, "Bounce Rate");
  }

  @FXML
  private void binSizePrompt() {
    operationLogger.log("Click Cost histogram chosen, bin size prompt displayed");
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
    operationLogger.log(metricType + " chart display option clicked and displayed");
    ChartViewer chartViewer = new ChartViewer(chart);
    chartViewer.prefWidthProperty().bind(addChartFlowPane.widthProperty().divide(2).subtract(15));
    chartViewer.prefHeightProperty().bind(addChartFlowPane.widthProperty().divide(2).subtract(15));
    chartViewer.setUserData(metricType);
    chartViewer.addEventHandler(ScrollEvent.SCROLL, Event::consume);

    VBox chartBox = new VBox();
    HBox buttonBox = new HBox();

    if (chart.getPlot() instanceof CategoryPlot) {
      CategoryPlot chartPlot = chart.getCategoryPlot();
      CategoryAxis xAxis = chartPlot.getDomainAxis();
      xAxis.setCategoryLabelPositions(
          CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));

      Button filterButton = new Button("Filter");
      filterButton.getStyleClass().add("chart-button");
      filterButton.setPrefSize(70, 25);
      filterButton.setOnAction(e -> showFilterSelection(chartViewer));
      buttonBox.getChildren().add(0, filterButton);
    }

    Button exportPDFButton = new Button("Export as PDF");
    exportPDFButton.getStyleClass().add("export-button");
    exportPDFButton.setPrefSize(110, 25);
    exportPDFButton.setOnAction(
        e -> {
          operationLogger.log(metricType + " chart export as PDF button clicked");
          try {
            String filePath = ExportService.askForPDFFilename();
            ExportService.chartToPDF(chartViewer.getChart(), filePath);
            operationLogger.log( metricType + " chart successfully exported as PDF to " + filePath);
            UIManager.showAlert("Success", metricType + " chart successfully exported as PDF to " + filePath);
          } catch (IOException ex) {
            logger.error("Error exporting as PDF: " + ex);
            operationLogger.log("Error exporting " + metricType + " chart as PDF " + ex);
            UIManager.showAlert("Error", "Error exporting as PDF " + ex);
          }
        });

    Button exportCSVButton = new Button("Export as CSV");
    exportCSVButton.getStyleClass().add("export-button");
    exportCSVButton.setPrefSize(110, 25);
    exportCSVButton.setOnAction(
        e -> {
          operationLogger.log(metricType + " chart export as CSV button clicked");
          try {
            String filePath = ExportService.askForCSVFilename();
            ExportService.chartToCSV(chartViewer.getChart(), filePath);
            operationLogger.log( metricType + " chart successfully exported as CSV to " + filePath);
            UIManager.showAlert("Success", metricType + " chart successfully exported as CSV to " + filePath);
          } catch (IOException ex) {
            logger.error("Error exporting as CSV " + ex);
            operationLogger.log("Error exporting " + metricType + " chart as CSV " + ex);
            UIManager.showAlert("Error", "Error exporting as CSV " + ex);
          }
        });

    Button deleteButton = new Button("Delete");
    deleteButton.getStyleClass().add("chart-button");
    deleteButton.setPrefSize(70, 25);
    deleteButton.setOnAction(e -> {
      operationLogger.log("Delete button clicked");
      addChartFlowPane.getChildren().remove(chartBox);
    });

    buttonBox.getChildren().addAll(exportPDFButton, exportCSVButton, deleteButton);

    buttonBox.setSpacing(10);
    chartBox.getChildren().addAll(buttonBox, chartViewer);

    addChartFlowPane.getChildren().add(chartBox);
  }

  private void addHistogram(int numBins) {
    JFreeChart chart = GenerateChartService.clickCostHistogram(numBins);
    addChart(chart, "Click Cost Histogram");
    operationLogger.log("Bin size of "+ numBins +" chosen and histogram displayed");

    XYPlot plot = (XYPlot) chart.getPlot();
    XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
    renderer.setBarPainter(new StandardXYBarPainter());
    renderer.setDrawBarOutline(true);
    renderer.setDefaultOutlinePaint(Color.BLACK);
    renderer.setSeriesPaint(0, new Color(173, 216, 230));
  }

  @FXML
  private void showHelpGuide() {
    FXMLLoader loader = UIManager.createFxmlLoader("/fxml/HelpGuidePane.fxml");
    try {
      loader.load();
      HelpGuideController helpController = loader.getController();
      helpController.setupCarousel("Charts");
      UIManager.showModalStage("Charts Page Help Guide", loader, false);
      operationLogger.log("Charts Page Help Guide Icon clicked");
    } catch (IOException e) {
      logger.error("Failed to open Help Guide", e);
    }
  }
}
