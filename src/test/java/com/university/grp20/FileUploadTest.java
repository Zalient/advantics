package com.university.grp20;

import com.university.grp20.controller.FileSelectionController;
import com.university.grp20.controller.LoginController;
import com.university.grp20.model.FileImportService;
import com.university.grp20.model.LoginService;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import java.io.File;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.api.FxAssert.verifyThat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jfree.chart.fx.ChartViewer;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


public class FileUploadTest extends ApplicationTest {
    private FileImportService fileImportService;
    private FileSelectionController fileSelectionController;

    @Override
    public void start(Stage stage) throws Exception {
        UIManager.setPrimaryStage(stage);
        stage.setTitle("Advertising Dashboard");
        UIManager.switchScene(UIManager.createFXMLLoader("/fxml/FileSelectionScene.fxml"));
        fileImportService = new FileImportService();
        fileSelectionController = (FileSelectionController) UIManager.getController();
    }

    /** doesn't work
     * @Test
     *   public void invalidImpressionHeaderTest() throws URISyntaxException {
     *     ClassLoader classLoader = getClass().getClassLoader();
     *     File impressionFile = new File(classLoader.getResource("testFiles/impression_log.csv").getFile());
     *     fileImportService.setImpressionLog(impressionFile);
     *
     *     File clickFile = new File(getClass().getClassLoader().getResource("testFiles/impression_log.csv").toURI());
     *     fileImportService.setClickLog(clickFile);
     *
     *     File serverFile = new File(getClass().getClassLoader().getResource("testFiles/impression_log.csv").toURI());
     *     fileImportService.setServerLog(serverFile);
     *
     *     clickOn("#nextButton");
     *
     *     Node dialogPane = lookup(".dialog-pane").query();
     *     Text dialogText = from(dialogPane).lookup((Text t) -> t.getText().startsWith("Impression Log File Header is Invalid")).query();
     *
     *     assertNotNull(dialogText, "Error alert should tell the user if they haven't uploaded a valid impression file");
     *
     *   }
     *
     */

    @Test
    void uploadButtonsExist() {
        verifyThat("#impressionLogButton", isVisible());
        verifyThat("#clickLogButton", isVisible());
        verifyThat("#serverLogButton", isVisible());

        verifyThat("#impressionLogButton", hasText("Select..."));
        verifyThat("#clickLogButton", hasText("Select..."));
        verifyThat("#serverLogButton", hasText("Select..."));

        verifyThat("#impressionLogButton", isEnabled());
        verifyThat("#clickLogButton", isEnabled());
        verifyThat("#serverLogButton", isEnabled());

        verifyThat("#impressionLogButton", node -> node instanceof Button);
        verifyThat("#clickLogButton", node -> node instanceof Button);
        verifyThat("#serverLogButton", node -> node instanceof Button);
    }

    @Test
    void nextButtonExists() {
        verifyThat("#nextButton", isVisible());
        verifyThat("#nextButton", hasText("Next"));
        verifyThat("#nextButton", isDisabled());
        verifyThat("#nextButton", node -> node instanceof Button);
    }

    @Test
    void filePathsExists() {
        verifyThat("#impressionPathLabel", isVisible());
        verifyThat("#impressionPathLabel", hasText("File Path"));
        verifyThat("#impressionPathLabel", node -> node instanceof Label);

        verifyThat("#clickPathLabel", isVisible());
        verifyThat("#clickPathLabel", hasText("File Path"));
        verifyThat("#clickPathLabel", node -> node instanceof Label);

        verifyThat("#serverPathLabel", isVisible());
        verifyThat("#serverPathLabel", hasText("File Path"));
        verifyThat("#serverPathLabel", node -> node instanceof Label);
    }

    @Test
    void logoutButtonExists() {
        verifyThat("#logoutButton", isVisible());
        verifyThat("#logoutButton", hasText("Logout"));
        verifyThat("#logoutButton", node -> node instanceof Button);
        verifyThat("#logoutButton", isEnabled());
    }

    @Test
    void skipButtonExists() {
        verifyThat("#skipButton", isVisible());
        verifyThat("#skipButton", hasText("Skip"));
        verifyThat("#skipButton", node -> node instanceof Button);
        verifyThat("#skipButton", isEnabled());
    }

    @Test
    void progressBarExists() {
        verifyThat("#importProgressBar", isVisible());
        verifyThat("#importProgressBar", node -> node instanceof ProgressBar);

        verifyThat("#importProgressLabel", isVisible());
        verifyThat("#importProgressLabel", hasText(""));
        verifyThat("#importProgressLabel", node -> node instanceof Label);
    }

    @Test
    void titleLabelExists() {
        verifyThat("#fileImportLabel", isVisible());
        verifyThat("#fileImportLabel", node -> node instanceof Label);

    }

}