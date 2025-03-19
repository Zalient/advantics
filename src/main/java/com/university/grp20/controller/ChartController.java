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

public class ChartController {

  @FXML
  private FlowPane addChartFlowPane;
  @FXML
  private Button backButton;
  @FXML
  private MenuButton addChartButton;

  private final Logger logger = LogManager.getLogger(ChartController.class);
  private final OperationLogger operationLogger = new OperationLogger();

  @FXML
  private void initialize() {

    /**
     * Platform.runLater(() -> {
     *       if (User.getRole().equals("Viewer")) {
     *         backButton.setVisible(false);
     *       }
     *     });
     */


  }


  public void disableForViewer() {
    logger.info("disableForViewer called");

    addChartButton.setDisable(User.getRole().equals("Viewer"));

    addChartFlowPane.getChildren().forEach(node -> {
      if (node instanceof VBox vbox) {
        vbox.getChildren().forEach(child -> {
          if (child instanceof Button button && button.getText().equals("Filter")) {
            button.setDisable(User.getRole().equals("Viewer"));
          }
        });
      }
    });
  }

  @FXML
  private void showMetrics() {
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/MetricsScene.fxml"));
    operationLogger.log("Metrics page chosen, displaying metrics dashboard");
  }

  @FXML
  private void showFileSelection() {
    if (User.getRole().equals("Viewer")) {
      UIManager.switchScene(UIManager.createFXMLLoader("/fxml/LoginScene.fxml"), false);
       operationLogger.log("Back button clicked, returned to login page");
    } else {
      UIManager.switchScene(UIManager.createFXMLLoader("/fxml/FileSelectionScene.fxml"), false);
      operationLogger.log("Back button clicked, returned to file upload page");
    }
   
  }

  @FXML
  private void showFilterSelection(ChartViewer chartViewer) {
    if (!User.getRole().equals("Viewer")) {
      operationLogger.log("Charts filter chosen, displaying filter options");
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
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      alert.setHeaderText(null);
      alert.setContentText("Viewers cannot filter charts. Please contact an editor or administrator.");
      alert.showAndWait();

    }
  }

  @FXML
  private void addImpressionsChart() {
    JFreeChart chart = new GenerateChartService().impressionsChart();
    addChart(chart, "Impressions");
    operationLogger.log("Impressions chart chosen and displayed");
  }

  @FXML
  private void addClicksChart() {
    JFreeChart chart = new GenerateChartService().clicksChart();
    addChart(chart, "Clicks");
    operationLogger.log("Clicks chart chosen and displayed");
  }

  @FXML
  private void addUniquesChart() {
    JFreeChart chart = new GenerateChartService().uniquesChart();
    addChart(chart, "Uniques");
    operationLogger.log("Uniques chart chosen and displayed");
  }

  @FXML
  private void addBouncesChart() {
    JFreeChart chart = new GenerateChartService().bouncesChart();
    addChart(chart, "Bounces");
    operationLogger.log("Bounces chart chosen and displayed");
  }

  @FXML
  private void addConversionsChart() {
    JFreeChart chart = new GenerateChartService().conversionsChart();
    addChart(chart, "Conversions");
    operationLogger.log("Conversions chart chosen and displayed");

  }

  @FXML
  private void addTotalCostChart() {
    JFreeChart chart = new GenerateChartService().totalCostChart();
    addChart(chart, "Total Cost");
    operationLogger.log("Total Cost chart chosen and displayed");
  }

  @FXML
  private void addCTRChart() {
    JFreeChart chart = new GenerateChartService().ctrChart();
    addChart(chart, "CTR");
    operationLogger.log("CTR chart chosen and displayed");
  }

  @FXML
  private void addCPAChart() {
    JFreeChart chart = new GenerateChartService().cpaChart();
    addChart(chart, "CPA");
    operationLogger.log("CPA chart chosen and displayed");
  }

  @FXML
  private void addCPCChart() {
    JFreeChart chart = new GenerateChartService().cpcChart();
    addChart(chart, "CPC");
    operationLogger.log("CPC chart chosen and displayed");
  }

  @FXML
  private void addCPMChart() {
    JFreeChart chart = new GenerateChartService().cpmChart();
    addChart(chart, "CPM");
    operationLogger.log("CPM chart chosen and displayed");
  }

  @FXML
  private void addBounceRateChart() {
    JFreeChart chart = new GenerateChartService().bounceRateChart();
    addChart(chart, "Bounce Rate");
    operationLogger.log("Bounce Rate chart chosen and displayed");
  }

  @FXML
  private void binSizePrompt() {
    Dialog<Integer> numBinsDialog = new Dialog<>();
    numBinsDialog.setTitle("Set Number Of Bins");
    operationLogger.log("Click Cost histogram chosen, bin size prompt displayed");

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

    VBox chartBox;
    HBox buttonBox = new HBox();

    if (chart.getPlot() instanceof CategoryPlot) {
      CategoryPlot chartPlot = chart.getCategoryPlot();
      CategoryAxis xAxis = chartPlot.getDomainAxis();
      xAxis.setCategoryLabelPositions(
              CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4)
      );

      Button filterButton = new Button("Filter");
      filterButton.setOnAction(e -> showFilterSelection(chartViewer));
      buttonBox.getChildren().add(0, filterButton);
    }

    Button exportPDFButton = new Button("Export as PDF");
    exportPDFButton.setOnAction(e -> {
      try{
        String filePath = ExportService.askForPDFFilename();
        ExportService.chartToPDF(chartViewer.getChart(), filePath);
      } catch (IOException ex){
        ex.printStackTrace();
      }
    });

    Button exportCSVButton = new Button("Export as CSV");
    exportCSVButton.setOnAction(e -> {
      try {
        String filePath = ExportService.askForCSVFilename();
        ExportService.chartToCSV(chartViewer.getChart(), filePath);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });

    buttonBox.getChildren().addAll(exportPDFButton, exportCSVButton);
    buttonBox.setSpacing(10);
    chartBox = new VBox(buttonBox, chartViewer);


    addChartFlowPane.getChildren().add(chartBox);
  }

  private void addHistogram(int numBins) {
    JFreeChart chart = new GenerateChartService().clickCostHistogram(numBins);
    addChart(chart, "Click Cost Histogram");
    operationLogger.log("Bin size chosen and histogram displayed");

    XYPlot plot = (XYPlot) chart.getPlot();
    XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
    renderer.setBarPainter(new StandardXYBarPainter());
    renderer.setDrawBarOutline(true);
    renderer.setDefaultOutlinePaint(Color.BLACK);
    renderer.setSeriesPaint(0, new Color(173, 216, 230));
  }
}
