package com.university.grp20;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;

public class FileSelectionControllerTest extends ApplicationTest {

    public void start(Stage stage) {
        new App().start(stage);
    }

    @Test
    void testImpressionLabel() {
        verifyThat("Impression Log", LabeledMatchers.hasText("Impression Log"));
    }

    @Test
    void testClickLabel() {
        verifyThat("Click Log", LabeledMatchers.hasText("Click Log"));
    }

    @Test
    void testServerLabel() {
        verifyThat("Server Log", LabeledMatchers.hasText("Server Log"));
    }

    @Test
    void testImpressionLogButton() {
        verifyThat("#impressionLogButton", LabeledMatchers.hasText("Select"));
    }

    @Test
    void testClickLogButton() {
        verifyThat("#clickLogButton", LabeledMatchers.hasText("Select"));
    }

    @Test
    void testServerLogButton() {
        verifyThat("#serverLogButton", LabeledMatchers.hasText("Select"));
    }

    @Test
    void testNextButton() {
        verifyThat("#nextButton", LabeledMatchers.hasText("Next"));
    }

    @Test
    void testTitle() {
        verifyThat("File Import", LabeledMatchers.hasText("File Import"));
    }

    @Test
    void testProgressBarVisibility() {
        verifyThat("#importProgressBar", NodeMatchers.isVisible());
    }

    @Test
    void testProgressBarLabelVisibility() {
        verifyThat("#importProgressLabel", NodeMatchers.isVisible());
    }


}
