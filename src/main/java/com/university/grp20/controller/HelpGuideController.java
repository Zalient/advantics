package com.university.grp20.controller;

import com.university.grp20.model.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class HelpGuideController {
    @FXML private Button nextPicButton, prevPicButton;
    @FXML private Label guideTitleLabel;
    @FXML
    private ImageView imageCarousel;
    private static final Logger logger = LogManager.getLogger(LoginService.class);
    private int imageIndex = 0;
    private List<Image> imageList = new ArrayList<>();
    private int numImg;
    
    public void setupCarousel(String pageType){
        setPageTitle(pageType);
        selectImageList(pageType);
        imageCarousel.setPreserveRatio(true);
        imageCarousel.setSmooth(true);
        imageCarousel.setImage(imageList.get(imageIndex));
    }

    private void selectImageList(String pageType){
        switch (pageType) {
            case "Login" -> {
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/login-pane/1. Login Page.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/login-pane/2. Login Page Filled.png"))));
                numImg = imageList.size();
            }
            case "Upload" -> {
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/file-upload-pane/1. Upload Page.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/file-upload-pane/2. Windows File Explorer.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/file-upload-pane/3. Upload Page Filled.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/file-upload-pane/4. Upload Page Progress Bar.png"))));
                numImg = imageList.size();
            }
            case "Metrics" -> {
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/metrics-pane/1. Metrics Page.png"))));
                numImg = imageList.size();
            }
            case "Charts" -> {
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/charts-pane/1. Charts Page.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/charts-pane/2. Charts Page with 1 Chart.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/charts-pane/3. Print Chart.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/charts-pane/4. Histogram Bin Prompt.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/charts-pane/5. Histogram Displayed.png"))));
                numImg = imageList.size();
            }
            case "Settings" -> {
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/settings-pane/1. Theme Settings.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/settings-pane/2. Metric Settings.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/settings-pane/3. User Settings.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/settings-pane/4. Export Settings.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/settings-pane/Colourblind view.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/settings-pane/Purple view.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/settings-pane/Dark mode view.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/settings-pane/High contrast view.png"))));

                numImg = imageList.size();
            }
            case "Metric-Filter" -> {
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/metrics-filtering-pane/1. Metrics Filtering.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/metrics-filtering-pane/2. Date Range.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/metrics-filtering-pane/3. Gender.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/metrics-filtering-pane/4. Age Group.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/metrics-filtering-pane/5. Income.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/metrics-filtering-pane/6. Context.png"))));
                numImg = imageList.size();
            }
            case "Chart-Filter" -> {
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/1. Chart Filtering.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/2. Date Range.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/3. Gender.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/4. Age Group.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/5. Income.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/6. Context.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/7. Time Granularity.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/8. Per Day Of Week.png"))));
                imageList.add(new Image(String.valueOf(getClass().getResource("/images/guide-pics/chart-filtering-pane/9. Per Time Of Day.png"))));
                numImg = imageList.size();
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
    public void handleNext(){
        imageIndex = (imageIndex + 1) % numImg;
        imageCarousel.setImage(imageList.get(imageIndex));
        logger.info(imageIndex);
    }

    @FXML
    public void handlePrev(){
        imageIndex = (imageIndex - 1 + numImg) % numImg;
        imageCarousel.setImage(imageList.get(imageIndex));
        logger.info(imageIndex);
    }

    public void setImageCarousel(ImageView imageView) {
        this.imageCarousel = imageView;
    }

    public void setImageList(List<Image> images) {
        this.imageList = new ArrayList<>(images);
        this.numImg = images.size();
    }

    public int getImageIndex() {
        return imageIndex;
    }
}
