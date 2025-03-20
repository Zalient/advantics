package com.university.grp20;

import com.university.grp20.model.ExportService;
import com.university.grp20.model.FilterCriteriaDTO;
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

        assertTrue(file.exists(), "Dashboard to PDF failed");
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

        assertTrue(file.exists(), "Dashboard to CSV failed");
    }

    @Test
    void testExportChartToPDF() throws IOException {
        GenerateChartService generateChartService = new GenerateChartService();
        JFreeChart chart = generateChartService.impressionsChart();

        File file = tempDir.resolve("test.pdf").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToPDF(chart, filePath);

        assertTrue(file.exists(), "Chart to PDF failed");
    }

    @Test
    void testExportChartToCSV() throws IOException {
        GenerateChartService generateChartService = new GenerateChartService();
        JFreeChart chart = generateChartService.impressionsChart();

        File file = tempDir.resolve("test.csv").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToCSV(chart, filePath);

        assertTrue(file.exists(), "Chart to CSV failed");
    }

    @Test
    void testExportFilteredChartToPDF() throws IOException {
        GenerateChartService generateChartService = new GenerateChartService();
        FilterCriteriaDTO filterCriteriaDTO = new FilterCriteriaDTO();
        JFreeChart chart = generateChartService.filteredImpressionsChart(filterCriteriaDTO);

        File file = tempDir.resolve("test.pdf").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToPDF(chart, filePath);

        assertTrue(file.exists(), "Filtered Chart to PDF failed");
    }

    @Test
    void testExportFilteredChartToCSV() throws IOException {
        GenerateChartService generateChartService = new GenerateChartService();
        FilterCriteriaDTO filterCriteriaDTO = new FilterCriteriaDTO();
        JFreeChart chart = generateChartService.filteredImpressionsChart(filterCriteriaDTO);

        File file = tempDir.resolve("test.csv").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToCSV(chart, filePath);

        assertTrue(file.exists(), "Filtered Chart to CSV failed");
    }

    @Test
    void testHistogramToPDF() throws IOException {
        GenerateChartService generateChartService = new GenerateChartService();
        JFreeChart histogram = generateChartService.clickCostHistogram(14);

        File file = tempDir.resolve("test.pdf").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToPDF(histogram, filePath);

        assertTrue(file.exists(), "Histogram to PDF failed");
    }

    @Test
    void testHistogramToCSV() throws IOException {
        GenerateChartService generateChartService = new GenerateChartService();
        JFreeChart histogram = generateChartService.clickCostHistogram(14);

        File file = tempDir.resolve("test.csv").toFile();
        String filePath = file.getAbsolutePath();
        ExportService.chartToCSV(histogram, filePath);

        assertTrue(file.exists(), "Histogram to CSV failed");
    }
}