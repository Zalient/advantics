package com.university.grp20.logicTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.university.grp20.controller.ProgressBarListener;
import com.university.grp20.controller.ProgressLabel;
import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.university.grp20.model.CalculateMetricsService;
import com.university.grp20.model.FileImportService;
import com.university.grp20.model.FilterCriteriaDTO;
import com.university.grp20.model.MetricsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CalculateMetricsServiceTest {
    private CalculateMetricsService calculateMetricsService;
    private FileImportService fileImportService;
    private MetricsDTO metricsDTO;
    private FilterCriteriaDTO filterCriteriaDTO;
    private final ProgressLabel progressLabelMock = mock(ProgressLabel.class);
    private final ProgressBarListener progressBarListenerMock = mock(ProgressBarListener.class);

    @BeforeEach
    public void setUp() {
        calculateMetricsService = new CalculateMetricsService("testCampaign");
        calculateMetricsService.setOnFilterLabelStart(progressLabelMock);
        calculateMetricsService.setOnFilterStart(progressBarListenerMock);

        fileImportService = new FileImportService();
        fileImportService.setOnUploadLabelStart(progressLabelMock);
        fileImportService.setOnUploadStart(progressBarListenerMock);

        doNothing().when(progressLabelMock).labelText(anyString());
        doNothing().when(progressBarListenerMock).progressBar(anyDouble());
    }

    @Test
    public void testEmptyLogs() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/empty_impression_log.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/empty_click_log.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/empty_server_log.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(0.0, metricsDTO.impressions());
        assertEquals(0.0, metricsDTO.clicks());
        assertEquals(0.0, metricsDTO.uniques());
        assertEquals(0.0, metricsDTO.bounces());
        assertEquals(0.0, metricsDTO.conversions());
        assertEquals(0.0, metricsDTO.totalCost());
        assertEquals(0.0, metricsDTO.ctr());
        assertEquals(0.0, metricsDTO.cpa());
        assertEquals(0.0, metricsDTO.cpc());
        assertEquals(0.0, metricsDTO.cpm());
        assertEquals(0.0, metricsDTO.bounceRate());
    }

    @Test
    public void testSingleImpression() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/single_impression.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/empty_click_log.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/empty_server_log.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(1.0, metricsDTO.impressions());
        assertEquals(0.0, metricsDTO.clicks());
        assertEquals(0.0, metricsDTO.uniques());
        assertEquals(0.0, metricsDTO.bounces());
        assertEquals(0.0, metricsDTO.conversions());
        assertEquals(0.0, metricsDTO.totalCost());
        assertEquals(0.0, metricsDTO.ctr());
        assertEquals(0.0, metricsDTO.cpa());
        assertEquals(0.0, metricsDTO.cpc());
        assertEquals(0.0, metricsDTO.cpm());
        assertEquals(0.0, metricsDTO.bounceRate());
    }

    @Test
    public void testMultiImpressions() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/empty_click_log.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/empty_server_log.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(18.0, metricsDTO.impressions());
        assertEquals(0.0, metricsDTO.clicks());
        assertEquals(0.0, metricsDTO.uniques());
        assertEquals(0.0, metricsDTO.bounces());
        assertEquals(0.0, metricsDTO.conversions());
        assertEquals(153.0, metricsDTO.totalCost());
        assertEquals(0.0, metricsDTO.ctr());
        assertEquals(0.0, metricsDTO.cpa());
        assertEquals(0.0, metricsDTO.cpc());
        assertEquals(8500.0, metricsDTO.cpm());
        assertEquals(0.0, metricsDTO.bounceRate());
    }

    @Test
    public void testSingleImpressionSingleClick() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/single_impression.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/single_click.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/single_click_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(1.0, metricsDTO.impressions());
        assertEquals(1.0, metricsDTO.clicks());
        assertEquals(1.0, metricsDTO.uniques());
        assertEquals(1.0, metricsDTO.bounces());
        assertEquals(1.0, metricsDTO.conversions());
        assertEquals(0.0, metricsDTO.totalCost());
        assertEquals(1.0, metricsDTO.ctr());
        assertEquals(0.0, metricsDTO.cpa());
        assertEquals(0.0, metricsDTO.cpc());
        assertEquals(0.0, metricsDTO.cpm());
        assertEquals(1.0, metricsDTO.bounceRate());
    }

    @Test
    public void testMultiImpressionsSingleClick() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/single_click.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/single_click_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(18.0, metricsDTO.impressions());
        assertEquals(1.0, metricsDTO.clicks());
        assertEquals(1.0, metricsDTO.uniques());
        assertEquals(1.0, metricsDTO.bounces());
        assertEquals(1.0, metricsDTO.conversions());
        assertEquals(153.0, metricsDTO.totalCost());
        assertEquals(0.05555555555555555, metricsDTO.ctr());
        assertEquals(153.0, metricsDTO.cpa());
        assertEquals(153.0, metricsDTO.cpc());
        assertEquals(8500.0, metricsDTO.cpm());
        assertEquals(1.0, metricsDTO.bounceRate());
    }

    @Test
    public void testSingleImpressionMultiClicks() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/single_impression.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/single_impression_clicks.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/single_impression_clicks_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(1.0, metricsDTO.impressions());
        assertEquals(3.0, metricsDTO.clicks());
        assertEquals(1.0, metricsDTO.uniques());
        assertEquals(1.0, metricsDTO.bounces());
        assertEquals(2.0, metricsDTO.conversions());
        assertEquals(3.0, metricsDTO.totalCost());
        assertEquals(3.0, metricsDTO.ctr());
        assertEquals(1.5, metricsDTO.cpa());
        assertEquals(1.0, metricsDTO.cpc());
        assertEquals(3000.0, metricsDTO.cpm());
        assertEquals(0.3333333333333333, metricsDTO.bounceRate());

    }

    @Test
    public void testMultiImpressionsMultiClicks() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(18.0, metricsDTO.impressions());
        assertEquals(18.0, metricsDTO.clicks());
        assertEquals(6.0, metricsDTO.uniques());
        assertEquals(6.0, metricsDTO.bounces());
        assertEquals(9.0, metricsDTO.conversions());
        assertEquals(306.0, metricsDTO.totalCost());
        assertEquals(1.0, metricsDTO.ctr());
        assertEquals(34.0, metricsDTO.cpa());
        assertEquals(17.0, metricsDTO.cpc());
        assertEquals(17000.0, metricsDTO.cpm());
        assertEquals(0.3333333333333333, metricsDTO.bounceRate());
    }

    @Test
    public void testGenderMaleFilter() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        filterCriteriaDTO = new FilterCriteriaDTO(null, null, null, null, "Male", null, null, null, null);

        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(9.0, metricsDTO.impressions());
        assertEquals(9.0, metricsDTO.clicks());
        assertEquals(3.0, metricsDTO.uniques());
        assertEquals(3.0, metricsDTO.bounces());
        assertEquals(6.0, metricsDTO.conversions());
        assertEquals(126.0, metricsDTO.totalCost());
        assertEquals(1.0, metricsDTO.ctr());
        assertEquals(21.0, metricsDTO.cpa());
        assertEquals(14.0, metricsDTO.cpc());
        assertEquals(14000.0, metricsDTO.cpm());
        assertEquals(0.3333333333333333, metricsDTO.bounceRate());
    }

    @Test
    public void testGenderFemaleFilter() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        filterCriteriaDTO = new FilterCriteriaDTO(null, null, null, null, "Female", null, null, null, null);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(9.0, metricsDTO.impressions());
        assertEquals(9.0, metricsDTO.clicks());
        assertEquals(3.0, metricsDTO.uniques());
        assertEquals(3.0, metricsDTO.bounces());
        assertEquals(3.0, metricsDTO.conversions());
        assertEquals(180.0, metricsDTO.totalCost());
        assertEquals(1.0, metricsDTO.ctr());
        assertEquals(60.0, metricsDTO.cpa());
        assertEquals(20.0, metricsDTO.cpc());
        assertEquals(20000.0, metricsDTO.cpm());
        assertEquals(0.3333333333333333, metricsDTO.bounceRate());
    }

    @Test
    public void testAllAgesFilter() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        filterCriteriaDTO = new FilterCriteriaDTO(List.of("<25", "25-34", "35-44", "45-54", ">54"), null, null, null, null, null, null, null, null);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(18.0, metricsDTO.impressions());
        assertEquals(18.0, metricsDTO.clicks());
        assertEquals(6.0, metricsDTO.uniques());
        assertEquals(6.0, metricsDTO.bounces());
        assertEquals(9.0, metricsDTO.conversions());
        assertEquals(306.0, metricsDTO.totalCost());
        assertEquals(1.0, metricsDTO.ctr());
        assertEquals(34.0, metricsDTO.cpa());
        assertEquals(17.0, metricsDTO.cpc());
        assertEquals(17000.0, metricsDTO.cpm());
        assertEquals(0.3333333333333333, metricsDTO.bounceRate());
    }

    @Test
    public void testAllIncomesFilter() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        filterCriteriaDTO = new FilterCriteriaDTO(null,List.of("Low","Medium","High"),null,null,null,null,null,null,null);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(18.0, metricsDTO.impressions());
        assertEquals(18.0, metricsDTO.clicks());
        assertEquals(6.0, metricsDTO.uniques());
        assertEquals(6.0, metricsDTO.bounces());
        assertEquals(9.0, metricsDTO.conversions());
        assertEquals(306.0, metricsDTO.totalCost());
        assertEquals(1.0, metricsDTO.ctr());
        assertEquals(34.0, metricsDTO.cpa());
        assertEquals(17.0, metricsDTO.cpc());
        assertEquals(17000.0, metricsDTO.cpm());
        assertEquals(0.3333333333333333, metricsDTO.bounceRate());
    }

    @Test
    public void testAllContextsFilter() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        filterCriteriaDTO = new FilterCriteriaDTO(null,null,List.of("News","Shopping","Social Media","Blog","Hobbies","Travel"),null,null,null,null,null,null);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(18.0, metricsDTO.impressions());
        assertEquals(18.0, metricsDTO.clicks());
        assertEquals(6.0, metricsDTO.uniques());
        assertEquals(6.0, metricsDTO.bounces());
        assertEquals(9.0, metricsDTO.conversions());
        assertEquals(306.0, metricsDTO.totalCost());
        assertEquals(1.0, metricsDTO.ctr());
        assertEquals(34.0, metricsDTO.cpa());
        assertEquals(17.0, metricsDTO.cpc());
        assertEquals(17000.0, metricsDTO.cpm());
        assertEquals(0.3333333333333333, metricsDTO.bounceRate());

    }

    @Test
    public void testAllDates() throws URISyntaxException {
        File impressionLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_impressions.csv")).toURI());
        File clickLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks.csv")).toURI());
        File serverLog = new File(Objects.requireNonNull(getClass().getResource("/multiple_clicks_server.csv")).toURI());
        fileImportService.setImpressionLog(impressionLog);
        fileImportService.setClickLog(clickLog);
        fileImportService.setServerLog(serverLog);
        filterCriteriaDTO = new FilterCriteriaDTO(null,null,null,null,null,LocalDate.of(2015,1,1),LocalDate.of(2015,1,6),null,null);
        fileImportService.runFullImport("testCampaign");
        metricsDTO = calculateMetricsService.fetchMetrics(filterCriteriaDTO);

        assertEquals(18.0, metricsDTO.impressions());
        assertEquals(18.0, metricsDTO.clicks());
        assertEquals(6.0, metricsDTO.uniques());
        assertEquals(6.0, metricsDTO.bounces());
        assertEquals(9.0, metricsDTO.conversions());
        assertEquals(306.0, metricsDTO.totalCost());
        assertEquals(1.0, metricsDTO.ctr());
        assertEquals(34.0, metricsDTO.cpa());
        assertEquals(17.0, metricsDTO.cpc());
        assertEquals(17000.0, metricsDTO.cpm());
        assertEquals(0.3333333333333333, metricsDTO.bounceRate());
    }
}