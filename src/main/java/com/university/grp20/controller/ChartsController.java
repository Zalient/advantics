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
import javafx.scene.control.Label;
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
  private void showFilterSelection(ChartViewer chartViewer, String campaignName) {
    if (!User.getRole().equals("Viewer")) {
      operationLogger.log("Charts filter button clicked, displaying filter options");
      try {
        FXMLLoader filterLoader = UIManager.createFxmlLoader("/fxml/FilterSelectionModal.fxml");
        filterLoader.load();

        FilterSelectionController filterController = filterLoader.getController();
        filterController.init("Chart", null, chartViewer, campaignName);

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
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.impressionsChart();
    addChart(chart, "Impressions");
  }

  @FXML
  private void addClicksChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.clicksChart();
    addChart(chart, "Clicks");
  }

  @FXML
  private void addUniquesChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.uniquesChart();
    addChart(chart, "Uniques");
  }

  @FXML
  private void addBouncesChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.bouncesChart();
    addChart(chart, "Bounces");
  }

  @FXML
  private void addConversionsChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.conversionsChart();
    addChart(chart, "Conversions");
    operationLogger.log("Conversions chart chosen and displayed");
  }

  @FXML
  private void addTotalCostChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.totalCostChart();
    addChart(chart, "Total Cost");
  }

  @FXML
  private void addCTRChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.ctrChart();
    addChart(chart, "CTR");
  }

  @FXML
  private void addCPAChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.cpaChart();
    addChart(chart, "CPA");
  }

  @FXML
  private void addCPCChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.cpcChart();
    addChart(chart, "CPC");
  }

  @FXML
  private void addCPMChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.cpmChart();
    addChart(chart, "CPM");
  }

  @FXML
  private void addBounceRateChart() {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
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
    System.out.println("DEBUG: addChart, user campaign is " + User.getSelectedCampaign());
    operationLogger.log(metricType + " chart display option clicked and displayed");
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
          CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));

      Button filterButton = new Button("Filter");
      String campaignToUse = User.getSelectedCampaign();
      filterButton.setOnAction(e -> showFilterSelection(chartViewer, campaignToUse));
      buttonBox.getChildren().add(0, filterButton);
    }

    Button exportPDFButton = new Button("Export as PDF");
    exportPDFButton.setOnAction(
        e -> {
          try {
            String filePath = ExportService.askForPDFFilename();
            ExportService.chartToPDF(chartViewer.getChart(), filePath);
          } catch (IOException ex) {
            logger.error("Error exporting as PDF: " + ex);
          }
        });

    Button exportCSVButton = new Button("Export as CSV");
    exportCSVButton.setOnAction(
        e -> {
          try {
            String filePath = ExportService.askForCSVFilename();
            ExportService.chartToCSV(chartViewer.getChart(), filePath);
          } catch (IOException ex) {
            logger.error("Error exporting as CSV " + ex);
          }
        });

    buttonBox.getChildren().addAll(exportPDFButton, exportCSVButton);

    Label campaignLabel = new Label("Campaign: " + User.getSelectedCampaign());
    buttonBox.getChildren().add(campaignLabel);

    buttonBox.setSpacing(10);
    chartBox = new VBox(buttonBox, chartViewer);

    addChartFlowPane.getChildren().add(chartBox);
  }

  private void addHistogram(int numBins) {
    GenerateChartService.setCampaignName(User.getSelectedCampaign());
    JFreeChart chart = GenerateChartService.clickCostHistogram(numBins);
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
