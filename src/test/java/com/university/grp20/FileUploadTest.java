package com.university.grp20;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;

public class FileUploadTest extends ApplicationTest {

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
    void testImpressionUploadButton() {
        verifyThat("#impressionUpload", LabeledMatchers.hasText("Upload"));
    }

    @Test
    void testClickUploadButton() {
        verifyThat("#clickUpload", LabeledMatchers.hasText("Upload"));
    }

    @Test
    void testServerUploadButton() {
        verifyThat("#serverUpload", LabeledMatchers.hasText("Upload"));
    }

    @Test
    void testNextButton() {
        verifyThat("Next", LabeledMatchers.hasText("Next"));
    }

    @Test
    void testTitle() {
        verifyThat("File Upload", LabeledMatchers.hasText("File Upload"));
    }

    @Test
    void testProgressBar() {
        verifyThat("#fileProgressBar", NodeMatchers.isVisible());
    }

    @Test
    void testProgressBarLabel() {
        verifyThat("#progressLabel", NodeMatchers.isVisible());
    }


}
