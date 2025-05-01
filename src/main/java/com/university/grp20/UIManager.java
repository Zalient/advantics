package com.university.grp20;

import com.university.grp20.controller.ChartsController;
import com.university.grp20.controller.MetricsController;
import com.university.grp20.controller.Navigator;
import com.university.grp20.controller.layout.MainLayoutController;
import com.university.grp20.controller.layout.PopupLayoutController;
import com.university.grp20.model.User;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class UIManager {
  private static Object controller;
  private static final Logger logger = LogManager.getLogger(UIManager.class);
  private static Stage mainStage;
  private static final int CACHE_MAX_SIZE = 100;
  private static String currentTheme = "/styles/default-blue.css";
  private static String currentFont = "System";

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
      /**String key = (useCache && childLoader.getLocation() != null)
              ? childLoader.getLocation().toString()
              : null;*/
      String key = null;
      if (childLoader.getLocation()!=null) {
        String pageName = childLoader.getLocation().toString();

        // If page being loaded is a metrics page make a different one per campaign
        if (pageName.endsWith("/MetricsPane.fxml")) {
          String campaign = User.getSelectedCampaign();
          key = (campaign != null) ? pageName + "#" + campaign : pageName;
        } else {
           key = (useCache && childLoader.getLocation() != null)
                  ? childLoader.getLocation().toString()
                  : null;
        }
      }
      if (key != null && ROOT_CACHE.containsKey(key)) {
        return ROOT_CACHE.get(key);
      }
      // If the loader has already loaded its content, reuse it
      Parent root = (childLoader.getRoot() == null) ? childLoader.load() : childLoader.getRoot();
      Object controller = childLoader.getController();
      root.getProperties().put("controller", controller);
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
    //Object controller = childLoader.getController();
    Object controller = childRoot.getProperties().get("controller");

    if (controller == null) {
      controller = childLoader.getController();
    }
    if (controller instanceof Navigator) {
      ((Navigator) controller).init(contentPane);
    }
    if (controller instanceof MetricsController) {
      System.out.println("Controller is instance of metrics controller");
      ((MetricsController) controller).disableForViewer();
    }
    if (controller instanceof ChartsController) {
      System.out.println("Controller is instance of charts controller");
      ((ChartsController) controller).disableForViewer();
    }

    /**
    Object myController = childRoot.getProperties().get("controller");
    if (myController instanceof MetricsController) {
      System.out.println("UI MANAGER: Calling disableForViewer on metrics controller");
      ((MetricsController) myController).disableForViewer();
    }
    if (myController instanceof ChartsController) {
      ((ChartsController) myController).disableForViewer();
    }
     */
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

    mainStage.setTitle(title);

    Scene scene = new Scene(mainLayoutRoot);
    scene.setFill(Color.TRANSPARENT);

    scene.getRoot().getStylesheets().clear();
    applyCurrentTheme(scene);
    applyCurrentFont(scene);

    mainStage.setScene(scene);

    mainLayoutController.setMainTitle(title);
    mainLayoutController.setMainStage(mainStage);

    mainStage.show();
  }

  public static void showMainStage(String title, FXMLLoader childLoader) {
    showMainStage(title, childLoader, false);
  }

  public static void showPopupStage(String title, FXMLLoader childLoader, boolean useCache) {
    FXMLLoader popupLayoutLoader = UIManager.createFxmlLoader("/fxml/layout/PopupLayout.fxml");
    Parent popupLayoutRoot = resolveFxmlRoot(popupLayoutLoader);

    PopupLayoutController popupLayoutController = popupLayoutLoader.getController();
    Pane popupLayoutContentPane = popupLayoutController.getContentPane();

    switchContent(popupLayoutContentPane, childLoader, useCache);

    Stage popupStage = new Stage();
    popupStage.initStyle(StageStyle.TRANSPARENT);
    popupStage.initModality(Modality.WINDOW_MODAL);
    popupStage.initOwner(mainStage);
    popupStage.setTitle(title);

    Scene scene = new Scene(popupLayoutRoot);
    scene.setFill(Color.TRANSPARENT);

    popupStage.setScene(scene);

    applyCurrentTheme(scene);
    applyCurrentFont(scene);
    
    popupLayoutController.setPopupTitle(title);
    popupLayoutController.setPopupStage(popupStage);

    popupStage.showAndWait();
  }

  public static void showPopupStage(String title, FXMLLoader childLoader) {
    showPopupStage(title, childLoader, false);
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

  public static void applyCurrentTheme(Scene scene) {
    scene.getStylesheets().clear();
    scene.getStylesheets().add(
            UIManager.class.getResource(currentTheme).toExternalForm()
    );
    scene.getRoot().applyCss();
    scene.getRoot().layout();
  }

  public static void applyCurrentFont(Scene scene) {
    scene.getStylesheets().removeIf(sheet -> sheet.contains("dynamic-font"));
    String fontCss = "* {\n" +
            "    -fx-font-family: '" + currentFont + "';\n" +
            "}";
    try {
      Path tempFontCss = Files.createTempFile("dynamic-font", ".css");
      Files.writeString(tempFontCss, fontCss);
      scene.getStylesheets().add(tempFontCss.toUri().toString());
      scene.getRoot().applyCss();
      scene.getRoot().layout();
    } catch (IOException e) {
      logger.error("Error applying font: ", e);
    }
  }

  public static void setTheme(String theme) {
    switch (theme) {
      case "Dark":
        currentTheme = "/styles/dark.css";
        break;
      case "High Contrast":
        currentTheme = "/styles/high-contrast.css";
        break;
      case "Colourblind":
        currentTheme = "/styles/colourblind.css";
        break;
      case "Purple":
        currentTheme = "/styles/purple.css";
        break;
      default:
        currentTheme = "/styles/default-blue.css";
    }
    if (mainStage != null && mainStage.getScene() != null) {
      applyCurrentTheme(mainStage.getScene());
    }
  }

  public static void setFont(String font) {
    currentFont = font.equals("Default") ? "System" : font;
    if (mainStage != null && mainStage.getScene() != null) {
      applyCurrentFont(mainStage.getScene());
    }
  }
}


