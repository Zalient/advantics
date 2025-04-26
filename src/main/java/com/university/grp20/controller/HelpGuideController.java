package com.university.grp20.controller;

import com.university.grp20.model.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class HelpGuideController {
    @FXML private Button nextPicButton, prevPicButton;
    @FXML private Label guideTitleLabel;
    @FXML private ImageView imageCarousel;
    private static final Logger logger = LogManager.getLogger(LoginService.class);
    private int imageIndex = 0;
    List<Image> imageList;
    
    private void setupCarousel(String pageType){
        setPageTitle(pageType);
        selectImageList(pageType);
        //display first image
    }

    private void selectImageList(String pageType){
        switch (pageType) {
            case "Login" -> {
            }
            case "Upload" -> {
            }
            case "Metrics" -> {
            }
            case "Charts" -> {
            }
            case "Settings" -> {
            }
            case "Metric-Filter" -> {
            }
            case "Chart-Filter" -> {
            }
            default -> logger.error("Invalid page type, unable to add to image list");
        }
    }

    private void setPageTitle(String pageType){
        switch (pageType) {
            case "Login" -> guideTitleLabel.setText("Login Help Guide");
            case "Upload" -> guideTitleLabel.setText("Upload Help Guide");
            case "Metrics" -> guideTitleLabel.setText("Metrics Help Guide");
            case "Charts" -> guideTitleLabel.setText("Charts Help Guide");
            case "Settings" -> guideTitleLabel.setText("Settings Help Guide");
            case "Metric-Filter" -> guideTitleLabel.setText("Metric Filtering Help Guide");
            case "Chart-Filter" -> guideTitleLabel.setText("Chart Filtering Help Guide");
            default -> logger.error("Invalid page type, unable to set page title");
        }
    }

    @FXML
    private void handleNext(){

    }

    @FXML
    private void handlePrev(){

    }
}
