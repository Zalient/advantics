package com.university.grp20.flowTests;

import com.university.grp20.UIManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class FilterSelectionModalTest extends ApplicationTest {
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
    void testNormalFilterSelection(){
        clickOn("#maleRadioButton");
        clickOn("#ageGroupSelector");
        clickOn("<25");
        clickOn("#incomeSelector");
        clickOn("High");
        clickOn("#contextSelector");
        clickOn("Shopping");
        clickOn("#granularityChooser");
        clickOn("Per Day");
        clickOn("#dayOfWeekSelector");
        clickOn("Monday");
    }

    @Test
    void testNormalFilterSelection2(){
        clickOn("#femaleRadioButton");
        clickOn("#ageGroupSelector");
        clickOn("<25");
        clickOn("#incomeSelector");
        clickOn("High");
        clickOn("#contextSelector");
        clickOn("Blog");
        clickOn("#granularityChooser");
        clickOn("Per Hour");
        clickOn("#timeOfDaySelector");
        clickOn("Morning 06:00 - 11:59");
    }

    @Test
    void testAllAgeRangesSelection(){
        clickOn("#ageGroupSelector");
        clickOn("<25");
        clickOn("25-34");
        clickOn("35-44");
        clickOn("45-54");
        clickOn(">54");
    }

    @Test
    void testAllIncomesSelection(){
        clickOn("#incomeSelector");
        clickOn("High");
        clickOn("Medium");
        clickOn("Low");
    }

    @Test
    void testAllContextsSelection(){
        clickOn("#contextSelector");
        clickOn("Blog");
        clickOn("News");
        clickOn("Shopping");
        clickOn("Hobbies");
        clickOn("Travel");
        clickOn("Social Media");
    }

    @Test
    void testAllDaysOfWeekSelection(){
        clickOn("#granularityChooser");
        clickOn("Per Day");
        clickOn("#dayOfWeekSelector");
        clickOn("Monday");
        clickOn("Tuesday");
        clickOn("Wednesday");
        clickOn("Thursday");
        clickOn("Friday");
        clickOn("Saturday");
        clickOn("Sunday");
    }
}