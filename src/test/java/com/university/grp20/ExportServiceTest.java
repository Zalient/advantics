package com.university.grp20;

import com.university.grp20.model.ExportService;
import com.university.grp20.model.GenerateChartService;
import com.university.grp20.model.MetricsDTO;
import org.jfree.chart.JFreeChart;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

        File file = tempDir.resolve("test.pdf").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.dashboardToPDF(metricsDTO, filePath);

        assertTrue(file.exists(), "PDF file should be created");
    }

    @Test
    void testDashboardToCSV() throws IOException {

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

        File file = tempDir.resolve("test.csv").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.dashboardToCSV(metricsDTO, filePath);

        assertTrue(file.exists(), "CSV file should be created");
    }



}
