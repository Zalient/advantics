package com.university.grp20;

import com.university.grp20.model.OperationLogger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class OperationLoggerTest {
    @Test
    void testInitializeFileCreation() {
        OperationLogger logger = new OperationLogger();
        logger.initialize();

        File logFile = new File(OperationLogger.getLogFileName());
        assertNotNull(OperationLogger.getLogFileName(), "Log file name should not be null after initialize called");
        assertTrue(logFile.exists(), "Log file should exist after initialize called");
    }

    @Test
    void testInitializeLogsSessionStart() throws IOException {
        OperationLogger logger = new OperationLogger();
        logger.initialize();

        File logFile = new File(OperationLogger.getLogFileName());
        List<String> lines = Files.readAllLines(logFile.toPath());
        assertTrue(lines.get(0).contains("Session Started"), "On initialize, log file should display initial Session Start message");
    }

    @Test
    void testSingleLoggingFunctionality() throws IOException {
        OperationLogger logger = new OperationLogger();
        logger.initialize();
        logger.log("Log one time");
        File logFile = new File(OperationLogger.getLogFileName());
        List<String> lines = Files.readAllLines(logFile.toPath());
        assertTrue(lines.get(0).contains("Session Started"), "On initialize, log file should display initial Session Start message");
        assertTrue(lines.get(lines.size() - 1).contains("Log one time"), "First log message should show Log one time");
    }

    @Test
    void testMultiLoggingFunctionality() throws IOException {
        OperationLogger logger = new OperationLogger();
        logger.initialize();
        logger.log("Demo Activity 1");
        logger.log("Chart Created Here");
        File logFile = new File(OperationLogger.getLogFileName());
        List<String> lines = Files.readAllLines(logFile.toPath());
        assertTrue(lines.get(0).contains("Session Started"), "On initialize, log file should display initial Session Start message");
        assertTrue(lines.get(lines.size() - 2).contains("Demo Activity 1"), "First log message should show Demo Activity 1");
        assertTrue(lines.get(lines.size() - 1).contains("Chart Created Here"), "Second log message should show Chart Created Here");
    }

    @Test
    void testInitializeDirectoryCreation() {
        File logDir = new File("operationLogs/");
        if (logDir.exists()) {
            File[] files = logDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            logDir.delete();
        }
        assertFalse(logDir.exists(), "Log directory should be removed");

        OperationLogger logger = new OperationLogger();
        logger.initialize();

        assertTrue(logDir.exists(), "operationLogs directory should be created after calling initialize, if not exist");
    }
}
