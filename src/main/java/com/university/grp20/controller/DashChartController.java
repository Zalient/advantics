package com.university.grp20.controller;

import com.university.grp20.model.ChartGeneratorModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.embed.swing.SwingNode;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

public class DashChartController {
    @FXML
    public MenuItem impressionsChart, clicksChart, uniquesChart, bouncesChart, conversionsChart, totalCostChart, ctrChart, cpaChart, cpcChart, cpmChart, bounceRateChart;

    @FXML
    private GridPane chartDisplayGrid;

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

    public void displayImpressionsChart(){
        JFreeChart chart = generateChart.impressionsChart();
        ChartViewer chartViewer = new ChartViewer(chart);
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(chartViewer, col, row);
        numOfCharts++;
    }

    public void displayClicksChart(){
        JFreeChart chart = generateChart.clicksChart();
        ChartViewer chartViewer = new ChartViewer(chart);
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(chartViewer, col, row);
        numOfCharts++;
    }

    public void displayUniquesChart(){
        JFreeChart chart = generateChart.uniquesChart();
        ChartViewer chartViewer = new ChartViewer(chart);
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(chartViewer, col, row);
        numOfCharts++;
    }

    public void displayBouncesChart(){
        JFreeChart chart = generateChart.bouncesChart();
        ChartViewer chartViewer = new ChartViewer(chart);
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(chartViewer, col, row);
        numOfCharts++;
    }

    public void displayConversionsChart(){
        JFreeChart chart = generateChart.conversionsChart();
        ChartViewer chartViewer = new ChartViewer(chart);
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(chartViewer, col, row);
        numOfCharts++;
    }

    public void displayTotalCostChart(){
        JFreeChart chart = generateChart.totalCostChart();
        ChartViewer chartViewer = new ChartViewer(chart);
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(chartViewer, col, row);
        numOfCharts++;
    }

    public void displayCTRChart(){
        JFreeChart chart = generateChart.ctrChart();
        ChartViewer chartViewer = new ChartViewer(chart);
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(chartViewer, col, row);
        numOfCharts++;
    }
}
