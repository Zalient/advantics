package com.university.grp20.controller;

import com.university.grp20.model.ChartGeneratorModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ChartController {
    @FXML
    public MenuItem impressionsChart, clicksChart, uniquesChart, bouncesChart, conversionsChart, totalCostChart, ctrChart, cpaChart, cpcChart, cpmChart, bounceRateChart, ccHistogram;

    @FXML
    private FlowPane chartDisplayFlowPane;
    private Stage stage;
    private Scene scene;
    private Parent root;

    ChartGeneratorModel generateChart = new ChartGeneratorModel();

    public void showDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/DashboardPage.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void displayUpload(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/FileUpload.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void displaySettings(ChartViewer chartViewer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChartSettings.fxml"));
            Parent root = loader.load();

            ChartSettingsController controller = loader.getController();
            controller.initChartViewer(chartViewer, this);

            Stage settingsStage = new Stage();
            settingsStage.setTitle("Chart Settings");
            settingsStage.setScene(new Scene(root));
            settingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayChart (JFreeChart chart, String metricType){
        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chartViewer.prefWidthProperty().bind(chartDisplayFlowPane.widthProperty().divide(2).subtract(15));
        chartViewer.prefHeightProperty().bind(chartDisplayFlowPane.widthProperty().divide(2).subtract(15));

        chartViewer.setUserData(metricType);

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> displaySettings(chartViewer));

        VBox chartBox = new VBox();
        chartBox.getChildren().addAll(settingsButton, chartViewer);
        chartDisplayFlowPane.getChildren().add(chartBox);
    }

    public void displayImpressionsChart(){
        JFreeChart chart = generateChart.impressionsChart();
        displayChart(chart, "Impressions");
    }

    public void displayClicksChart(){
        JFreeChart chart = generateChart.clicksChart();
        displayChart(chart, "Clicks");
    }

    public void displayUniquesChart(){
        JFreeChart chart = generateChart.uniquesChart();
        displayChart(chart, "Uniques");
    }

    public void displayBouncesChart(){
        JFreeChart chart = generateChart.bouncesChart();
        displayChart(chart, "Bounces");
    }

    public void displayConversionsChart(){
        JFreeChart chart = generateChart.conversionsChart();
        displayChart(chart, "Conversions");
    }

    public void displayTotalCostChart(){
        JFreeChart chart = generateChart.totalCostChart();
        displayChart(chart, "Total Cost");
    }

    public void displayCTRChart(){
        JFreeChart chart = generateChart.ctrChart();
        displayChart(chart, "CTR");
    }

    public void displayCPAChart(){
        JFreeChart chart = generateChart.cpaChart();
        displayChart(chart, "CPA");
    }

    public void displayCPCChart(){
        JFreeChart chart = generateChart.cpcChart();
        displayChart(chart, "CPC");
    }

    public void displayCPMChart(){
        JFreeChart chart = generateChart.cpmChart();
        displayChart(chart, "CPM");
    }

    public void displayBounceRateChart(){
        JFreeChart chart = generateChart.bounceRateChart();
        displayChart(chart, "Bounce Rate");
    }

    public void binSizePrompt(){
        Dialog<Integer> numBinsDialog = new Dialog<>();
        numBinsDialog.setTitle("Set Num Of Bins");
        TextField numBinsField = new TextField();
        numBinsField.setPromptText("Enter Number Of Bins");
        VBox binInput = new VBox();
        binInput.getChildren().add(numBinsField);
        numBinsDialog.getDialogPane().setContent(binInput);
        ButtonType applyButton = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        numBinsDialog.getDialogPane().getButtonTypes().setAll(applyButton, cancelButton);

        numBinsDialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButton) {
                String input = numBinsField.getText();
                try{
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
        numBinsRes.ifPresent(numBins -> displayHistogram(numBins));
    }

    public void displayHistogram(int numBins){
        JFreeChart chart = generateChart.clickCostHistogram(numBins);
        displayChart(chart, "Click Cost Histogram");

        XYPlot plot = (XYPlot) chart.getPlot();
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setDrawBarOutline(true);
        renderer.setDefaultOutlinePaint(Color.BLACK);
        renderer.setSeriesPaint(0, new Color(173, 216, 230));
    }
}