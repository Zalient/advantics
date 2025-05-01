package com.university.grp20.logicTests;

import com.university.grp20.model.*;
import org.jfree.chart.JFreeChart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportServiceTest {


    @TempDir
    Path tempDir;

    @Test
    void testDashboardToPDF() throws IOException {
        MetricsDTO metricsDTO = new MetricsDTO(1000,1000,1,1,1,1,1,1,1,1,1);
        File file = tempDir.resolve("test.pdf").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.dashboardToPDF(metricsDTO, filePath);

        assertTrue(file.exists(), "Dashboard to PDF failed");
    }

    @Test
    void testDashboardToCSV() throws IOException {
        MetricsDTO metricsDTO = new MetricsDTO(1000,1000,1,1,1,1,1,1,1,1,1);
        File file = tempDir.resolve("test.csv").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.dashboardToCSV(metricsDTO, filePath);

        assertTrue(file.exists(), "Dashboard to CSV failed");
    }

    @Test
    void testExportChartToPDF() throws IOException, SQLException {
        DBHelper.useTestConnection(DriverManager.getConnection("jdbc:sqlite:test_dataset.db"));
        JFreeChart chart = GenerateChartService.impressionsChart();

        File file = tempDir.resolve("test.pdf").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToPDF(chart, filePath);

        assertTrue(file.exists(), "Chart to PDF failed");
    }

    @Test
    void testExportChartToCSV() throws IOException, SQLException {
        DBHelper.useTestConnection(DriverManager.getConnection("jdbc:sqlite:test_dataset.db"));
        JFreeChart chart = GenerateChartService.impressionsChart();

        File file = tempDir.resolve("test.csv").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToCSV(chart, filePath);

        assertTrue(file.exists(), "Chart to CSV failed");
    }

    @Test
    void testExportFilteredChartToPDF() throws IOException {
        GenerateChartService generateChartService = new GenerateChartService("test_dataset");
        FilterCriteriaDTO filterCriteriaDTO = new FilterCriteriaDTO(null, null, null, null, null, null, null, null, null);
        JFreeChart chart = generateChartService.filteredImpressionsChart(filterCriteriaDTO);

        File file = tempDir.resolve("test.pdf").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToPDF(chart, filePath);

        assertTrue(file.exists(), "Filtered Chart to PDF failed");
    }

    @Test
    void testExportFilteredChartToCSV() throws IOException {
        GenerateChartService generateChartService = new GenerateChartService("test_dataset");
        FilterCriteriaDTO filterCriteriaDTO = new FilterCriteriaDTO(null, null, null, null, null, null, null, null, null);
        JFreeChart chart = generateChartService.filteredImpressionsChart(filterCriteriaDTO);

        File file = tempDir.resolve("test.csv").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToCSV(chart, filePath);

        assertTrue(file.exists(), "Filtered Chart to CSV failed");
    }

    @Test
    void testHistogramToPDF() throws IOException, SQLException {
        DBHelper.useTestConnection(DriverManager.getConnection("jdbc:sqlite:test_dataset.db"));
        JFreeChart histogram = GenerateChartService.clickCostHistogram(14);

        File file = tempDir.resolve("test.pdf").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToPDF(histogram, filePath);

        assertTrue(file.exists(), "Histogram to PDF failed");
    }

    @Test
    void testHistogramToCSV() throws IOException, SQLException {
        DBHelper.useTestConnection(DriverManager.getConnection("jdbc:sqlite:test_dataset.db"));
        JFreeChart histogram = GenerateChartService.clickCostHistogram(14);

        File file = tempDir.resolve("test.csv").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToCSV(histogram, filePath);

        assertTrue(file.exists(), "Histogram to CSV failed");
    }

    @Test
    void testExportLogToPDF() throws IOException {
        File logFile = tempDir.resolve("log.txt").toFile();
        try (FileWriter writer = new FileWriter(logFile)) {
            writer.write("[2025-03-20 10:00:00] User logged in\n");
            writer.write("[2025-03-20 10:05:00] User viewed dashboard\n");
        }
        File pdfFile = tempDir.resolve("log.pdf").toFile();
        String pdfFilePath = pdfFile.getAbsolutePath();
        ExportService.exportLogToPDF(logFile.getAbsolutePath(), pdfFilePath);
        assertTrue(pdfFile.exists(), "PDF file should be created");
    }

    @Test
    void testExportLogToCSV() throws IOException {
        File logFile = tempDir.resolve("log.txt").toFile();
        try (FileWriter writer = new FileWriter(logFile)) {
            writer.write("[2025-03-20 10:00:00] User logged in\n");
            writer.write("[2025-03-20 10:05:00] User viewed dashboard\n");
        }
        File csvFile = tempDir.resolve("log.csv").toFile();
        String csvFilePath = csvFile.getAbsolutePath();
        ExportService.exportLogToCSV(logFile.getAbsolutePath(), csvFilePath);
        assertTrue(csvFile.exists(), "CSV file should be created");
    }

    @Test
    void testExportEmptyLogToPDF() throws IOException {
        File logFile = tempDir.resolve("empty_log.txt").toFile();
        logFile.createNewFile();
        File pdfFile = tempDir.resolve("empty_log.pdf").toFile();
        String pdfFilePath = pdfFile.getAbsolutePath();
        ExportService.exportLogToPDF(logFile.getAbsolutePath(), pdfFilePath);
        assertTrue(pdfFile.exists(), "PDF file should be created even for an empty log file");
    }

    @Test
    void testExportEmptyLogToCSV() throws IOException {
        File logFile = tempDir.resolve("empty_log.txt").toFile();
        logFile.createNewFile();
        File csvFile = tempDir.resolve("empty_log.csv").toFile();
        String csvFilePath = csvFile.getAbsolutePath();
        ExportService.exportLogToCSV(logFile.getAbsolutePath(), csvFilePath);
        assertTrue(csvFile.exists(), "CSV file should be created even for an empty log file");
    }
}