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
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            JFreeChart chart = generateChart.impressionsChart();
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            swingNode.setContent(chartPanel);
        });
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(swingNode, col, row);
        numOfCharts++;
    }

    public void displayClicksChart(){
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            JFreeChart chart = generateChart.clicksChart();
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            swingNode.setContent(chartPanel);
        });
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(swingNode, col, row);
        numOfCharts++;
    }

    public void displayUniquesChart(){
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            JFreeChart chart = generateChart.uniquesChart();
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            swingNode.setContent(chartPanel);
        });
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(swingNode, col, row);
        numOfCharts++;
    }

    public void displayBouncesChart(){
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            JFreeChart chart = generateChart.bouncesChart();
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            swingNode.setContent(chartPanel);
        });
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(swingNode, col, row);
        numOfCharts++;
    }

    public void displayConversionsChart(){
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            JFreeChart chart = generateChart.conversionsChart();
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            swingNode.setContent(chartPanel);
        });
        int col = numOfCharts % 2;
        int row = numOfCharts / 2;
        chartDisplayGrid.add(swingNode, col, row);
        numOfCharts++;
    }
}
