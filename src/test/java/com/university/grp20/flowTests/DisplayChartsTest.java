package com.university.grp20.flowTests;

import com.university.grp20.UIManager;
import com.university.grp20.model.DBHelper;
import com.university.grp20.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jfree.chart.fx.ChartViewer;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.DriverManager;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

//fix
public class DisplayChartsTest extends ApplicationTest{
    @Override
    public void start(Stage stage) throws Exception {
        User.setRole("Admin");
        UIManager.setMainStage(stage);
        FXMLLoader loader = UIManager.createFxmlLoader("/fxml/ChartsPane.fxml");
        UIManager.showMainStage("Charts Test Window", loader);
        DBHelper.useTestConnection(DriverManager.getConnection("jdbc:sqlite:test_dataset.db"));
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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(6, buttonBox.getChildren().size(), "HBox should contain five buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "First button should be a Filter Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(3) instanceof Button, "Fourth button should be a delete chart Button");

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
        assertEquals(5, buttonBox.getChildren().size(), "HBox should contain four buttons and a label");
        assertTrue(buttonBox.getChildren().get(0) instanceof Button, "Second button should be an export PDF Button");
        assertTrue(buttonBox.getChildren().get(1) instanceof Button, "Third button should be an export CSV Button");
        assertTrue(buttonBox.getChildren().get(2) instanceof Button, "Fourth button should be a delete chart Button");

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

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Input is less than 0, please enter a positive value"));

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertTrue(flowPane.getChildren().isEmpty(), "Charts scene should be empty, no charts displayed");
    }

    @Test
    void testHistogramNegativeInput() {
        clickOn("#addChartButton");
        clickOn("Click Cost Histogram");
        sleep(500);
        clickOn(".dialog-pane .text-field");
        sleep(500);
        write("-5");
        sleep(500);
        clickOn("Apply");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Input is less than 0, please enter a positive value"));

        FlowPane flowPane = lookup("#addChartFlowPane").query();
        assertTrue(flowPane.getChildren().isEmpty(), "Charts scene should be empty, no charts displayed");
    }

    /**
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
    **/
}
