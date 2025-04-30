package com.university.grp20.guiTests;

import com.university.grp20.UIManager;
import com.university.grp20.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.assertions.api.Assertions.assertThat;

public class MetricsPaneTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        User.setUsername("tester");
        User.setRole("Admin");
        User.setSelectedCampaign("test_dataset");

        UIManager.setMainStage(stage);
        FXMLLoader loader = UIManager.createFxmlLoader("/fxml/MetricsPane.fxml");
        UIManager.showMainStage("Metrics Test Window", loader);
    }

    @Test
    void testBackButtonExist(){
        verifyThat("#backButton", isVisible());
        verifyThat("#backButton", hasText("Back"));
        verifyThat("#backButton", isEnabled());
        verifyThat("#backButton", node -> node instanceof Button);
    }

    @Test
    void testMetricsButtonExist(){
        verifyThat("#metricsButton", isVisible());
        verifyThat("#metricsButton", hasText("Metrics"));
        verifyThat("#metricsButton", isEnabled());
        verifyThat("#metricsButton", node -> node instanceof Button);
    }

    @Test
    void testChartsButtonExist(){
        verifyThat("#chartsButton", isVisible());
        verifyThat("#chartsButton", hasText("Charts"));
        verifyThat("#chartsButton", isEnabled());
        verifyThat("#chartsButton", node -> node instanceof Button);
    }

    @Test
    void testHelpButtonExist(){
        verifyThat("#helpButton", isVisible());
        verifyThat("#helpButton", hasText("Click Me!"));
        verifyThat("#helpButton", isEnabled());
        verifyThat("#helpButton", node -> node instanceof Button);
    }

    @Test
    void testFilterButtonExist(){
        verifyThat("#filterButton", isVisible());
        verifyThat("#filterButton", hasText("Filter"));
        verifyThat("#filterButton", isEnabled());
        verifyThat("#filterButton", node -> node instanceof Button);
    }

    @Test
    void testSaveAsPDFButtonExist(){
        verifyThat("#pdfButton", isVisible());
        verifyThat("#pdfButton", hasText("Save as PDF"));
        verifyThat("#pdfButton", isEnabled());
        verifyThat("#pdfButton", node -> node instanceof Button);
    }

    @Test
    void testSaveAsCSVButtonExist(){
        verifyThat("#csvButton", isVisible());
        verifyThat("#csvButton", hasText("Save as CSV"));
        verifyThat("#csvButton", isEnabled());
        verifyThat("#csvButton", node -> node instanceof Button);
    }

    @Test
    void testResetDashButtonExist(){
        verifyThat("#resetDashButton", isVisible());
        verifyThat("#resetDashButton", hasText("Reset Filter"));
        verifyThat("#resetDashButton", isEnabled());
        verifyThat("#resetDashButton", node -> node instanceof Button);
    }

    @Test
    void testSettingsButtonExist(){
        verifyThat("#settingsButton", isVisible());
        verifyThat("#settingsButton", hasText("Settings"));
        verifyThat("#settingsButton", isEnabled());
        verifyThat("#settingsButton", node -> node instanceof Button);
    }

    @Test
    void testDashboardLabelsExist(){
        assertThat(lookup("#clicksLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#impressionsLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#uniquesLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#conversionsLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#bouncesLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#bounceRateLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#ctrLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#cpaLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#cpmLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#cpcLabel").queryAs(Label.class)).isNotNull();
        assertThat(lookup("#totalLabel").queryAs(Label.class)).isNotNull();

        assertThat(lookup("CLICKS").queryLabeled()).hasText("CLICKS");
        assertThat(lookup("IMPRESSIONS").queryLabeled()).hasText("IMPRESSIONS");
        assertThat(lookup("UNIQUES").queryLabeled()).hasText("UNIQUES");
        assertThat(lookup("CONVERSIONS").queryLabeled()).hasText("CONVERSIONS");
        assertThat(lookup("BOUNCES").queryLabeled()).hasText("BOUNCES");
        assertThat(lookup("BOUNCE RATE").queryLabeled()).hasText("BOUNCE RATE");
        assertThat(lookup("CTR").queryLabeled()).hasText("CTR");
        assertThat(lookup("CPA").queryLabeled()).hasText("CPA");
        assertThat(lookup("CPM").queryLabeled()).hasText("CPM");
        assertThat(lookup("CPC").queryLabeled()).hasText("CPC");
        assertThat(lookup("TOTAL COST").queryLabeled()).hasText("TOTAL COST");
    }
}
