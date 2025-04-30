 package com.university.grp20.model;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataGenerator {

    public static class ImpressionEntry {
        public long id;
        public LocalDateTime date;

        public ImpressionEntry(long id, LocalDateTime date) {
            this.id = id;
            this.date = date;
        }
    }

    public static List<ImpressionEntry> generateImpressionLog(Connection conn, int totalRows, int days) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE IF EXISTS impressionLog");
        stmt.execute("CREATE TABLE impressionLog (" +
                "impressionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Date TEXT, " +
                "ID INTEGER, " +
                "ImpressionCost REAL)");

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO impressionLog (Date, ID, ImpressionCost) VALUES (?, ?, ?)");

        Random rand = new Random();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<ImpressionEntry> resultList = new ArrayList<>();

        int[] rowsPerDay = new int[days];
        Arrays.fill(rowsPerDay, totalRows / days);
        for (int i = 0; i < totalRows % days; i++) {
            rowsPerDay[i]++;
        }

        for (int day = 0; day < days; day++) {
            LocalDateTime baseDate = LocalDateTime.of(2015, 1, 1, 0, 0).plusDays(day);
            int rows = rowsPerDay[day];
            int secondsBetween = 86400 / rows;

            for (int i = 0; i < rows; i++) {
                LocalDateTime timestamp = baseDate.plusSeconds(i * secondsBetween);

                int numDigits = 14 + rand.nextInt(6);
                long min = (long) Math.pow(10, numDigits - 1);
                long max = (long) Math.pow(10, numDigits) - 1;
                long id = ThreadLocalRandom.current().nextLong(min, max + 1);

                double cost = Math.round((0.001 + rand.nextDouble() * 0.009) * 1_000_000.0) / 1_000_000.0;

                ps.setString(1, timestamp.format(fmt));
                ps.setLong(2, id);
                ps.setDouble(3, cost);
                ps.addBatch();

                resultList.add(new ImpressionEntry(id, timestamp));
            }
        }

        ps.executeBatch();
        System.out.println("✅ impressionLog generated with " + totalRows + " rows over " + days + " days");

        return resultList;
    }

    public static void generateClickAndServerLogs(Connection conn, List<ImpressionEntry> impressions, int rowCount) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE IF EXISTS clickLog");
        stmt.execute("CREATE TABLE clickLog (" +
                "clickID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Date TEXT, " +
                "ID INTEGER, " +
                "ClickCost REAL)");

        stmt.execute("DROP TABLE IF EXISTS serverLog");
        stmt.execute("CREATE TABLE serverLog (" +
                "serverID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EntryDate TEXT, " +
                "ID INTEGER, " +
                "ExitDate TEXT, " +
                "PagesViewed INTEGER, " +
                "TimeSpent INTEGER, " +
                "Conversion TEXT)");

        PreparedStatement insertClick = conn.prepareStatement(
                "INSERT INTO clickLog (Date, ID, ClickCost) VALUES (?, ?, ?)");

        PreparedStatement insertServer = conn.prepareStatement(
                "INSERT INTO serverLog (EntryDate, ID, ExitDate, PagesViewed, TimeSpent, Conversion) " +
                        "VALUES (?, ?, ?, ?, ?, ?)");

        Random rand = new Random();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Collections.shuffle(impressions);
        List<ImpressionEntry> selected = impressions.subList(0, rowCount);

        for (ImpressionEntry impression : selected) {
            long id = impression.id;

            LocalDateTime clickTime = impression.date.plusSeconds(10 + rand.nextInt(300));
            double clickCost = Math.round(rand.nextDouble() * 15 * 1_000_000.0) / 1_000_000.0;

            insertClick.setString(1, clickTime.format(fmt));
            insertClick.setLong(2, id);
            insertClick.setDouble(3, clickCost);
            insertClick.addBatch();

            LocalDateTime entryTime = clickTime.plusSeconds(rand.nextInt(120));
            boolean noExit = rand.nextDouble() < 0.1;

            String exitStr;
            int timeSpent;

            if (noExit) {
                exitStr = "n/a";
                timeSpent = -1;
            } else {
                LocalDateTime exitTime = entryTime.plusSeconds(30 + rand.nextInt(1200));
                exitStr = exitTime.format(fmt);
                timeSpent = (int) Duration.between(entryTime, exitTime).getSeconds();
            }

            int pagesViewed = rand.nextInt(20) + 1;
            String conversion = rand.nextDouble() < 0.15 ? "Yes" : "No";

            insertServer.setString(1, entryTime.format(fmt));
            insertServer.setLong(2, id);
            insertServer.setString(3, exitStr);
            insertServer.setInt(4, pagesViewed);
            insertServer.setInt(5, timeSpent);
            insertServer.setString(6, conversion);
            insertServer.addBatch();
        }

        insertClick.executeBatch();
        insertServer.executeBatch();

        System.out.println("✅ clickLog and serverLog generated with " + rowCount + " rows each.");
    }

    public static void generateUserDataTable(Connection conn, List<ImpressionEntry> selected) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE IF EXISTS userData");
        stmt.execute("CREATE TABLE userData (" +
                "ID INTEGER PRIMARY KEY, " +
                "Gender TEXT, " +
                "Age TEXT, " +
                "Income TEXT, " +
                "Context TEXT)");

        PreparedStatement insertUser = conn.prepareStatement(
                "INSERT INTO userData (ID, Gender, Age, Income, Context) VALUES (?, ?, ?, ?, ?)");

        Random rand = new Random();

        String[] genders = {"Male", "Female"};
        String[] ageGroups = {"<25", "25-34", "35-44", "45-54", ">54"};
        String[] incomes = {"Low", "Medium", "High"};
        String[] contexts = {"Blog", "News", "Shopping", "Social Media", "Hobbies", "Travel"};

        for (ImpressionEntry entry : selected) {
            insertUser.setLong(1, entry.id);
            insertUser.setString(2, genders[rand.nextInt(genders.length)]);
            insertUser.setString(3, ageGroups[rand.nextInt(ageGroups.length)]);
            insertUser.setString(4, incomes[rand.nextInt(incomes.length)]);
            insertUser.setString(5, contexts[rand.nextInt(contexts.length)]);
            insertUser.addBatch();
        }

        insertUser.executeBatch();
        System.out.println("✅ userData generated for " + selected.size() + " users.");
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:test_dataset.db");

        List<ImpressionEntry> impressions = generateImpressionLog(conn, 500, 14);
        List<ImpressionEntry> selected = impressions.subList(0, 300);

        generateClickAndServerLogs(conn, selected, 300);
        generateUserDataTable(conn, selected);

        conn.close();
    }
}