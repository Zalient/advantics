package com.university.grp20;

import com.university.grp20.controller.FileUpload;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Simple JavaFX app for the intro page
 * intro page contains an intro message with 3 buttons
 */
public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);

    /**
     * Starts JavaFX app by setting up page with intro message and 3 buttons
     */
    @Override
    public void start(Stage stage) {
        logger.info("Starting application");
        try {
            FXMLLoader loader = new FXMLLoader(new File("src/main/java/com/university/grp20/view/FileUpload.fxml").toURI().toURL());
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Advertising Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Main method launches the application
     */
    public static void main(String[] args) {
        launch();
    }
}
