package com.university.grp20.guiTests;

import com.university.grp20.UIManager;
import com.university.grp20.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;

public class DefaultSettingsPaneTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        User.setRole("Admin");
        UIManager.setMainStage(stage);
        FXMLLoader loader = UIManager.createFxmlLoader("/fxml/settings/SettingsPane.fxml");
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
    void testPageTitleExist(){
        verifyThat("#settingsTitle", isNotNull());
        verifyThat("#settingsTitle", hasText("Settings"));
        verifyThat("#settingsTitle", isVisible());
    }

    @Test
    void testHelpButtonExist(){
        verifyThat("#helpButton", isVisible());
        verifyThat("#helpButton", hasText("Click Me!"));
        verifyThat("#helpButton", isEnabled());
        verifyThat("#helpButton", node -> node instanceof Button);
    }

    @Test
    void testMetricsSettingsButtonExist() {
        verifyThat("#metricSettingsButton", isVisible());
        verifyThat("#metricSettingsButton", hasText("Metrics"));
        verifyThat("#metricSettingsButton", isEnabled());
        verifyThat("#metricSettingsButton", node -> node instanceof Button);
    }

    @Test
    void testUserSettingsButtonExist() {
        verifyThat("#userSettingsButton", isVisible());
        verifyThat("#userSettingsButton", hasText("Users"));
        verifyThat("#userSettingsButton", isEnabled());
        verifyThat("#userSettingsButton", node -> node instanceof Button);
    }

    @Test
    void testExportSettingsButtonExist() {
        verifyThat("#exportSettingsButton", isVisible());
        verifyThat("#exportSettingsButton", hasText("Export"));
        verifyThat("#exportSettingsButton", isEnabled());
        verifyThat("#exportSettingsButton", node -> node instanceof Button);
    }

    @Test
    void testThemesSettingsButtonExist() {
        verifyThat("#styleSettingsButton", isVisible());
        verifyThat("#styleSettingsButton", hasText("Style"));
        verifyThat("#styleSettingsButton", isEnabled());
        verifyThat("#styleSettingsButton", node -> node instanceof Button);
    }

    @Test
    void testMetricSettingsPage(){
        clickOn("#metricSettingsButton");

        verifyThat("#bounceDefTitle", isNotNull());
        verifyThat("#bounceDefTitle", hasText("Bounce Definition"));
        verifyThat("#bounceDefTitle", isVisible());

        verifyThat("#bounceDefLabel", isNotNull());
        verifyThat("#bounceDefLabel", isVisible());

        verifyThat("#pagesViewedButton", isVisible());
        verifyThat("#pagesViewedButton", hasText("Pages Viewed"));
        verifyThat("#pagesViewedButton", isEnabled());
        verifyThat("#pagesViewedButton", node -> node instanceof RadioButton);

        verifyThat("#timeSpentButton", isVisible());
        verifyThat("#timeSpentButton", hasText("Time Spent on Page"));
        verifyThat("#timeSpentButton", isEnabled());
        verifyThat("#timeSpentButton", node -> node instanceof RadioButton);

        verifyThat("#bounceValField", isVisible());
        verifyThat("#bounceValField", Node::isDisable);
        verifyThat("#bounceValField", (TextField tf) -> tf.getText().isEmpty());
        verifyThat("#bounceValField", (TextField tf) -> "Bounce Value".equals(tf.getPromptText()));
        verifyThat("#bounceValField", node -> node instanceof TextField);

        verifyThat("#applyBounceButton", isVisible());
        verifyThat("#applyBounceButton", hasText("Apply"));
        verifyThat("#applyBounceButton", Node::isDisable);
        verifyThat("#applyBounceButton", node -> node instanceof Button);
    }

    @Test
    void testAddUserSettingsPage(){
        clickOn("#userSettingsButton");

        verifyThat("#addUserTitle", isNotNull());
        verifyThat("#addUserTitle", hasText("Add User"));
        verifyThat("#addUserTitle", isVisible());

        verifyThat("#usernameSubTitle", isNotNull());
        verifyThat("#usernameSubTitle", hasText("Username:"));
        verifyThat("#usernameSubTitle", isVisible());

        verifyThat("#newUsernameField", isVisible());
        verifyThat("#newUsernameField", isEnabled());
        verifyThat("#newUsernameField", (TextField tf) -> tf.getText().isEmpty());
        verifyThat("#newUsernameField", (TextField tf) -> "Username".equals(tf.getPromptText()));
        verifyThat("#newUsernameField", node -> node instanceof TextField);

        verifyThat("#passwordSubTitle", isNotNull());
        verifyThat("#passwordSubTitle", hasText("Password:"));
        verifyThat("#passwordSubTitle", isVisible());

        verifyThat("#newPasswordField", isVisible());
        verifyThat("#newPasswordField", isEnabled());
        verifyThat("#newPasswordField", (TextField tf) -> tf.getText().isEmpty());
        verifyThat("#newPasswordField", (TextField tf) -> "Password".equals(tf.getPromptText()));
        verifyThat("#newPasswordField", node -> node instanceof TextField);

        verifyThat("#newRoleMenu", isVisible());
        verifyThat("#newRoleMenu", node -> node instanceof ComboBox<?> && "Select Role".equals(((ComboBox<?>) node).getPromptText()));
        verifyThat("#newRoleMenu", isEnabled());
        verifyThat("#newRoleMenu", node -> node instanceof ComboBox<?>);

        verifyThat("#addUserButton", isVisible());
        verifyThat("#addUserButton", hasText("Add User"));
        verifyThat("#addUserButton", isEnabled());
        verifyThat("#addUserButton", node -> node instanceof Button);
    }

    @Test
    void testAddUserSettingsDropdown(){
        clickOn("#userSettingsButton");
        clickOn("#newRoleMenu");

        verifyThat("Admin", isVisible());
        verifyThat("Editor", isVisible());
        verifyThat("Viewer", isVisible());
    }

    @Test
    void testEditUserSettingsDropdownUsernames(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        clickOn("#currentUsersMenu");

        verifyThat("Admin", isVisible());
        verifyThat("Editor", isVisible());
        verifyThat("Viewer", isVisible());
    }

    @Test
    void testEditUserSettingsDropdownRoles(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        clickOn("#changeRoleMenu");

        verifyThat("Admin", isVisible());
        verifyThat("Editor", isVisible());
        verifyThat("Viewer", isVisible());
    }

    @Test
    void testEditUserSettingsPage(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        verifyThat("#editUserTitle", isNotNull());
        verifyThat("#editUserTitle", hasText("Edit User"));
        verifyThat("#editUserTitle", isVisible());

        verifyThat("#currentUsersMenu", isVisible());
        verifyThat("#currentUsersMenu", node -> node instanceof ComboBox<?> && "Select User".equals(((ComboBox<?>) node).getPromptText()));
        verifyThat("#currentUsersMenu", isEnabled());
        verifyThat("#currentUsersMenu", node -> node instanceof ComboBox<?>);

        verifyThat("#newPasswordSubTitle", isNotNull());
        verifyThat("#newPasswordSubTitle", hasText("New Password:"));
        verifyThat("#newPasswordSubTitle", isVisible());

        verifyThat("#changePasswordField", isVisible());
        verifyThat("#changePasswordField", isEnabled());
        verifyThat("#changePasswordField", (TextField tf) -> tf.getText().isEmpty());
        verifyThat("#changePasswordField", (TextField tf) -> "New Password".equals(tf.getPromptText()));
        verifyThat("#changePasswordField", node -> node instanceof TextField);

        verifyThat("#changeRoleMenu", isVisible());
        verifyThat("#changeRoleMenu", node -> node instanceof ComboBox<?> && "New Role".equals(((ComboBox<?>) node).getPromptText()));
        verifyThat("#changeRoleMenu", isEnabled());
        verifyThat("#changeRoleMenu", node -> node instanceof ComboBox<?>);

        verifyThat("#editUserButton", isVisible());
        verifyThat("#editUserButton", hasText("Edit User"));
        verifyThat("#editUserButton", isEnabled());
        verifyThat("#editUserButton", node -> node instanceof Button);
    }

    @Test
    void testThemesSettings(){
        clickOn("#styleSettingsButton");

        verifyThat("#themeTitle", isNotNull());
        verifyThat("#themeTitle", hasText("Theme"));
        verifyThat("#themeTitle", isVisible());

        verifyThat("#themeComboBox", isVisible());
        verifyThat("#themeComboBox", node -> node instanceof ComboBox<?> && "Select Theme".equals(((ComboBox<?>) node).getPromptText()));
        verifyThat("#themeComboBox", isEnabled());
        verifyThat("#themeComboBox", node -> node instanceof ComboBox<?>);
    }

    @Test
    void testFontsSettings(){
        clickOn("#styleSettingsButton");

        verifyThat("#fontTitle", isNotNull());
        verifyThat("#fontTitle", hasText("Font"));
        verifyThat("#fontTitle", isVisible());

        verifyThat("#fontComboBox", isVisible());
        verifyThat("#fontComboBox", node -> node instanceof ComboBox<?> && "Select Font".equals(((ComboBox<?>) node).getPromptText()));
        verifyThat("#fontComboBox", isEnabled());
        verifyThat("#fontComboBox", node -> node instanceof ComboBox<?>);
    }

    @Test
    void testThemeSettingsDropDown(){
        clickOn("#styleSettingsButton");
        clickOn("#themeComboBox");

        verifyThat("Default", isVisible());
        verifyThat("Dark", isVisible());
        verifyThat("High Contrast", isVisible());
        verifyThat("Colourblind", isVisible());
        verifyThat("Purple", isVisible());
    }

    @Test
    void testFontsSettingsDropDown(){
        clickOn("#styleSettingsButton");
        clickOn("#fontComboBox");

        verifyThat("Default", isVisible());
        verifyThat("Arial", isVisible());
        verifyThat("Cambria", isVisible());
        verifyThat("Roboto", isVisible());
        verifyThat("Candara", isVisible());
        verifyThat("Comic Sans MS", isVisible());
        verifyThat("Calibri", isVisible());
        verifyThat("Times New Roman", isVisible());
    }

    @Test
    void testExportSettingsPage(){
        clickOn("#exportSettingsButton");

        verifyThat("#exportLogTitle", isNotNull());
        verifyThat("#exportLogTitle", hasText("Export Operations Log"));
        verifyThat("#exportLogTitle", isVisible());

        verifyThat("#exportLogPDFButton", isVisible());
        verifyThat("#exportLogPDFButton", hasText("Export PDF"));
        verifyThat("#exportLogPDFButton", isEnabled());
        verifyThat("#exportLogPDFButton", node -> node instanceof Button);

        verifyThat("#exportLogCSVButton", isVisible());
        verifyThat("#exportLogCSVButton", hasText("Export CSV"));
        verifyThat("#exportLogCSVButton", isEnabled());
        verifyThat("#exportLogCSVButton", node -> node instanceof Button);
    }
}
