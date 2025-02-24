package com.university.grp20;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Simple JavaFX app for the intro page
 * intro page contains an intro message with 3 buttons
 */
public class App extends Application {

    /**
     * Starts JavaFX app by setting up page with intro message and 3 buttons
     */
    @Override
    public void start(Stage stage) {
        Label label = new Label("Upload Files");
        Button impressionButton = new Button("Upload impression");
        Button clickButton = new Button("Upload click");
        Button serverButton = new Button("Upload server");

        VBox introBox = new VBox(10, label, impressionButton, clickButton, serverButton);
        introBox.setStyle("-fx-padding:20px; -fx-alignment: center;");

        Scene scene = new Scene(introBox, 400, 300);
        stage.setScene(scene);

        stage.setTitle("JavaFX App");
        stage.show();
    }

    /**
     * Main method launches the application
     */
    public static void main(String[] args) {
        launch();
    }
}
