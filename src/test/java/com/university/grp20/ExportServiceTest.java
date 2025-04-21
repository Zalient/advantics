package com.university.grp20;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.university.grp20.model.ExportService;
import com.university.grp20.model.MetricsDTO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

public class ExportServiceTest {

  @TempDir Path tempDir;

  @Test
  void testDashboardToPDF() throws IOException {

    MetricsDTO metricsDTO = new MetricsDTO(1000, 1000, 1, 1, 1, 1, 1, 1, 1, 1, 1);

    File file = tempDir.resolve("test.pdf").toFile();
    String filePath = file.getAbsolutePath();
    ExportService.dashboardToPDF(metricsDTO, filePath);

    assertTrue(file.exists(), "PDF file should be created");
  }

  @Test
  void testDashboardToCSV() throws IOException {
    MetricsDTO metricsDTO = new MetricsDTO(1000, 1000, 1, 1, 1, 1, 1, 1, 1, 1, 1);

    File file = tempDir.resolve("test.csv").toFile();
    String filePath = file.getAbsolutePath();
    ExportService.dashboardToCSV(metricsDTO, filePath);

    assertTrue(file.exists(), "CSV file should be created");
  }
}
