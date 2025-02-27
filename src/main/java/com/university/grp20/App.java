package com.university.grp20;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Simple JavaFX app for the intro page
 * intro page contains an intro message with 3 buttons
 */
public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/DashboardPage.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard Page");
            stage.show();
        } catch(Exception e) {
            e.printStackTrace(); //change this
        }
    }

    /**
     * Main method launches the application
     */
    public static void main(String[] args) {
        launch();
    }
}
