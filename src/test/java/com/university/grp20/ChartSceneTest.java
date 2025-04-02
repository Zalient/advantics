package com.university.grp20;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import com.university.grp20.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jfree.chart.fx.ChartViewer;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.testfx.matcher.control.LabeledMatchers.hasText;


public class ChartSceneTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChartsScene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void testBackButtonExistence() {
        verifyThat("#backButton", isVisible());
        verifyThat("#backButton", hasText("Back"));
        verifyThat("#backButton", isEnabled());
        verifyThat("#backButton", node -> node instanceof Button);
    }

    @Test
    void testMetricButtonExistence() {
        verifyThat("#metricsButton", isVisible());
        verifyThat("#metricsButton", hasText("Metrics"));
        verifyThat("#metricsButton", isEnabled());
        verifyThat("#metricsButton", node -> node instanceof ToggleButton);
    }

    @Test
    void testChartsButtonExistence() {
        verifyThat("#chartsButton", isVisible());
        verifyThat("#chartsButton", hasText("Charts"));
        verifyThat("#chartsButton", isEnabled());
        verifyThat("#chartsButton", node -> node instanceof ToggleButton);
    }

    @Test
    void testAddChartButtonExistence() {
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
    void testFlowPaneExistence() {
        verifyThat("#addChartFlowPane", isVisible());
        verifyThat("#addChartFlowPane", node -> node instanceof FlowPane);
    }

    @Test
    void testDisplayingImpressionsChart() {
        clickOn("#addChartButton");
        clickOn("No Of Impressions");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingClicksChart() {
        clickOn("#addChartButton");
        clickOn("No Of Clicks");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingUniquesChart() {
        clickOn("#addChartButton");
        clickOn("No Of Uniques");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingBouncesChart() {
        clickOn("#addChartButton");
        clickOn("No Of Bounces");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingConversionsChart() {
        clickOn("#addChartButton");
        clickOn("No Of Conversions");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingTotalCostChart() {
        clickOn("#addChartButton");
        clickOn("Total");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingCTRChart() {
        clickOn("#addChartButton");
        clickOn("CTR");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingCPAChart() {
        clickOn("#addChartButton");
        clickOn("CPA");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingCPCChart() {
        clickOn("#addChartButton");
        clickOn("CPC");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingCPMChart() {
        clickOn("#addChartButton");
        clickOn("CPM");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testDisplayingBounceRateChart() {
        clickOn("#addChartButton");
        clickOn("Bounce Rate");
        sleep(500);

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(3, buttonBox.getChildren().size(), "HBox should contain three buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testHistogramNormalInput() {
        clickOn("#addChartButton");
        clickOn("Click Cost Histogram");
        sleep(500);
        clickOn(".dialog-pane .text-field");
        sleep(500);
        write("10");
        sleep(500);
        clickOn("Apply");

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertNotNull(flowPane, "Chart container should exist");
        assertFalse(flowPane.getChildren().isEmpty(), "Chart should be displayed here");

        VBox chartBox = (VBox) flowPane.getChildren().get(flowPane.getChildren().size() - 1);
        assertNotNull(chartBox, "Chart VBox should be present");

        assertTrue(chartBox.getChildren().get(0) instanceof HBox, "HBox should come first");
        assertTrue(chartBox.getChildren().get(1) instanceof ChartViewer, "ChartViewer should come second");

        HBox buttonBox = (HBox) chartBox.getChildren().get(0);
        assertEquals(2, buttonBox.getChildren().size(), "HBox should contain two buttons");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Third button should be an export CSV Button");

        ChartViewer chartViewer = (ChartViewer) chartBox.getChildren().get(1);
        assertNotNull(chartViewer.getChart(), "ChartViewer should contain a chart");
    }

    @Test
    void testHistogramInvalidInputType() {
        clickOn("#addChartButton");
        clickOn("Click Cost Histogram");
        sleep(500);
        clickOn(".dialog-pane .text-field");
        sleep(500);
        write("NotAnInt");
        sleep(500);
        clickOn("Apply");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Input is not an integer, wrong type"));

        clickOn("OK");

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertTrue(flowPane.getChildren().isEmpty(), "Charts scene should be empty, no charts displayed");
    }

    @Test
    void testHistogramZeroInput() {
        clickOn("#addChartButton");
        clickOn("Click Cost Histogram");
        sleep(500);
        clickOn(".dialog-pane .text-field");
        sleep(500);
        write("0");
        sleep(500);
        clickOn("Apply");

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertTrue(flowPane.getChildren().isEmpty(), "Charts scene should be empty, no charts displayed");
    }

    @Test
    void testHistogramNegativeInput() {
        clickOn("#addChartButton");
        clickOn("Click Cost Histogram");
        sleep(500);
        clickOn(".dialog-pane .text-field");
        clickOn(".dialog-pane .text-field");
        sleep(500);
        write("-5");
        sleep(500);
        clickOn("Apply");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Input is negative, please enter a positive value"));

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertTrue(flowPane.getChildren().isEmpty(), "Charts scene should be empty, no charts displayed");
    }

    @Test
    void testDisplayingImpressionsFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("No Of Impressions");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingClicksFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("No Of Clicks");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingUniquesFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("No Of Uniques");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingBouncesFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("No Of Bounces");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingConversionsFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("No Of Conversions");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingTotalCostsFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("Total");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingCTRFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("CTR");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingCPAFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("CPA");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingCPCFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("CPC");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingCPMFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("CPM");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }

    @Test
    void testDisplayingBounceRateFilter() {
        User.setRole("Admin");
        clickOn("#addChartButton");
        clickOn("Bounce Rate");
        sleep(500);
        clickOn("Filter");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        Optional<Window> filterWindow = listWindows().stream().filter(window -> window instanceof Stage && ((Stage) window).getTitle().equals("Filter Selection")).findFirst();
        assertTrue(filterWindow.isPresent(), "Filter Selection window should be opened");
    }
}
