package com.university.grp20;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;

import static org.testfx.api.FxAssert.verifyThat;

class IntroPageTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new App().start(stage);
    }

    @Test
    void testLabelExists() {
        verifyThat("Upload Files", LabeledMatchers.hasText("Upload Files"));
    }

    @Test
    void testAllButtonsExist() {
        verifyThat("Upload impression", LabeledMatchers.hasText("Upload impression"));
        verifyThat("Upload click", LabeledMatchers.hasText("Upload click"));
        verifyThat("Upload server", LabeledMatchers.hasText("Upload server"));
    }

    @Test
    void testClickButtons() {
        clickOn("Upload impression");
        clickOn("Upload click");
        clickOn("Upload server");
    }
}
