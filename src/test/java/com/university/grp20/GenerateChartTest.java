package com.university.grp20;

import static org.junit.jupiter.api.Assertions.*;

import com.university.grp20.model.FilterCriteriaDTO;
import com.university.grp20.model.GenerateChartService;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class GenerateChartTest {
    private FilterCriteriaDTO filterDTO;
    private final GenerateChartService generateChartService = new GenerateChartService();

    @BeforeEach
    void setUp() {
        filterDTO = new FilterCriteriaDTO();
        filterDTO.setIncomes(Collections.singletonList("Medium"));
        filterDTO.setContexts(Arrays.asList("Blog", "Shopping"));
        filterDTO.setTimeGranularity("Per Week");
    }

    @Test
    void testDefaultImpressionsChart() {
        JFreeChart chart = GenerateChartService.impressionsChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();

        assertNotNull(chart, "Default Impressions chart should not be null");
        assertEquals("Impressions Per Day", chart.getTitle().getText(), "Chart title should be Impressions Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("Impressions", yAxis.getLabel(), "Y-Axis should be Impressions");
    }

    @Test
    void testDefaultClicksChart() {
        JFreeChart chart = GenerateChartService.clicksChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default Clicks chart should not be null");
        assertEquals("Clicks Per Day", chart.getTitle().getText(), "Chart title should be Clicks Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("Clicks", yAxis.getLabel(), "Y-Axis should be Clicks");
    }

    @Test
    void testDefaultUniquesChart() {
        JFreeChart chart = GenerateChartService.uniquesChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default Uniques chart should not be null");
        assertEquals("Uniques Per Day", chart.getTitle().getText(), "Chart title should be Uniques Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("Uniques", yAxis.getLabel(), "Y-Axis should be Uniques");
    }

    @Test
    void testDefaultBouncesChart() {
        JFreeChart chart = GenerateChartService.bouncesChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default Bounces chart should not be null");
        assertEquals("Bounces Per Day", chart.getTitle().getText(), "Chart title should be Bounces Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("Bounces", yAxis.getLabel(), "Y-Axis should be Bounces");
    }

    @Test
    void testDefaultConversionsChart() {
        JFreeChart chart = GenerateChartService.conversionsChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default Conversions chart should not be null");
        assertEquals("Conversions Per Day", chart.getTitle().getText(), "Chart title should be Conversions Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("Conversions", yAxis.getLabel(), "Y-Axis should be Conversions");
    }

    @Test
    void testDefaultTotalCostChart() {
        JFreeChart chart = GenerateChartService.totalCostChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default Total Cost chart should not be null");
        assertEquals("Total Cost Per Day", chart.getTitle().getText(), "Chart title should be Total Cost Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("Total Cost", yAxis.getLabel(), "Y-Axis should be Total Cost");
    }

    @Test
    void testDefaultCTRChart() {
        JFreeChart chart = GenerateChartService.ctrChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default CTR chart should not be null");
        assertEquals("CTR Per Day", chart.getTitle().getText(), "Chart title should be CTR Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("CTR", yAxis.getLabel(), "Y-Axis should be CTR");
    }

    @Test
    void testDefaultCPAChart() {
        JFreeChart chart = GenerateChartService.cpaChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default CPA chart should not be null");
        assertEquals("CPA Per Day", chart.getTitle().getText(), "Chart title should be CPA Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("CPA", yAxis.getLabel(), "Y-Axis should be CPA");
    }

    @Test
    void testDefaultCPCChart() {
        JFreeChart chart = GenerateChartService.cpcChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default CPC chart should not be null");
        assertEquals("CPC Per Day", chart.getTitle().getText(), "Chart title should be CPC Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("CPC", yAxis.getLabel(), "Y-Axis should be CPC");
    }

    @Test
    void testDefaultCPMChart() {
        JFreeChart chart = GenerateChartService.cpmChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default CPM chart should not be null");
        assertEquals("CPM Per Day", chart.getTitle().getText(), "Chart title should be CPM Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("CPM", yAxis.getLabel(), "Y-Axis should be CPM");
    }

    @Test
    void testDefaultBounceRateChart() {
        JFreeChart chart = GenerateChartService.bounceRateChart();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Default Bounce Rate chart should not be null");
        assertEquals("Bounce Rate Per Day", chart.getTitle().getText(), "Chart title should be Bounce Rate Per Day");
        assertEquals("Day", xAxis.getLabel(), "X-Axis should be Day");
        assertEquals("Bounce Rate", yAxis.getLabel(), "Y-Axis should be Bounce Rate");
    }

    @Test
    void testDefaultClickCostHistogram() {
        JFreeChart chart = GenerateChartService.clickCostHistogram(10);

        assertNotNull(chart, "Click Cost Histogram chart should not be null");
    }

    @Test
    void testFilteredImpressionsChart() {
        JFreeChart chart = generateChartService.filteredImpressionsChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered Impressions chart should not be null");
        assertEquals("Impressions Per Week", chart.getTitle().getText(), "Chart title should be Impressions Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("Impressions", yAxis.getLabel(), "Y-Axis should be Impressions");
    }

    @Test
    void testFilteredClicksChart() {
        JFreeChart chart = generateChartService.filteredClicksChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered Clicks chart should not be null");
        assertEquals("Clicks Per Week", chart.getTitle().getText(), "Chart title should be Clicks Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("Clicks", yAxis.getLabel(), "Y-Axis should be Clicks");
    }

    @Test
    void testFilteredUniquesChart() {
        JFreeChart chart = generateChartService.filteredUniquesChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered Uniques chart should not be null");
        assertEquals("Uniques Per Week", chart.getTitle().getText(), "Chart title should be Uniques Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("Uniques", yAxis.getLabel(), "Y-Axis should be Uniques");
    }

    @Test
    void testFilteredBouncesChart() {
        JFreeChart chart = generateChartService.filteredBouncesChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered Bounces chart should not be null");
        assertEquals("Bounces Per Week", chart.getTitle().getText(), "Chart title should be Bounces Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("Bounces", yAxis.getLabel(), "Y-Axis should be Bounces");
    }

    @Test
    void testFilteredConversionsChart() {
        JFreeChart chart = generateChartService.filteredConversionsChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered Conversions chart should not be null");
        assertEquals("Conversions Per Week", chart.getTitle().getText(), "Chart title should be Conversions Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("Conversions", yAxis.getLabel(), "Y-Axis should be Conversions");
    }

    @Test
    void testFilteredTotalCostChart() {
        JFreeChart chart = generateChartService.filteredTotalCostChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered Total Cost chart should not be null");
        assertEquals("Total Cost Per Week", chart.getTitle().getText(), "Chart title should be Total Cost Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("Total Cost", yAxis.getLabel(), "Y-Axis should be Total Cost");
    }

    @Test
    void testFilteredCTRChart() {
        JFreeChart chart = generateChartService.filteredCTRChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered CTR chart should not be null");
        assertEquals("CTR Per Week", chart.getTitle().getText(), "Chart title should be CTR Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("CTR", yAxis.getLabel(), "Y-Axis should be CTR");
    }

    @Test
    void testFilteredCPAChart() {
        JFreeChart chart = generateChartService.filteredCPAChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered CPA chart should not be null");
        assertEquals("CPA Per Week", chart.getTitle().getText(), "Chart title should be CPA Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("CPA", yAxis.getLabel(), "Y-Axis should be CPA");
    }

    @Test
    void testFilteredCPCChart() {
        JFreeChart chart = generateChartService.filteredCPCChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered CPC chart should not be null");
        assertEquals("CPC Per Week", chart.getTitle().getText(), "Chart title should be CPC Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("CPC", yAxis.getLabel(), "Y-Axis should be CPC");
    }

    @Test
    void testFilteredCPMChart() {
        JFreeChart chart = generateChartService.filteredCPMChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered CPM chart should not be null");
        assertEquals("CPM Per Week", chart.getTitle().getText(), "Chart title should be CPM Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("CPM", yAxis.getLabel(), "Y-Axis should be CPM");
    }

    @Test
    void testFilteredBounceRateChart() {
        JFreeChart chart = generateChartService.filteredBounceRateChart(filterDTO);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        assertNotNull(chart, "Filtered Bounce Rate chart should not be null");
        assertEquals("Bounce Rate Per Week", chart.getTitle().getText(), "Chart title should be Bounce Rate Per Week");
        assertEquals("Week", xAxis.getLabel(), "X-Axis should be Week");
        assertEquals("Bounce Rate", yAxis.getLabel(), "Y-Axis should be Bounce Rate");
    }
}
