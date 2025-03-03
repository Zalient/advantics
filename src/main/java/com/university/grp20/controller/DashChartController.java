package com.university.grp20.controller;

import com.university.grp20.model.ChartGeneratorModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

import java.io.IOException;
import java.util.Objects;

public class DashChartController {
    @FXML
    public MenuItem impressionsChart, clicksChart, uniquesChart, bouncesChart, conversionsChart, totalCostChart, ctrChart, cpaChart, cpcChart, cpmChart, bounceRateChart, ccHistogram;

    @FXML
    private FlowPane chartDisplayFlowPane;
    private Stage stage;
    private Scene scene;
    private Parent root;

    ChartGeneratorModel generateChart = new ChartGeneratorModel();
    private int numOfCharts = 0;

    public void showDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/DashboardPage.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void showChartsPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/ChartPage.fxml")));
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

    public void displaySettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChartSettings.fxml"));
            Parent root = loader.load();

            Stage settingsStage = new Stage();
            settingsStage.setTitle("Chart Settings");
            settingsStage.setScene(new Scene(root));
            settingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayChart (JFreeChart chart){
        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chartViewer.prefWidthProperty().bind(chartDisplayFlowPane.widthProperty().divide(2).subtract(15));
        chartViewer.prefHeightProperty().bind(chartDisplayFlowPane.widthProperty().divide(2).subtract(15));
        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> displaySettings());
        VBox chartBox = new VBox();
        chartBox.getChildren().addAll(settingsButton, chartViewer);
        chartDisplayFlowPane.getChildren().add(chartBox);
        numOfCharts++;
    }

    public void displayImpressionsChart(){
        JFreeChart chart = generateChart.impressionsChart();
        displayChart(chart);
    }

    public void displayClicksChart(){
        JFreeChart chart = generateChart.clicksChart();
        displayChart(chart);
    }

    public void displayUniquesChart(){
        JFreeChart chart = generateChart.uniquesChart();
        displayChart(chart);
    }

    public void displayBouncesChart(){
        JFreeChart chart = generateChart.bouncesChart();
        displayChart(chart);
    }

    public void displayConversionsChart(){
        JFreeChart chart = generateChart.conversionsChart();
        displayChart(chart);
    }

    public void displayTotalCostChart(){
        JFreeChart chart = generateChart.totalCostChart();
        displayChart(chart);
    }

    public void displayCTRChart(){
        JFreeChart chart = generateChart.ctrChart();
        displayChart(chart);
    }

    public void displayHistogram(){}
}
