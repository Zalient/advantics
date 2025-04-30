package com.university.grp20.guiTests;

import com.university.grp20.UIManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.apache.logging.log4j.core.async.AsyncLoggerContextSelector.isSelected;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class FilterPaneTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        UIManager.setMainStage(stage);

        FXMLLoader loader = UIManager.createFxmlLoader("/fxml/FilterSelectionPopup.fxml");
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Filter Test Window");
        stage.show();
    }


    @Test
    void testFilterTitleExist(){
        verifyThat("#chartNameLabel", isNotNull());
        verifyThat("#chartNameLabel", hasText("Filtering Mode"));
        verifyThat("#chartNameLabel", isVisible());
    }

    @Test
    void testHelpButtonExist(){
        verifyThat("#helpButton", isVisible());
        verifyThat("#helpButton", hasText("Click Me!"));
        verifyThat("#helpButton", isEnabled());
        verifyThat("#helpButton", node -> node instanceof Button);
    }

    @Test
    void testDateTitlesExist(){
        verifyThat("#startDateTitle", isNotNull());
        verifyThat("#startDateTitle", hasText("Start Date"));
        verifyThat("#startDateTitle", isVisible());

        verifyThat("#endDateTitle", isNotNull());
        verifyThat("#endDateTitle", hasText("End Date"));
        verifyThat("#endDateTitle", isVisible());
    }

    @Test
    void testStartDatePickerExist(){
        verifyThat("#startDatePicker", isVisible());
        verifyThat("#startDatePicker", isEnabled());
    }

    @Test
    void testEndDatePickerExist(){
        verifyThat("#endDatePicker", isVisible());
        verifyThat("#endDatePicker", isEnabled());
    }

    @Test
    void testGenderPickerExist() {
        verifyThat("#maleRadioButton", isVisible());
        verifyThat("#femaleRadioButton", isVisible());

        verifyThat("#maleRadioButton", hasText("Male"));
        verifyThat("#femaleRadioButton", hasText("Female"));

        clickOn("#maleRadioButton");

        RadioButton maleRadioButton = lookup("#maleRadioButton").queryAs(RadioButton.class);
        RadioButton femaleRadioButton = lookup("#femaleRadioButton").queryAs(RadioButton.class);

        assertTrue(maleRadioButton.isSelected());
        assertFalse(femaleRadioButton.isSelected());

        clickOn("#femaleRadioButton");

        assertTrue(femaleRadioButton.isSelected());
        assertFalse(maleRadioButton.isSelected());
    }


    @Test
    void testAgeGroupDropdownExist(){
        clickOn("#ageGroupSelector");

        verifyThat("<25", isVisible());
        verifyThat("25-34", isVisible());
        verifyThat("35-44", isVisible());
        verifyThat("45-54", isVisible());
        verifyThat(">54", isVisible());
    }

    @Test
    void testIncomeDropdownExist(){
        clickOn("#incomeSelector");

        verifyThat("Low", isVisible());
        verifyThat("Medium", isVisible());
        verifyThat("High", isVisible());
    }

    @Test
    void testContextDropdownExist(){
        clickOn("#contextSelector");

        verifyThat("News", isVisible());
        verifyThat("Shopping", isVisible());
        verifyThat("Social Media", isVisible());
        verifyThat("Blog", isVisible());
        verifyThat("Hobbies", isVisible());
        verifyThat("Travel", isVisible());
    }

    @Test
    void testTimeGranularityDropdownExist(){
        clickOn("#granularityChooser");

        verifyThat("Per Hour", isVisible());
        verifyThat("Per Day", isVisible());
        verifyThat("Per Week", isVisible());
    }

    @Test
    void testPerDayOfWeekDropdownExist(){
        clickOn("#granularityChooser");
        clickOn("Per Day");

        clickOn("#dayOfWeekSelector");
        verifyThat("Monday", isVisible());
        verifyThat("Tuesday", isVisible());
        verifyThat("Wednesday", isVisible());
        verifyThat("Thursday", isVisible());
        verifyThat("Friday", isVisible());
        verifyThat("Saturday", isVisible());
        verifyThat("Sunday", isVisible());
    }

    @Test
    void testPerTimeOfDayDropdownExist(){
        clickOn("#granularityChooser");
        clickOn("Per Hour");

        clickOn("#timeOfDaySelector");
        verifyThat("Morning 06:00 - 11:59", isVisible());
        verifyThat("Afternoon 12:00 - 17:59", isVisible());
        verifyThat("Night 18:00 - 05:59", isVisible());
        verifyThat("None", isVisible());
    }

    @Test
    void testApplyChangesButtonExist(){
        verifyThat("#applyChangesButton", isVisible());
        verifyThat("#applyChangesButton", hasText("Apply Changes"));
        verifyThat("#applyChangesButton", isEnabled());
        verifyThat("#applyChangesButton", node -> node instanceof Button);
    }

    @Test
    void testProgressBarExist(){
        verifyThat("#filterProgressBar", isVisible());
    }
}
