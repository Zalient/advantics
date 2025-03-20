package com.university.grp20;

import com.university.grp20.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class SettingsSceneTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        User.setRole("Admin");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsScene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void testBackButtonExists() {
        verifyThat("#backButton", isVisible());
        verifyThat("#backButton", isEnabled());
        verifyThat("#backButton", hasText("Back"));
    }

    @Test
    void testBounceDefinitionUI(){
        verifyThat("Metrics", isVisible());
        verifyThat("Bounce Registered as:", isVisible());
        verifyThat("#pagesViewedOpt", isVisible());
        verifyThat("#pagesViewedOpt", isEnabled());
        verifyThat("#timeSpentOpt", isVisible());
        verifyThat("#timeSpentOpt", isEnabled());
        verifyThat("#bounceValField", isVisible());
        verifyThat("#bounceChooser", isVisible());
        verifyThat("#bounceChooser", hasText("Apply"));
    }

    @Test
    void testBounceDefinitionFlow(){
        clickOn("#pagesViewedOpt");
        verifyThat("#bounceValField", isEnabled());
        verifyThat("#bounceChooser", isEnabled());
    }

    @Test
    void testPagesViewedAppSucc() {
        clickOn("#pagesViewedOpt");
        clickOn("#bounceValField").write("10");
        clickOn("#bounceChooser");
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Bounce successfully redefined"));

        clickOn("OK");
    }

    @Test
    void testTimeSpentAppSucc() {
        clickOn("#timeSpentOpt");
        clickOn("#bounceValField").write("10");
        clickOn("#bounceChooser");
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Bounce successfully redefined"));

        clickOn("OK");
    }

    @Test
    void testPagesViewedAppEmpty() {
        clickOn("#pagesViewedOpt");
        clickOn("#bounceValField").write("");
        clickOn("#bounceChooser");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Empty input, please enter a value"));
        clickOn("OK");
    }

    @Test
    void testTimeSpentAppEmpty() {
        clickOn("#timeSpentOpt");
        clickOn("#bounceValField").write("");
        clickOn("#bounceChooser");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Empty input, please enter a value"));
        clickOn("OK");
    }

    @Test
    void testPagesViewedAppNeg() {
        clickOn("#pagesViewedOpt");
        clickOn("#bounceValField").write("-10");
        clickOn("#bounceChooser");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Input is a negative number, please enter positive integer"));
        clickOn("OK");
    }

    @Test
    void testTimeSpentAppNeg() {
        clickOn("#timeSpentOpt");
        clickOn("#bounceValField").write("-10");
        clickOn("#bounceChooser");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Input is a negative number, please enter positive integer"));
        clickOn("OK");
    }

    @Test
    void testPagesViewedAppWrongType() {
        clickOn("#pagesViewedOpt");
        clickOn("#bounceValField").write("NotAnInt");
        clickOn("#bounceChooser");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Input is not an integer, wrong type"));
        clickOn("OK");
    }

    @Test
    void testTimeSpentAppWrongType() {
        clickOn("#timeSpentOpt");
        clickOn("#bounceValField").write("NotAnInt");
        clickOn("#bounceChooser");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("Input is not an integer, wrong type"));
        clickOn("OK");
    }

    @Test
    void testAddUserUI() {
        verifyThat("#addUsernameField", isVisible());
        verifyThat("#addUsernameField", isEnabled());
        verifyThat("#addPasswordField", isVisible());
        verifyThat("#addPasswordField", isEnabled());
        verifyThat("#addUserButton", isVisible());
        verifyThat("#addUserButton", isVisible());
        verifyThat("#addUserButton", isEnabled());
        verifyThat("#addUserButton", hasText("Add User"));
    }

    @Test
    void testAddUserMissingUsername(){
        clickOn("#addPasswordField").write("passAdmin");
        clickOn("#selectRoleMenu");
        clickOn("Admin");
        clickOn("#addUserButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("You have not filled out all the user detail fields.."));
        clickOn("OK");
    }

    @Test
    void testAddUserMissingPassword(){
        clickOn("#addUsernameField").write("New Admin");
        clickOn("#selectRoleMenu");
        clickOn("Admin");
        clickOn("#addUserButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("You have not filled out all the user detail fields.."));
        clickOn("OK");
    }

    @Test
    void testAddUserNoRole(){
        clickOn("#addUsernameField").write("New Admin");
        clickOn("#addPasswordField").write("passAdmin");
        clickOn("#addUserButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("You have not filled out all the user detail fields.."));
        clickOn("OK");
    }

    @Test
    void testAddUserNormalViewer(){
        clickOn("#addUsernameField").write("New Viewer");
        clickOn("#addPasswordField").write("passViewer");
        clickOn("#selectRoleMenu");
        clickOn("Viewer");
        clickOn("#addUserButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("New user \"New Viewer\" was successfully added to the database"));
        clickOn("OK");
    }

    @Test
    void testAddUserNormalEditor(){
        clickOn("#addUsernameField").write("New Editor");
        clickOn("#addPasswordField").write("passEditor");
        clickOn("#selectRoleMenu");
        clickOn("Editor");
        clickOn("#addUserButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("New user \"New Editor\" was successfully added to the database"));
        clickOn("OK");
    }

    @Test
    void testAddUserNormalAdmin(){
        clickOn("#addUsernameField").write("New Admin");
        clickOn("#addPasswordField").write("passAdmin");
        clickOn("#selectRoleMenu");
        clickOn("Admin");
        clickOn("#addUserButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("New user \"New Admin\" was successfully added to the database"));
        clickOn("OK");
    }

    @Test
    void testDuplicateEntry(){
        clickOn("#addUsernameField").write("Repeat Admin");
        clickOn("#addPasswordField").write("RepAdPwrd");
        clickOn("#selectRoleMenu");
        clickOn("Admin");
        clickOn("#addUserButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("New user \"Repeat Admin\" was successfully added to the database"));
        clickOn("OK");

        clickOn("#addUsernameField").write("Repeat Admin");
        clickOn("#addPasswordField").write("RepAdPwrd");
        clickOn("#selectRoleMenu");
        clickOn("Admin");
        clickOn("#addUserButton");
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", hasText("A user with that username already exists in the database"));
        clickOn("OK");
    }

    /**
    @Test
    void testEditUserMissingUser(){

    }

    @Test
    void testEditUserMissingPassword(){

    }

    @Test
    void testEditUserMissingRole(){

    }

    @Test
    void testEditUserNormal(){

    }
     **/

    @Test
    void testExportLogButtonsExist() {
        verifyThat("#exportLogToPDFButton", isVisible());
        verifyThat("#exportLogToPDFButton", isEnabled());
        verifyThat("#exportLogToPDFButton", hasText("Export To PDF"));
        verifyThat("#exportLogToCSVButton", isVisible());
        verifyThat("#exportLogToPDFButton", isEnabled());
        verifyThat("#exportLogToCSVButton", hasText("Export To CSV"));
    }
}
