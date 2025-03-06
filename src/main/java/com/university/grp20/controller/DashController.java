package com.university.grp20.controller;

import com.university.grp20.model.DashboardModel;
import com.university.grp20.model.FilterDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DashController {

    @FXML public ToggleButton metricsButton, chartsButton;
    @FXML public Label impressionsLabel;
    @FXML public Label clicksLabel;
    @FXML public Label uniquesLabel;
    @FXML public Label bouncesLabel;
    @FXML public Label conversionsLabel;
    @FXML public Label totalLabel;
    @FXML public Label ctrLabel;
    @FXML public Label cpaLabel;
    @FXML public Label cpcLabel;
    @FXML public Label cpmLabel;
    @FXML public Label bounceRateLabel;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private final DashboardModel dashModel = new DashboardModel();
    private FilterDTO filterDTO;

    public void displayUpload(ActionEvent event) throws IOException {
        Parent root =
                FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/FileUpload.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void displayMetricsEditor(ActionEvent event) throws IOException {
        Parent root =
                FXMLLoader.load(
                        Objects.requireNonNull(getClass().getResource("/fxml/MetricsEditorPage.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void showChartsPage(ActionEvent event) throws IOException {
        Parent root =
                FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/ChartPage.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        loadMetrics();
    }

    public void setFilterDTO(FilterDTO filterDTO) {
        this.filterDTO = filterDTO;
        loadMetrics();
    }

    private void loadMetrics() {
        String impressionQuery = buildImpressionQuery(filterDTO);
        double impressions = dashModel.rawMetricGetter(impressionQuery);
        impressionsLabel.setText(String.format("%.2f", impressions) + " impressions");

        String clickQuery = buildClickQuery(filterDTO);
        double clicks = dashModel.rawMetricGetter(clickQuery);
        clicksLabel.setText(String.format("%.2f", clicks) + " clicks");

        String uniquesQuery = buildUniquesQuery(filterDTO);
        double uniques = dashModel.rawMetricGetter(uniquesQuery);
        uniquesLabel.setText(String.format("%.2f", uniques) + " unique IDs");

        String bounceQuery = buildBounceQuery(filterDTO);
        double bounces = dashModel.rawMetricGetter(bounceQuery);
        bouncesLabel.setText(String.format("%.2f", bounces) + " bounces");

        String conversionQuery = buildConversionQuery(filterDTO);
        double conversions = dashModel.rawMetricGetter(conversionQuery);
        conversionsLabel.setText(String.format("%.2f", conversions) + " conversions");

        String impressionCostQuery = buildImpressionCostQuery(filterDTO);
        String clickCostQuery = buildClickCostQuery(filterDTO);
        double impressionCost = dashModel.rawMetricGetter(impressionCostQuery);
        double clickCost = dashModel.rawMetricGetter(clickCostQuery);
        double total = impressionCost + clickCost;
        totalLabel.setText(String.format("%.2f", total / 100) + " pounds");

        double ctr = (impressions == 0) ? 0 : (clicks * 100.0 / impressions);
        ctrLabel.setText(String.format("%.2f%%", ctr));

        double cpa = (conversions == 0) ? 0 : (total / conversions / 100);
        cpaLabel.setText(String.format("%.2f pounds per conversion", cpa));

        double cpc = (clicks == 0) ? 0 : (total / clicks);
        cpcLabel.setText(String.format("%.2f pence per click", cpc));

        double cpm = (impressions == 0) ? 0 : ((total / impressions) * 10);
        cpmLabel.setText(String.format("%.2f pounds per thousand impressions", cpm));

        double bounceRate = (clicks == 0) ? 0 : (bounces / clicks);
        bounceRateLabel.setText(String.format("%.2f bounces per click", bounceRate));
    }

    private String buildImpressionQuery(FilterDTO filter) {
        if (filter == null) {
            return "SELECT COUNT(*) FROM impressionLog";
        }
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM impressionLog i WHERE 1=1");
        appendDateFilter(sb, filter, "i", "Date");
        appendGenderFilter(sb, filter, "i");
        appendAgeFilter(sb, filter, "i");
        appendIncomeFilter(sb, filter, "i");
        appendContextFilter(sb, filter, "i");
        return sb.toString();
    }

    private String buildClickQuery(FilterDTO filter) {
        if (filter == null) {
            return "SELECT COUNT(*) FROM clickLog cl";
        }
        StringBuilder sb =
                new StringBuilder(
                        "SELECT COUNT(*) FROM clickLog cl INNER JOIN impressionLog i ON cl.ID = i.ID WHERE 1=1");
        appendDateFilter(sb, filter, "cl", "Date");
        appendGenderFilter(sb, filter, "i");
        appendAgeFilter(sb, filter, "i");
        appendIncomeFilter(sb, filter, "i");
        appendContextFilter(sb, filter, "i");
        return sb.toString();
    }

    private String buildUniquesQuery(FilterDTO filter) {
        if (filter == null) {
            return "SELECT COUNT(DISTINCT ID) FROM clickLog";
        }
        StringBuilder sb =
                new StringBuilder(
                        "SELECT COUNT(DISTINCT i.ID)"
                                + " FROM clickLog cl"
                                + " INNER JOIN impressionLog i "
                                + " ON cl.ID = i.ID"
                                + " WHERE 1=1");
        appendDateFilter(sb, filter, "i", "Date");
        appendGenderFilter(sb, filter, "i");
        appendAgeFilter(sb, filter, "i");
        appendIncomeFilter(sb, filter, "i");
        appendContextFilter(sb, filter, "i");
        return sb.toString();
    }

    private String buildBounceQuery(FilterDTO filter) {
        if (filter == null) {
            return "SELECT COUNT(*) FROM serverLog s WHERE s.PagesViewed = 1";
        }
        StringBuilder sb =
                new StringBuilder(
                        "SELECT COUNT(*) FROM serverLog s INNER JOIN impressionLog i ON s.ID = i.ID "
                                + "WHERE s.PagesViewed = 1");
        appendDateFilter(sb, filter, "s", "EntryDate");
        appendGenderFilter(sb, filter, "i");
        appendAgeFilter(sb, filter, "i");
        appendIncomeFilter(sb, filter, "i");
        appendContextFilter(sb, filter, "i");
        return sb.toString();
    }

    private String buildConversionQuery(FilterDTO filter) {
        if (filter == null) {
            return "SELECT COUNT(*) FROM serverLog s WHERE s.Conversion = 'Yes'";
        }
        StringBuilder sb =
                new StringBuilder(
                        "SELECT COUNT(*) FROM serverLog s INNER JOIN impressionLog i ON s.ID = i.ID "
                                + "WHERE s.Conversion = 'Yes'");
        appendDateFilter(sb, filter, "s", "EntryDate");
        appendGenderFilter(sb, filter, "i");
        appendAgeFilter(sb, filter, "i");
        appendIncomeFilter(sb, filter, "i");
        appendContextFilter(sb, filter, "i");
        return sb.toString();
    }

    private String buildImpressionCostQuery(FilterDTO filter) {
        if (filter == null) {
            return "SELECT SUM(ImpressionCost) FROM impressionLog";
        }
        StringBuilder sb =
                new StringBuilder("SELECT SUM(ImpressionCost) FROM impressionLog i WHERE 1=1");
        appendDateFilter(sb, filter, "i", "Date");
        appendGenderFilter(sb, filter, "i");
        appendAgeFilter(sb, filter, "i");
        appendIncomeFilter(sb, filter, "i");
        appendContextFilter(sb, filter, "i");
        return sb.toString();
    }

    private String buildClickCostQuery(FilterDTO filter) {
        if (filter == null) {
            return "SELECT SUM(ClickCost) FROM clickLog cl";
        }
        StringBuilder sb =
                new StringBuilder(
                        "SELECT SUM(ClickCost) FROM clickLog cl INNER JOIN impressionLog i ON cl.ID = i.ID WHERE 1=1");
        appendDateFilter(sb, filter, "cl", "Date");
        appendGenderFilter(sb, filter, "i");
        appendAgeFilter(sb, filter, "i");
        appendIncomeFilter(sb, filter, "i");
        appendContextFilter(sb, filter, "i");
        return sb.toString();
    }

    private void appendDateFilter(
            StringBuilder sb, FilterDTO filter, String alias, String dateColumn) {
        if (filter.getStartDate() != null) {
            sb.append(" AND ")
                    .append(alias)
                    .append(".")
                    .append(dateColumn)
                    .append(" >= '")
                    .append(filter.getStartDate())
                    .append("'");
        }
        if (filter.getEndDate() != null) {
            sb.append(" AND ")
                    .append(alias)
                    .append(".")
                    .append(dateColumn)
                    .append(" <= '")
                    .append(filter.getEndDate())
                    .append("'");
        }
    }

    private void appendGenderFilter(StringBuilder sb, FilterDTO filter, String alias) {
        if (filter.getGender() != null && !filter.getGender().isEmpty()) {
            sb.append(" AND ").append(alias).append(".Gender = '").append(filter.getGender()).append("'");
        }
    }

    private void appendAgeFilter(StringBuilder sb, FilterDTO filter, String alias) {
        List<String> ages = filter.getAgeRanges();
        if (ages != null && !ages.isEmpty()) {
            sb.append(" AND ")
                    .append(alias)
                    .append(".Age IN (")
                    .append(listToSQLInClause(ages))
                    .append(")");
        }
    }

    private void appendIncomeFilter(StringBuilder sb, FilterDTO filter, String alias) {
        List<String> incomes = filter.getIncomes();
        if (incomes != null && !incomes.isEmpty()) {
            sb.append(" AND ")
                    .append(alias)
                    .append(".Income IN (")
                    .append(listToSQLInClause(incomes))
                    .append(")");
        }
    }

    private void appendContextFilter(StringBuilder sb, FilterDTO filter, String alias) {
        List<String> contexts = filter.getContexts();
        if (contexts != null && !contexts.isEmpty()) {
            sb.append(" AND ")
                    .append(alias)
                    .append(".Context IN (")
                    .append(listToSQLInClause(contexts))
                    .append(")");
        }
    }

    private String listToSQLInClause(List<String> items) {
        return items.stream().map(item -> "'" + item + "'").collect(Collectors.joining(", "));
    }
}