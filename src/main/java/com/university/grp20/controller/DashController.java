package com.university.grp20.controller;

import com.university.grp20.model.DashboardModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class DashController {
    @FXML
    public ToggleButton metricsButton, chartsButton;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public Label impressionsLabel, clicksLabel, uniquesLabel, bouncesLabel, conversionsLabel, totalLabel, ctrLabel, cpaLabel, cpcLabel, cpmLabel, bounceRateLabel;

    private final DashboardModel dashModel = new DashboardModel();

    public void displayUpload(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/FileUpload.fxml")));
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

    public void displayMetrics(){
        double impressions = dashModel.rawMetricGetter("SELECT COUNT(*) FROM impressionLog");
        impressionsLabel.setText(String.format( "%.2f", impressions) + " impressions");
        double clicks = dashModel.rawMetricGetter("SELECT COUNT(*) FROM clickLog");
        clicksLabel.setText(String.format( "%.2f", clicks) + " clicks");
        double uniques = dashModel.rawMetricGetter("SELECT COUNT(DISTINCT ID) FROM impressionLog");
        uniquesLabel.setText(String.format( "%.2f", uniques) + " unique IDs");
        double bounces = dashModel.rawMetricGetter("SELECT COUNT(*) FROM serverLog WHERE \"PagesViewed\" = 1 ");
        bouncesLabel.setText(String.format( "%.2f", bounces) + " bounces");
        double conversions = dashModel.rawMetricGetter("SELECT COUNT(*) FROM serverLog WHERE \"Conversion\" = 'Yes' ");
        conversionsLabel.setText(String.format( "%.2f", conversions) + " conversions");
        double total = dashModel.rawMetricGetter("SELECT (SELECT SUM(ImpressionCost) FROM impressionLog) + (SELECT SUM(ClickCost) FROM clickLog)");
        totalLabel.setText(String.format( "%.2f", total) + " pence");
        ctrLabel.setText((clicks * 100.0) / impressions + "%");
        cpaLabel.setText(String.format( "%.2f", total/conversions) + " pence per conversion");
        cpcLabel.setText(String.format( "%.2f", total/clicks) + " pence per click");
        cpmLabel.setText(String.format( "%.2f", (total/impressions) * 1000) + " pence per 1000 impressions");
        bounceRateLabel.setText(String.format( "%.2f", bounces/clicks) + " bounce per click");
    }

    @FXML
    public void initialize(){
        displayMetrics();
    }

}
