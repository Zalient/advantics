package com.university.grp20.flowTests;

import com.university.grp20.UIManager;
import com.university.grp20.controller.LoginController;
import com.university.grp20.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginFlowTest extends ApplicationTest {
    private LoginController loginController;

    @Override
    public void start(Stage stage) throws Exception {
        UIManager.setMainStage(stage);
        FXMLLoader loader = UIManager.createFxmlLoader("/fxml/LoginPane.fxml");
        UIManager.setController(loader.getController());
        UIManager.showMainStage("Login", loader);
        loginController = loader.getController();
    }

    @Test
    void validLoginTest() {
        String username = "Admin";
        String password = "1";

        clickOn("#usernameInputBox").write(username);
        clickOn("#passwordInputBox").write(password);
        clickOn("#loginButton");

        assertEquals(username, User.getUsername(), "The user has successfully logged in");
    }

    @Test
    void adminLoginTest() {
        String username = "Admin";
        String password = "1";

        clickOn("#usernameInputBox").write(username);
        clickOn("#passwordInputBox").write(password);
        clickOn("#loginButton");

        assertEquals("Admin", User.getRole(), "Role is successfully identified as admin");
    }

    @Test
    void editorLoginTest() {
        String username = "Editor";
        String password = "2";

        clickOn("#usernameInputBox").write(username);
        clickOn("#passwordInputBox").write(password);
        clickOn("#loginButton");

        assertEquals("Editor", User.getRole(), "Role is successfully identified as editor");
    }

    @Test
    void viewerLoginTest() {
        String username = "Viewer";
        String password = "3";

        clickOn("#usernameInputBox").write(username);
        clickOn("#passwordInputBox").write(password);
        clickOn("#loginButton");

        assertEquals("Viewer", User.getRole(), "Role is successfully identified as viewer");
    }

    @Test
    void pageShowsEnteredValuesTest() {
        String username = "User";
        String password = "user";

        clickOn("#usernameInputBox").write(username);
        clickOn("#passwordInputBox").write(password);


    }

    @Test
    void invalidUsername() throws InterruptedException {
        String username = "Fake";
        String password = "Password";

        clickOn("#usernameInputBox").write(username);
        clickOn("#passwordInputBox").write(password);
        clickOn("#loginButton");

        assertEquals("Missing", loginController.getLoginStatus());

        Node dialogPane = lookup(".dialog-pane").query();
        Text dialogText = from(dialogPane).lookup((Text t) -> t.getText().startsWith("That username doesn't exist in the database. Please contact an administrator.")).query();

        assertNotNull(dialogText, "Error alert should tell the user they haven't entered a valid password");

    }

    @Test
    void invalidPassword() throws InterruptedException {
        String username = "Admin";
        String password = "WrongPassword";

        clickOn("#usernameInputBox").write(username);
        clickOn("#passwordInputBox").write(password);
        clickOn("#loginButton");

        assertEquals("Invalid", loginController.getLoginStatus());

        Node dialogPane = lookup(".dialog-pane").query();
        Text dialogText = from(dialogPane).lookup((Text t) -> t.getText().startsWith("Your password was invalid.")).query();

        assertNotNull(dialogText, "Error alert should tell the user they haven't entered a correct password");
    }

    @Test
    void noInput() throws InterruptedException {

        clickOn("#loginButton");

        Node dialogPane = lookup(".dialog-pane").query();
        Text dialogText = from(dialogPane).lookup((Text t) -> t.getText().startsWith("You have not entered a username or password.")).query();

        assertNotNull(dialogText, "Error alert should tell the user they haven't entered a username or password");
    }

    @Test
    void noUsernameInput() throws InterruptedException {
        String password = "WrongPassword";
        clickOn("#passwordInputBox").write(password);

        clickOn("#loginButton");

        Node dialogPane = lookup(".dialog-pane").query();
        Text dialogText = from(dialogPane).lookup((Text t) -> t.getText().startsWith("You have not entered a username.")).query();

        assertNotNull(dialogText, "Error alert should tell the user they haven't entered a username");
    }

    @Test
    void noPasswordInput() throws InterruptedException {
        String username = "Admin";
        clickOn("#usernameInputBox").write(username);

        clickOn("#loginButton");

        Node dialogPane = lookup(".dialog-pane").query();
        Text dialogText = from(dialogPane).lookup((Text t) -> t.getText().startsWith("You have not entered a password.")).query();

        assertNotNull(dialogText, "Error alert should tell the user they haven't entered a password");
    }


}