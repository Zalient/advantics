package com.university.grp20.logicTests;

import com.university.grp20.model.FilterCriteriaDTO;
import com.university.grp20.model.GenerateChartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLFilterTest {

    private GenerateChartService generateChartService;

    @BeforeEach
    void setUp() {
        generateChartService = new GenerateChartService("testCampaign");
    }

    @Test
    void testACFEmpty(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(true, "Start date condition missing");
    }

    @Test
    void testACFFull(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(List.of("<25",">54"),List.of("Low","Medium"),List.of("Shopping","Blog"),"Per Week","Male",LocalDate.of(2015,1,1),LocalDate.of(2015,1,12),null,null);

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
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(List.of("<25","25-34"),List.of("Low","Medium"),List.of("Shopping"),null,"Male",LocalDate.of(2015,1,1),LocalDate.of(2015,1,12),null,null);

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
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,"Per Hour",null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("GROUP BY STRFTIME('%Y-%m-%d %H', t.Date) "), "Time Granularity missing");
    }

    @Test
    void testACFPerDay(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,"Per Day",null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("GROUP BY STRFTIME('%Y-%m-%d', t.Date)"), "Time Granularity missing");
    }

    @Test
    void testACFPerWeek(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,"Per Week",null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("GROUP BY STRFTIME('%Y-%W', t.Date)"), "Time Granularity missing");
    }

    @Test
    void testACFStartDate(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,null,null,LocalDate.of(2015,1,1),null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND t.Date >= '2015-01-01'"), "Start date condition missing");
    }

    @Test
    void testACFEndDate(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,null,null,null,LocalDate.of(2015,1,14),null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND t.Date <= '2015-01-14'"), "End date condition missing");
    }

    @Test
    void testACFStartAndEndDate(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,null,null,LocalDate.of(2015,1,1),LocalDate.of(2015,1,14),null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND t.Date >= '2015-01-01'"), "Start date condition missing");
        assertTrue(result.contains("AND t.Date <= '2015-01-14'"), "End date condition missing");
    }

    @Test
    void testACFMale(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,null,"Male",null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Gender = 'Male'"), "Gender filter missing");
    }

    @Test
    void testACFFemale(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,null,null,"Female",null,null,null,null);

        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Gender = 'Female'"), "Gender filter missing");
    }

    @Test
    void testACFLow(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,Collections.singletonList("Low"),null,null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Income IN ('Low')"), "Income filter missing");
    }

    @Test
    void testACFMedium(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,Collections.singletonList("Medium"),null,null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Income IN ('Medium')"), "Income filter missing");
    }

    @Test
    void testACFHigh(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,Collections.singletonList("High"),null,null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Income IN ('High')"), "Income filter missing");
    }

    @Test
    void testACFLess25(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(List.of("<25"),null,null,null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('<25')"), "Age filter missing");
    }

    @Test
    void testACF25To34(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(List.of("25-34"),null,null,null,null,null,null,null,null);

        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('25-34')"), "Age filter missing");
    }

    @Test
    void testACF35To44(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(List.of("35-44"),null,null,null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('35-44')"), "Age filter missing");
    }

    @Test
    void testACF45To54(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(List.of("45-54"),null,null,null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('45-54')"), "Age filter missing");
    }

    @Test
    void testACFMore54(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(List.of(">54"),null,null,null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Age IN ('>54')"), "Age filter missing");
    }

    @Test
    void testACFNews(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,List.of("News"),null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('News')"), "Context filter missing");
    }

    @Test
    void testACFShopping(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,List.of("Shopping"),null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Shopping')"), "Context filter missing");
    }

    @Test
    void testACFSocialMedia(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,List.of("Social Media"),null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Social Media')"), "Context filter missing");
    }

    @Test
    void testACFBlog(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,List.of("Blog"),null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Blog')"), "Context filter missing");
    }

    @Test
    void testACFHobbies(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,List.of("Hobbies"),null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Hobbies')"), "Context filter missing");
    }

    @Test
    void testACFTravel(){
        FilterCriteriaDTO filterDTO = new FilterCriteriaDTO(null,null,List.of("Travel"),null,null,null,null,null,null);
        String baseSQL = "SELECT * FROM table WHERE 1=1";
        String result = generateChartService.applyCommonFilter(baseSQL, filterDTO, "t", "Date", "u");
        System.out.println("Generated SQL: " + result);
        assertTrue(result.contains("AND u.Context IN ('Travel')"), "Context filter missing");
    }

}