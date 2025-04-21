package com.university.grp20;

import com.university.grp20.controller.Navigator;
import com.university.grp20.controller.layout.MainLayoutController;
import com.university.grp20.controller.layout.ModalLayoutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class UIManager {
  private static Object controller;
  private static final Logger logger = LogManager.getLogger(UIManager.class);
  private static Stage mainStage;
  private static final int CACHE_MAX_SIZE = 5;
  private static final Map<String, Parent> ROOT_CACHE = new LinkedHashMap<>(CACHE_MAX_SIZE, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Parent> eldest) {
      return size() > CACHE_MAX_SIZE;
    }
  };

  public static void setMainStage(Stage stage) {
    mainStage = stage;
  }

  public static FXMLLoader createFxmlLoader(String fxmlPath) {
    return new FXMLLoader(UIManager.class.getResource(fxmlPath));
  }

  private static Parent resolveFxmlRoot(FXMLLoader childLoader, boolean useCache) {
    try {
      String key = (useCache && childLoader.getLocation() != null)
              ? childLoader.getLocation().toString()
              : null;
      if (key != null && ROOT_CACHE.containsKey(key)) {
        return ROOT_CACHE.get(key);
      }
      // If the loader has already loaded its content, reuse it
      Parent root = (childLoader.getRoot() == null) ? childLoader.load() : childLoader.getRoot();
      if (key != null) {
        ROOT_CACHE.put(key, root);
      }
      return root;
    } catch (IOException e) {
      logger.error("Error resolving FXML root: " + childLoader.getLocation(), e);
      throw new RuntimeException();
    }
  }
  private static Parent resolveFxmlRoot(FXMLLoader childLoader) {
    return resolveFxmlRoot(childLoader, false);
  }

  public static void switchContent(Pane contentPane, FXMLLoader childLoader, boolean useCache) {
    Parent childRoot = resolveFxmlRoot(childLoader, useCache);
    contentPane.getChildren().clear();
    contentPane.getChildren().setAll(childRoot);

    // If the controller is a Navigator it needs to know its parent container
    Object controller = childLoader.getController();
    if (controller instanceof Navigator) {
      ((Navigator) controller).init(contentPane);
    }
  }

  public static void switchContent(Pane contentPane, FXMLLoader childLoader) {
    switchContent(contentPane, childLoader, false);
  }

  public static void showMainStage(String title, FXMLLoader childLoader, boolean useCache) {
    FXMLLoader mainLayoutLoader = UIManager.createFxmlLoader("/fxml/layout/MainLayout.fxml");
    Parent mainLayoutRoot = resolveFxmlRoot(mainLayoutLoader);

    MainLayoutController mainLayoutController = mainLayoutLoader.getController();
    Pane mainLayoutContentPane = mainLayoutController.getContentPane();

    switchContent(mainLayoutContentPane, childLoader, useCache);

    mainStage.initStyle(StageStyle.TRANSPARENT);
    mainStage.setTitle(title);
    mainStage.setScene(new Scene(mainLayoutRoot));
    mainStage.getScene().setFill(Color.TRANSPARENT);
    mainLayoutController.setMainTitle(title);
    mainLayoutController.setMainStage(mainStage);
    mainStage.show();
  }

  public static void showMainStage(String title, FXMLLoader childLoader) {
    showMainStage(title, childLoader, false);
  }

  public static void showModalStage(String title, FXMLLoader childLoader, boolean useCache) {
    FXMLLoader modalLayoutLoader = UIManager.createFxmlLoader("/fxml/layout/ModalLayout.fxml");
    Parent modalLayoutRoot = resolveFxmlRoot(modalLayoutLoader);

    ModalLayoutController modalLayoutController = modalLayoutLoader.getController();
    Pane modalLayoutContentPane = modalLayoutController.getContentPane();

    switchContent(modalLayoutContentPane, childLoader, useCache);

    Stage modalStage = new Stage();
    modalStage.initStyle(StageStyle.TRANSPARENT);
    modalStage.initModality(Modality.WINDOW_MODAL);
    modalStage.initOwner(mainStage);
    modalStage.setTitle(title);
    modalStage.setScene(new Scene(modalLayoutRoot));
    modalStage.getScene().setFill(Color.TRANSPARENT);
    modalLayoutController.setModalTitle(title);
    modalLayoutController.setModalStage(modalStage);
    modalStage.showAndWait();
  }

  public static void showModalStage(String title, FXMLLoader childLoader) {
    showModalStage(title, childLoader, false);
  }

  public static void showError(String errorMessage) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error!");
    alert.setHeaderText(null);
    alert.setContentText(errorMessage);
    alert.showAndWait();
  }

  public static void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void setController(Object newController) {
    controller = newController;
  }


  public static Object getController() {
    return controller;
  }
}


