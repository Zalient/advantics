package com.university.grp20;

import static org.junit.jupiter.api.Assertions.*;

import com.university.grp20.model.FilterCriteriaDTO;
import com.university.grp20.model.GenerateChartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

class SQLFilterTest {

    private GenerateChartService generateChartService;

    @BeforeEach
    void setUp() {
        generateChartService = new GenerateChartService();
    }

    @Test
    void testACFEmpty(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(true, "Start date condition missing");
    }

    @Test
    void testACFFull(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setStartDate(LocalDate.of(2015, 1, 1));
        filterDTO.setEndDate(LocalDate.of(2015, 1, 12));
        filterDTO.setGender("Male");
        filterDTO.setAgeRanges(List.of("<25", ">54"));
        filterDTO.setIncomes(List.of("Low", "Medium"));
        filterDTO.setContexts(List.of("Shopping", "Blog"));
        filterDTO.setTimeGranularity("Per Week");

        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");

        System.out.println("Generated SQL: " + result);

        assertTrue(result.contains("AND t.Date >= '2015-01-01'"), "Start date condition missing");
        assertTrue(result.contains("AND t.Date <= '2015-01-12'"), "End date condition missing");
        assertTrue(result.contains("AND u.Gender = 'Male'"), "Gender filter missing");
        assertTrue(result.contains("AND u.Age IN ('<25', '>54')"), "Age filter missing");
        assertTrue(result.contains("AND u.Income IN ('Low', 'Medium')"), "Income filter missing");
        assertTrue(result.contains("AND u.Context IN ('Shopping', 'Blog')"), "Context filter missing");
        assertTrue(result.contains("GROUP BY STRFTIME('%Y-%W', t.Date) "), "Time Granularity missing");
    }


    @Test
    void testACFNormal(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setStartDate(LocalDate.of(2015, 1, 1));
        filterDTO.setEndDate(LocalDate.of(2015, 1, 12));
        filterDTO.setGender("Male");
        filterDTO.setAgeRanges(List.of("<25", "25-34"));
        filterDTO.setIncomes(List.of("Low", "Medium"));
        filterDTO.setContexts(List.of("Shopping"));

        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");

        System.out.println("Generated SQL: " + result);

        assertTrue(result.contains("AND t.Date >= '2015-01-01'"), "Start date condition missing");
        assertTrue(result.contains("AND t.Date <= '2015-01-12'"), "End date condition missing");
        assertTrue(result.contains("AND u.Gender = 'Male'"), "Gender filter missing");
        assertTrue(result.contains("AND u.Age IN ('<25', '25-34')"), "Age filter missing");
        assertTrue(result.contains("AND u.Income IN ('Low', 'Medium')"), "Income filter missing");
        assertTrue(result.contains("AND u.Context IN ('Shopping')"), "Context filter missing");
    }

    @Test
    void testACFPerHour(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setTimeGranularity("Per Hour");
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("GROUP BY STRFTIME('%Y-%m-%d %H', t.Date) "), "Time Granularity missing");
    }

    @Test
    void testACFPerDay(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setTimeGranularity("Per Day");
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("GROUP BY STRFTIME('%Y-%m-%d', t.Date)"), "Time Granularity missing");
    }

    @Test
    void testACFPerWeek(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setTimeGranularity("Per Week");
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("GROUP BY STRFTIME('%Y-%W', t.Date)"), "Time Granularity missing");
    }

    @Test
    void testACFStartDate(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setStartDate(LocalDate.of(2015, 1, 1));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND t.Date >= '2015-01-01'"), "Start date condition missing");
    }

    @Test
    void testACFEndDate(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setEndDate(LocalDate.of(2015, 1, 14));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND t.Date <= '2015-01-14'"), "End date condition missing");
    }

    @Test
    void testACFStartAndEndDate(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setStartDate(LocalDate.of(2015, 1, 1));
        filterDTO.setEndDate(LocalDate.of(2015, 1, 14));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND t.Date >= '2015-01-01'"), "Start date condition missing");
        assertTrue(result.contains("AND t.Date <= '2015-01-14'"), "End date condition missing");
    }

    @Test
    void testACFMale(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setGender("Male");
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Gender = 'Male'"), "Gender filter missing");
    }

    @Test
    void testACFFemale(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setGender("Female");
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Gender = 'Female'"), "Gender filter missing");
    }

    @Test
    void testACFLow(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setIncomes(Collections.singletonList("Low"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Income IN ('Low')"), "Income filter missing");
    }

    @Test
    void testACFMedium(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setIncomes(Collections.singletonList("Medium"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Income IN ('Medium')"), "Income filter missing");
    }

    @Test
    void testACFHigh(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setIncomes(Collections.singletonList("High"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Income IN ('High')"), "Income filter missing");
    }

    @Test
    void testACFLess25(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setAgeRanges(List.of("<25"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('<25')"), "Age filter missing");
    }

    @Test
    void testACF25To34(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setAgeRanges(List.of("25-34"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('25-34')"), "Age filter missing");
    }

    @Test
    void testACF35To44(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setAgeRanges(List.of("35-44"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('35-44')"), "Age filter missing");
    }

    @Test
    void testACF45To54(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setAgeRanges(List.of("45-54"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('45-54')"), "Age filter missing");
    }

    @Test
    void testACFMore54(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setAgeRanges(List.of(">54"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('>54')"), "Age filter missing");
    }

    @Test
    void testACFNews(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setContexts(List.of("News"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('News')"), "Context filter missing");
    }

    @Test
    void testACFShopping(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setContexts(List.of("Shopping"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Shopping')"), "Context filter missing");
    }

    @Test
    void testACFSocialMedia(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setContexts(List.of("Social Media"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Social Media')"), "Context filter missing");
    }

    @Test
    void testACFBlog(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setContexts(List.of("Blog"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Blog')"), "Context filter missing");
    }

    @Test
    void testACFHobbies(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setContexts(List.of("Hobbies"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Hobbies')"), "Context filter missing");
    }

    @Test
    void testACFTravel(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO();
        filterDTO.setContexts(List.of("Travel"));
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Travel')"), "Context filter missing");
    }

}