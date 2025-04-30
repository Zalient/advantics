package com.university.grp20.flowTests;

import com.university.grp20.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javax.management.OperationsException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class SettingsFlowTest extends ApplicationTest {
    private Connection testConn;
    @Override
    public void start(Stage stage) throws Exception {
        User.setRole("Admin");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings/SettingsPane.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        OperationLogger operationLogger = new OperationLogger();
        operationLogger.initialize();
    }

    @BeforeEach
    void setUp() throws Exception {
        LoginService.useTestDB("test_users.db");
        testConn = DriverManager.getConnection("jdbc:sqlite:test_dataset.db");
        DBHelper.useTestConnection(testConn);

        LoginService.useTestDB("test_users.db");

        try (Connection userConn = DriverManager.getConnection("jdbc:sqlite:test_users.db")) {
            userConn.createStatement().execute("DROP TABLE IF EXISTS users");
            userConn.createStatement().execute(
                    "CREATE TABLE users (" +
                            "username TEXT PRIMARY KEY, " +
                            "password TEXT, " +
                            "salt TEXT, " +
                            "role TEXT)");

            LoginService loginService = new LoginService();

            insertUser(userConn, loginService, "Admin", "1", "Admin");
            insertUser(userConn, loginService, "Editor", "2", "Editor");
            insertUser(userConn, loginService, "Viewer", "3", "Viewer");
        }
    }

    private void insertUser(Connection conn, LoginService loginService, String username, String plainPassword, String role) throws SQLException, SQLException {
        String[] hashed = loginService.hashPassword(plainPassword);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, salt, role) VALUES (?, ?, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, hashed[0]);
        stmt.setString(3, hashed[1]);
        stmt.setString(4, role);
        stmt.executeUpdate();
    }

    @AfterEach
    void tearDown() throws Exception {
        DBHelper.resetTestConnection();
        if (testConn != null) {
            testConn.close();
        }
        GlobalSettingsStorage globalSettings = GlobalSettingsStorage.getInstance();
        globalSettings.setBounceType("Pages Viewed");
        globalSettings.setBounceValue("1");
    }

    @Test
    void testNormalBounceDefUpdate(){
        clickOn("#metricSettingsButton");
        clickOn("#pagesViewedButton");
        clickOn("#bounceValField").write("10");
        clickOn("#applyBounceButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Bounce successfully redefined"));

        clickOn("OK");
        verifyThat("#bounceDefLabel", hasText("Current Setting: Pages Viewed = 10"));
    }

    @Test
    void testNoValBounceDefUpdate(){
        clickOn("#metricSettingsButton");
        clickOn("#pagesViewedButton");
        clickOn("#applyBounceButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Empty input, please enter a value"));
    }

    @Test
    void testNegativeBounceDefUpdate(){
        clickOn("#metricSettingsButton");
        clickOn("#pagesViewedButton");
        clickOn("#bounceValField").write("-10");
        clickOn("#applyBounceButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Input is a negative number, please enter positive integer"));
    }

    @Test
    void testNotIntBounceDefUpdate(){
        clickOn("#metricSettingsButton");
        clickOn("#pagesViewedButton");
        clickOn("#bounceValField").write("NotAnInt");
        clickOn("#applyBounceButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Input is not an integer, wrong type"));
    }

    @Test
    void testNormalAddUser(){
        clickOn("#userSettingsButton");

        clickOn("#newUsernameField").write("testNewUser");
        clickOn("#newPasswordField").write("testNewPass");
        clickOn("#newRoleMenu").clickOn("Viewer");
        clickOn("#addUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("New user \"testNewUser\" was successfully added to the database"));

        clickOn("OK");

        clickOn("#currentUsersMenu");
        verifyThat("testNewUser", isVisible());
    }

    @Test
    void testDuplicateAddUser(){
        clickOn("#userSettingsButton");

        clickOn("#newUsernameField").write("testNewUser");
        clickOn("#newPasswordField").write("testNewPass");
        clickOn("#newRoleMenu").clickOn("Viewer");
        clickOn("#addUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("New user \"testNewUser\" was successfully added to the database"));

        clickOn("OK");

        clickOn("#currentUsersMenu");
        verifyThat("testNewUser", isVisible());

        clickOn("#newUsernameField").eraseText(20).write("testNewUser");
        clickOn("#newPasswordField").eraseText(20).write("testNewPass");
        interact(() -> lookup("#newRoleMenu").queryComboBox().setValue("Viewer"));
        
        clickOn("#addUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Username testNewUser already in use"));
    }

    @Test
    void testMissingUsernameAddUser(){
        clickOn("#userSettingsButton");

        clickOn("#newPasswordField").write("testAdmin");
        clickOn("#newRoleMenu").clickOn("Admin");
        clickOn("#addUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Some user detail fields are empty, please check again"));
    }

    @Test
    void testMissingPasswordAddUser(){
        clickOn("#userSettingsButton");

        clickOn("#newUsernameField").write("testAdmin");
        clickOn("#newRoleMenu").clickOn("Admin");
        clickOn("#addUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Some user detail fields are empty, please check again"));
    }

    @Test
    void testMissingRoleAddUser(){
        clickOn("#userSettingsButton");

        clickOn("#newPasswordField").write("testAdmin");
        clickOn("#newRoleMenu").clickOn("Admin");
        clickOn("#addUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Some user detail fields are empty, please check again"));
    }

    @Test
    void testNormalEditUser(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        clickOn("#currentUsersMenu").clickOn("Admin");;
        clickOn("#changePasswordField").write("newPass");
        clickOn("#changeRoleMenu").clickOn("Editor");
        clickOn("#editUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Password updated for user: Admin"));
        clickOn("OK");
        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Role updated to Editor for user: Admin"));
        clickOn("OK");
    }

    @Test
    void testMissingUserEditUser(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        clickOn("#changePasswordField").write("newPass");
        clickOn("#changeRoleMenu").clickOn("Editor");
        clickOn("#editUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("User not selected, please select a user to edit"));
        clickOn("OK");
    }

    @Test
    void testOnlyPasswordEditUser(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        clickOn("#currentUsersMenu").clickOn("Admin");;
        clickOn("#changePasswordField").write("newPass");
        clickOn("#editUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Password updated for user: Admin"));
        clickOn("OK");
    }

    @Test
    void testOnlyRoleEditUser(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        clickOn("#currentUsersMenu").clickOn("Admin");;
        clickOn("#changeRoleMenu").clickOn("Editor");
        clickOn("#editUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Role updated to Editor for user: Admin"));
        clickOn("OK");
    }

    @Test
    void testMissingPasswordAndRole(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        clickOn("#currentUsersMenu").clickOn("Admin");;
        clickOn("#editUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("Password field is empty and no new role is selected"));
        clickOn("OK");
    }

    @Test
    void testMissingAllFields(){
        clickOn("#userSettingsButton");
        ScrollPane scrollPane = lookup("#userScroll").query();
        interact(() -> scrollPane.setVvalue(1.0));

        clickOn("#editUserButton");

        verifyThat(".alert", isVisible());
        verifyThat(".alert .content", hasText("User not selected, please select a user to edit"));
        clickOn("OK");
    }

    //test for export settings
    //test directory/path exists when clicked
}
