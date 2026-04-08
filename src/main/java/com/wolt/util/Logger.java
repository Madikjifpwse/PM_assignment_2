package com.wolt.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "logs/log.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static Logger instance;

    private Logger() {
        initializeLogFile();
    }

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    private void initializeLogFile() {
        try {
            Path logPath = Paths.get(LOG_FILE).getParent();
            if (logPath != null && !Files.exists(logPath)) {
                Files.createDirectories(logPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to create logs directory: " + e.getMessage());
        }
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    public void error(String message, Throwable throwable) {
        log("ERROR", message + " | Exception: " + throwable.getMessage());
    }

    public void warn(String message) {
        log("WARN", message);
    }

    private synchronized void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String logEntry = String.format("[%s] [%s] %s%n", timestamp, level, message);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}