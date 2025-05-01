package com.university.grp20.logicTests;

import com.university.grp20.controller.HelpGuideController;
import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelpGuideControllerTest {
    @BeforeAll
    static void setupImageTestToolkit() {
        new JFXPanel();
    }

    @Test
    void testClickNextButtons() {
        HelpGuideController controller = new HelpGuideController();
        controller.setImageCarousel(new ImageView());

        List<Image> testImages = List.of(
                new Image("file:test1.png"),
                new Image("file:test2.png"),
                new Image("file:test3.png")
        );
        controller.setImageList(testImages);

        controller.handleNext();
        assertEquals(1, controller.getImageIndex());

        controller.handleNext();
        assertEquals(2, controller.getImageIndex());

        controller.handleNext();
        assertEquals(0, controller.getImageIndex());
    }

    @Test
    void testClickPrevButtons() {
        HelpGuideController controller = new HelpGuideController();
        controller.setImageCarousel(new ImageView());

        List<Image> testImages = List.of(
                new Image("file:test1.png"),
                new Image("file:test2.png"),
                new Image("file:test3.png")
        );
        controller.setImageList(testImages);

        controller.handlePrev();
        assertEquals(2, controller.getImageIndex());

        controller.handlePrev();
        assertEquals(1, controller.getImageIndex());

        controller.handlePrev();
        assertEquals(0, controller.getImageIndex());
    }
}
