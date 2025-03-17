package com.university.grp20.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OperationLogger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
    private static String logFileName;

    public void initialize() {
        String timestamp = LocalDateTime.now().format(formatter);
        logFileName = "operationLogs/" + "userLog_" + timestamp + ".txt";

        try {
            new java.io.File("operationLogs/").mkdirs();

            try (PrintWriter pw = new PrintWriter(new FileWriter(logFileName, true))) {
                pw.println("Session Started: " + timestamp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String action) {
        try (FileWriter fw = new FileWriter(logFileName, true);
             PrintWriter pw = new PrintWriter(fw)) {

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.println("[" + timestamp + "] " + action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLogFileName() {
        return logFileName;
    }
}
