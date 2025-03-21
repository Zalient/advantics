package com.university.grp20;

import com.university.grp20.controller.ChartController;
import com.university.grp20.controller.MetricsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class UIManager {
  private static final Logger logger = LogManager.getLogger(UIManager.class);
  private static final String DEFAULT_MODAL_TITLE = "Filter Selection";
  private static final int CACHE_MAX_SIZE = 5;
  private static Stage primaryStage;
  private static Stage currentModal = null;
  private static final Map<String, Parent> ROOT_CACHE =
      new LinkedHashMap<>(CACHE_MAX_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Parent> eldest) {
          return size() > CACHE_MAX_SIZE;
        }
      };

  private static final Map<String, Object> CONTROLLER_CACHE =
      new LinkedHashMap<>(CACHE_MAX_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
          return size() > CACHE_MAX_SIZE;
        }
      };

  public static void setPrimaryStage(Stage stage) {
    primaryStage = stage;
  }

  public static FXMLLoader createFXMLLoader(String fxmlPath) {
    return new FXMLLoader(UIManager.class.getResource(fxmlPath));
  }

  public static void switchScene(FXMLLoader loader, boolean useCache) {
    if (primaryStage == null) {
      logger.error("Primary stage is not set");
      return;
    }
    try {
      Parent root = null;
      String key = null;
      Object controller;

      if (useCache && loader.getLocation() != null) {
        key = loader.getLocation().toString();
        root = ROOT_CACHE.get(key);
      }

      if (root == null) {
        root = (loader.getRoot() == null) ? loader.load() : loader.getRoot();
        if (useCache && key != null) {
          ROOT_CACHE.put(key, root);
          CONTROLLER_CACHE.put(key, loader.getController());
        }
      }

      Scene currentScene = primaryStage.getScene();

      if (currentScene == null) {
        currentScene = new Scene(root);
        primaryStage.setScene(currentScene);
      } else {
        currentScene.setRoot(root);
      }

      primaryStage.show();

      if (loader.getLocation().toString().contains("ChartsScene.fxml")) {
        logger.info("CHARTS SCENE IDENTIFIED");
        controller = CONTROLLER_CACHE.get(key);
        ((ChartController) controller).disableForViewer();
      } else if (loader.getLocation().toString().contains("MetricsScene.fxml")) {
        logger.info("METRICS SCENE IDENTIFIED");
        controller = CONTROLLER_CACHE.get(key);
        ((MetricsController) controller).disableForViewer();
      }

    } catch (IOException e) {
      String loaderLocation =
          (loader.getLocation() != null) ? loader.getLocation().toString() : "unknown";
      logger.error("Error switching scene using loader: " + loaderLocation, e);
    }
  }

  public static void switchScene(FXMLLoader loader) {
    switchScene(loader, true);
  }

  public static Stage showModal(FXMLLoader loader, String title, boolean useCache) {
    try {
      Parent root = null;
      String key = null;
      Object controller = null;
      if (useCache && loader.getLocation() != null) {
        key = loader.getLocation().toString();
        root = ROOT_CACHE.get(key);
        controller = CONTROLLER_CACHE.get(key);
      }
      if (root == null) {
        root = (loader.getRoot() == null) ? loader.load() : loader.getRoot();
        if (useCache && key != null) {
          ROOT_CACHE.put(key, root);
          CONTROLLER_CACHE.put(key, controller);
        }
      }
      Scene scene = new Scene(root);
      Stage modalStage = new Stage();
      modalStage.initModality(Modality.WINDOW_MODAL);
      modalStage.initOwner(primaryStage);
      modalStage.setScene(scene);
      modalStage.setTitle(title);
      currentModal = modalStage;
      modalStage.showAndWait();
      currentModal = null;
      return modalStage;
    } catch (IOException e) {
      String loaderLocation =
          (loader.getLocation() != null) ? loader.getLocation().toString() : "unknown";
      logger.error("Error showing modal scene using loader: " + loaderLocation, e);
      return null;
    }
  }

  public static Stage showModal(FXMLLoader loader, String title) {
    return showModal(loader, title, true);
  }

  public static Stage showModal(FXMLLoader loader, boolean useCache) {
    return showModal(loader, DEFAULT_MODAL_TITLE, useCache);
  }

  public static Stage showModal(FXMLLoader loader) {
    return showModal(loader, DEFAULT_MODAL_TITLE, true);
  }

  public static void closeModal() {
    if (currentModal != null) {
      currentModal.close();
      currentModal = null;
    }
  }
}
