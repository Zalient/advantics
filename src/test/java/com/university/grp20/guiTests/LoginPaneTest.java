package com.university.grp20.guiTests;

import com.university.grp20.UIManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class LoginPaneTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        UIManager.setMainStage(stage);
        FXMLLoader loader = UIManager.createFxmlLoader("/fxml/LoginPane.fxml");
        UIManager.setController(loader.getController());
        UIManager.showMainStage("Login", loader);
    }

    @Test
    void checkLoginPaneTitleExist(){
        verifyThat("#loginTitle", isNotNull());
        verifyThat("#loginTitle", hasText("Login"));
        verifyThat("#loginTitle", isVisible());
    }

    @Test
    void checkHelpButtonExist(){
        verifyThat("#helpButton", isVisible());
        verifyThat("#helpButton", hasText("Click Me!"));
        verifyThat("#helpButton", isEnabled());
        verifyThat("#helpButton", node -> node instanceof Button);
    }

    @Test
    void checkUsernameLabelExist(){
        verifyThat("#usernameLabel", isNotNull());
        verifyThat("#usernameLabel", hasText("Username:"));
        verifyThat("#usernameLabel", isVisible());
    }

    @Test
    void checkUsernameTextfieldExist(){
        verifyThat("#usernameInputBox", isVisible());
        verifyThat("#usernameInputBox", isEnabled());
        verifyThat("#usernameInputBox", (TextField tf) -> tf.getText().isEmpty());
        verifyThat("#usernameInputBox", node -> node instanceof TextField);
    }

    @Test
    void checkPasswordLabelExist(){
        verifyThat("#passwordLabel", isNotNull());
        verifyThat("#passwordLabel", hasText("Password:"));
        verifyThat("#passwordLabel", isVisible());
    }

    @Test
    void checkPasswordTextfieldExist(){
        verifyThat("#passwordInputBox", isVisible());
        verifyThat("#passwordInputBox", isEnabled());
        verifyThat("#passwordInputBox", (TextField tf) -> tf.getText().isEmpty());
        verifyThat("#passwordInputBox", node -> node instanceof TextField);
    }

    @Test
    void checkLoginButtonExist(){
        verifyThat("#loginButton", isVisible());
        verifyThat("#loginButton", hasText("Login"));
        verifyThat("#loginButton", isEnabled());
        verifyThat("#loginButton", node -> node instanceof Button);
    }
}
