package com.university.grp20;

import javafx.application.Application;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App extends Application {

  private static final Logger logger = LogManager.getLogger(App.class);

  @Override
  public void start(Stage stage) {
    logger.info("Starting application");
    UIManager.setPrimaryStage(stage);
    stage.setTitle("Advertising Dashboard");
    //stage.setMaximized(true);
    //stage.setFullScreen(true);
    //stage.setFullScreenExitHint("");
    //stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    UIManager.switchScene(UIManager.createFXMLLoader("/fxml/LoginScene.fxml"));
  }

  public static void main(String[] args) {
    launch();
  }
}
