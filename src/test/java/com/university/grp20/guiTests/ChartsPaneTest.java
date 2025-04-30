package com.university.grp20.guiTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import com.university.grp20.UIManager;
import com.university.grp20.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.testfx.matcher.control.LabeledMatchers.hasText;


public class ChartsPaneTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        User.setRole("Admin");
        UIManager.setMainStage(stage);
        FXMLLoader loader = UIManager.createFxmlLoader("/fxml/ChartsPane.fxml");
        UIManager.showMainStage("Charts Test Window", loader);
    }

    @Test
    void testBackButtonExist() {
        verifyThat("#backButton", isVisible());
        verifyThat("#backButton", hasText("Back"));
        verifyThat("#backButton", isEnabled());
        verifyThat("#backButton", node -> node instanceof Button);
    }

    @Test
    void testMetricButtonExist() {
        verifyThat("#metricsButton", isVisible());
        verifyThat("#metricsButton", hasText("Metrics"));
        verifyThat("#metricsButton", isEnabled());
        verifyThat("#metricsButton", node -> node instanceof Button);
    }

    @Test
    void testChartsButtonExist() {
        verifyThat("#chartsButton", isVisible());
        verifyThat("#chartsButton", hasText("Charts"));
        verifyThat("#chartsButton", isEnabled());
        verifyThat("#chartsButton", node -> node instanceof Button);
    }

    @Test
    void testAddChartButtonExist() {
        verifyThat("#addChartButton", isVisible());
        verifyThat("#addChartButton", hasText("Add"));
        verifyThat("#addChartButton", isEnabled());
        verifyThat("#addChartButton", node -> node instanceof MenuButton);
    }

    @Test
    void testMenuButtonOptions() {
        clickOn("#addChartButton");

        MenuButton menuButton = lookup("#addChartButton").queryAs(MenuButton.class);
        assertNotNull(menuButton, "MenuButton should exist");

        List<String> expectedOptions = List.of("No Of Impressions", "No Of Clicks", "No Of Uniques",
                "No Of Bounces", "No Of Conversions", "Total",
                "CTR", "CPA", "CPC", "CPM", "Bounce Rate", "Click Cost Histogram");

        List<String> actualOptions = menuButton.getItems().stream().map(MenuItem::getText).toList();

        assertTrue(actualOptions.containsAll(expectedOptions), "Missing options");
    }

    @Test
    void testSaveAllChartButtonExist() {
        verifyThat("#saveAllChartsButton", isVisible());
        verifyThat("#saveAllChartsButton", hasText("Save All Charts"));
        verifyThat("#saveAllChartsButton", isEnabled());
        verifyThat("#saveAllChartsButton", node -> node instanceof Button);
    }

    @Test
    void testHelpButtonExist() {
        verifyThat("#helpButton", isVisible());
        verifyThat("#helpButton", hasText("Click Me!"));
        verifyThat("#helpButton", isEnabled());
        verifyThat("#helpButton", node -> node instanceof Button);
    }
}
