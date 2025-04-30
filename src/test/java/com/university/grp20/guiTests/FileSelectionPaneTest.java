package com.university.grp20.guiTests;

import com.university.grp20.UIManager;
import com.university.grp20.model.User;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.api.FxAssert.verifyThat;

import javafx.scene.control.*;


public class FileSelectionPaneTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        User.setRole("Admin");
        UIManager.setMainStage(stage);
        stage.setTitle("Advertising Dashboard");
        UIManager.showMainStage("Advertising Dashboard", UIManager.createFxmlLoader("/fxml/FileSelectionPane.fxml"));
    }

    @Test
    void checkRoleLabelExist(){
        verifyThat("#roleLabel", isNotNull());
        verifyThat("#roleLabel", hasText("Role: Admin"));
        verifyThat("#roleLabel", isVisible());
    }

    @Test
    void checkAddNewCampaignTitleExist(){
        verifyThat("#addNewCampaignTitle", isNotNull());
        verifyThat("#addNewCampaignTitle", hasText("Add New Campaign"));
        verifyThat("#addNewCampaignTitle", isVisible());
    }

    @Test
    void checkHelpButtonExist(){
        verifyThat("#helpButton", isVisible());
        verifyThat("#helpButton", hasText("Click Me!"));
        verifyThat("#helpButton", isEnabled());
        verifyThat("#helpButton", node -> node instanceof Button);
    }

    @Test
    void checkLogoutButtonExist(){
        verifyThat("#logoutButton", isVisible());
        verifyThat("#logoutButton", hasText("Logout"));
        verifyThat("#logoutButton", isEnabled());
        verifyThat("#logoutButton", node -> node instanceof Button);
    }

    @Test
    void checkCampaignNameLabelAndTextfieldExist(){
        verifyThat("#campaignNameLabel", isNotNull());
        verifyThat("#campaignNameLabel", hasText("Campaign Name"));
        verifyThat("#campaignNameLabel", isVisible());

        verifyThat("#campaignNameTextField", isVisible());
        verifyThat("#campaignNameTextField", isEnabled());
        verifyThat("#campaignNameTextField", (TextField tf) -> tf.getText().isEmpty());
        verifyThat("#campaignNameTextField", node -> node instanceof TextField);
    }

    @Test
    void checkImpressionLogLabelAndButtonExist(){
        verifyThat("#impressionLogLabel", isNotNull());
        verifyThat("#impressionLogLabel", hasText("Impression Log"));
        verifyThat("#impressionLogLabel", isVisible());

        verifyThat("#impressionLogButton", isVisible());
        verifyThat("#impressionLogButton", hasText("Select..."));
        verifyThat("#impressionLogButton", isEnabled());
        verifyThat("#impressionLogButton", node -> node instanceof Button);
    }

    @Test
    void checkClickLogLabelAndButtonExist(){
        verifyThat("#clickLogLabel", isNotNull());
        verifyThat("#clickLogLabel", hasText("Click Log"));
        verifyThat("#clickLogLabel", isVisible());

        verifyThat("#clickLogButton", isVisible());
        verifyThat("#clickLogButton", hasText("Select..."));
        verifyThat("#clickLogButton", isEnabled());
        verifyThat("#clickLogButton", node -> node instanceof Button);
    }

    @Test
    void checkServerLogLabelAndButtonExist(){
        verifyThat("#serverLogLabel", isNotNull());
        verifyThat("#serverLogLabel", hasText("Server Log"));
        verifyThat("#serverLogLabel", isVisible());

        verifyThat("#serverLogButton", isVisible());
        verifyThat("#serverLogButton", hasText("Select..."));
        verifyThat("#serverLogButton", isEnabled());
        verifyThat("#serverLogButton", node -> node instanceof Button);
    }

    @Test
    void checkNextButtonExist(){
        verifyThat("#nextButton", isVisible());
        verifyThat("#nextButton", hasText("Next"));
        verifyThat("#nextButton", isDisabled());
        verifyThat("#nextButton", node -> node instanceof Button);
    }

    @Test
    void checkProgressBarExist(){
        verifyThat("#importProgressBar", isVisible());
        verifyThat("#importProgressBar", node -> node instanceof ProgressBar);

        verifyThat("#importProgressLabel", isVisible());
        verifyThat("#importProgressLabel", hasText(""));
        verifyThat("#importProgressLabel", node -> node instanceof Label);
    }

    @Test
    void checkSelectCampaignLabelExist(){
        verifyThat("#selectCampaignLabel", isNotNull());
        verifyThat("#selectCampaignLabel", hasText("Select Uploaded Campaign"));
        verifyThat("#selectCampaignLabel", isVisible());
    }

    @Test
    void checkUploadedCampaignVBoxExist(){
        verifyThat("#uploadedCampaignVBox", isNotNull());
    }

    @Test
    void checkFilePathLabelExists() {
        verifyThat("#impressionPathLabel", isVisible());
        verifyThat("#impressionPathLabel", hasText(""));
        verifyThat("#impressionPathLabel", node -> node instanceof Label);

        verifyThat("#clickPathLabel", isVisible());
        verifyThat("#clickPathLabel", hasText(""));
        verifyThat("#clickPathLabel", node -> node instanceof Label);

        verifyThat("#serverPathLabel", isVisible());
        verifyThat("#serverPathLabel", hasText(""));
        verifyThat("#serverPathLabel", node -> node instanceof Label);
    }
}