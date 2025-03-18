package com.university.grp20;

import com.university.grp20.model.ExportService;
import com.university.grp20.model.MetricsDTO;
import javafx.scene.shape.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

public class ExportServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void testDashboardToPDF() throws IOException {
        MetricsDTO metricsDTO = new MetricsDTO();
        metricsDTO.setImpressions(1000);
        metricsDTO.setClicks(1000);
        metricsDTO.setConversions(1);
        metricsDTO.setBounces(1);
        metricsDTO.setUniques(1);
        metricsDTO.setTotalCost(1);
        metricsDTO.setCpa(1);
        metricsDTO.setCpm(1);
        metricsDTO.setCpc(1);
        metricsDTO.setCtr(1);
        metricsDTO.setBounceRate(1);

    }
}
