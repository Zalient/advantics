package com.university.grp20.controller.layout;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ModalLayoutController {
  @FXML private HBox titleBar;
  @FXML private StackPane contentPane;
  @FXML private Label modalTitleLabel;
  private Stage modalStage;
  private double xOffset, yOffset;

  @FXML
  private void initialize() {
    titleBar.setOnMousePressed(this::recordOffsets);
    titleBar.setOnMouseDragged(this::dragStage);
  }
  private void recordOffsets(MouseEvent event) {
    xOffset = modalStage.getX() - event.getScreenX();
    yOffset = modalStage.getY() - event.getScreenY();
  }

  private void dragStage(MouseEvent event) {
    modalStage.setX(event.getScreenX() + xOffset);
    modalStage.setY(event.getScreenY() + yOffset);
  }

  @FXML
  private void closeStage() {
    modalStage.close();
  }

  public Pane getContentPane() {
    return contentPane;
  }

  public void setModalTitle(String title) {
    modalTitleLabel.setText(title);
  }

  public void setModalStage(Stage modalStage) {
    this.modalStage = modalStage;
  }
}
