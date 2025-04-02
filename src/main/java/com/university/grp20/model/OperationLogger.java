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
        String timeStamp = LocalDateTime.now().format(formatter);
        logFileName = "operationLogs/" + "userLog_" + timeStamp + ".txt";

        try {
            new java.io.File("operationLogs/").mkdirs();

            try (PrintWriter printWriter = new PrintWriter(new FileWriter(logFileName, true))) {
                printWriter.println("Session Started: " + timeStamp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("Operation logger initialized");
    }

    public void log(String activity) {
        if (logFileName == null) {
            initialize();
        }

        try (FileWriter fileWriter = new FileWriter(logFileName, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            printWriter.println("[" + timeStamp + "] " + activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLogFileName() {
        return logFileName;
    }
}
