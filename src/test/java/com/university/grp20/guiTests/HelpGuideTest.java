package com.university.grp20.guiTests;

import com.university.grp20.UIManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class HelpGuideTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        UIManager.setMainStage(stage);
        FXMLLoader loader = UIManager.createFxmlLoader("/fxml/HelpGuidePane.fxml");
        UIManager.showMainStage("Help Guide Test Window", loader);
    }

    @Test
    void testPageTitleExist(){
        verifyThat("#guideTitleLabel", isNotNull());
        verifyThat("#guideTitleLabel", hasText("Page Guide"));
        verifyThat("#guideTitleLabel", isVisible());
    }

    @Test
    void testPreviousButtonExist(){
        verifyThat("#prevPicButton", isVisible());
        verifyThat("#prevPicButton", hasText("Previous"));
        verifyThat("#prevPicButton", isEnabled());
        verifyThat("#prevPicButton", node -> node instanceof Button);
    }

    @Test
    void testNextButtonExist(){
        verifyThat("#nextPicButton", isVisible());
        verifyThat("#nextPicButton", hasText("Next"));
        verifyThat("#nextPicButton", isEnabled());
        verifyThat("#nextPicButton", node -> node instanceof Button);
    }
}
