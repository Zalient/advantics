package com.university.grp20.logicTests;

import com.university.grp20.controller.HelpGuideController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javafx.embed.swing.JFXPanel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelpGuideControllerTest {
    @BeforeAll
    static void initToolkit() {
        new JFXPanel();
    }

    @Test
    void testHandleNextCyclesImages() {
        HelpGuideController controller = new HelpGuideController();
        controller.setImageCarousel(new ImageView());

        List<Image> mockImages = List.of(
                new Image("file:test1.png"),
                new Image("file:test2.png"),
                new Image("file:test3.png")
        );
        controller.setImageList(mockImages);

        controller.handleNext();
        assertEquals(1, controller.getImageIndex());

        controller.handleNext();
        assertEquals(2, controller.getImageIndex());

        controller.handleNext(); // wraps to 0
        assertEquals(0, controller.getImageIndex());
    }
}
