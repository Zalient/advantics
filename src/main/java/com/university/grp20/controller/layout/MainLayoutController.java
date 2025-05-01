package com.university.grp20.controller.layout;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainLayoutController {
  @FXML private AnchorPane root;
  @FXML private HBox titleBar;
  @FXML private Button maximiseButton;
  @FXML private StackPane contentPane;
  @FXML private Label mainTitleLabel;
  private Stage mainStage;
  private boolean isMaximised = false;
  private double prevX, prevY, prevWidth, prevHeight;
  private double xOffset, yOffset;

  @FXML
  private void initialize() {
    addIconifiedListener();
    titleBar.setOnMousePressed(this::recordOffsets);
    titleBar.setOnMouseDragged(this::dragStage);
  }

  private void recordOffsets(MouseEvent event) {
    xOffset = mainStage.getX() - event.getScreenX();
    yOffset = mainStage.getY() - event.getScreenY();
  }

  private void dragStage(MouseEvent event) {
    if (!isMaximised) {
      mainStage.setX(event.getScreenX() + xOffset);
      mainStage.setY(event.getScreenY() + yOffset);
    }
  }

  @FXML
  private void minimiseStage() {
    double sceneHeight = root.getScene().getHeight();

    TranslateTransition slideDown = new TranslateTransition(Duration.millis(250), root);
    slideDown.setFromY(0);
    slideDown.setToY(sceneHeight);
    slideDown.setInterpolator(Interpolator.EASE_IN);

    ScaleTransition scaleDown = new ScaleTransition(Duration.millis(250), root);
    scaleDown.setFromX(1.0);
    scaleDown.setFromY(1.0);
    scaleDown.setToX(0.0);
    scaleDown.setToY(0.0);
    scaleDown.setInterpolator(Interpolator.EASE_IN);

    ParallelTransition minimiseAnimation = new ParallelTransition(slideDown, scaleDown);
    minimiseAnimation.setOnFinished(
        e -> {
          root.setTranslateY(0);
          root.setScaleX(1.0);
          root.setScaleY(1.0);
          mainStage.setIconified(true);
        });
    minimiseAnimation.play();
  }

  @FXML
  private void toggleMaximiseStage() {
    if (isMaximised) {
      mainStage.setX(prevX);
      mainStage.setY(prevY);
      mainStage.setWidth(prevWidth);
      mainStage.setHeight(prevHeight);
      isMaximised = false;
      maximiseButton.setText("⬜");
    } else {
      prevX = mainStage.getX();
      prevY = mainStage.getY();
      prevWidth = mainStage.getWidth();
      prevHeight = mainStage.getHeight();
      mainStage.setX(Screen.getPrimary().getVisualBounds().getMinX());
      mainStage.setY(Screen.getPrimary().getVisualBounds().getMinY());
      mainStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
      mainStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
      isMaximised = true;
      maximiseButton.setText("❐");
    }
  }

  @FXML
  private void closeStage() {
    mainStage.close();
  }

  public Pane getContentPane() {
    return contentPane;
  }

  public void setMainTitle(String title) {
    mainTitleLabel.setText(title);
  }

  public void setMainStage(Stage mainStage) {
    this.mainStage = mainStage;
  }

  private void addIconifiedListener() {
    javafx.application.Platform.runLater(
        () -> {
          Stage stage = (Stage) root.getScene().getWindow();
          stage
              .iconifiedProperty()
              .addListener(
                  (obs, notMinimised, isMinimised) -> {
                    if (!isMinimised) {
                      double sceneHeight = root.getScene().getHeight();
                      root.setTranslateY(sceneHeight);
                      root.setScaleX(0.0);
                      root.setScaleY(0.0);

                      TranslateTransition slideUp =
                          new TranslateTransition(Duration.millis(250), root);
                      slideUp.setFromY(sceneHeight);
                      slideUp.setToY(0);
                      slideUp.setInterpolator(Interpolator.EASE_OUT);

                      ScaleTransition scaleUp = new ScaleTransition(Duration.millis(250), root);
                      scaleUp.setFromX(0.0);
                      scaleUp.setFromY(0.0);
                      scaleUp.setToX(1.0);
                      scaleUp.setToY(1.0);
                      scaleUp.setInterpolator(Interpolator.EASE_OUT);

                      ParallelTransition restoreAnim = new ParallelTransition(slideUp, scaleUp);
                      restoreAnim.play();
                    }
                  });
        });
  }
}
