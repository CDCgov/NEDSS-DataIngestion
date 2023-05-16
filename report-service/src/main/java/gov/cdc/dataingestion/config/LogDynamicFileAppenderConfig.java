package gov.cdc.dataingestion.config;
import ch.qos.logback.core.FileAppender;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LogDynamicFileAppenderConfig<E> extends FileAppender<E> {

    private String logFilePath; // New property for the log file path

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public void start() {
        if (logFilePath == null) {
            addError("Log file path is not configured");
            return;
        }

        File logFile = new File(logFilePath);
        if (!logFile.isAbsolute()) {
            logFile = new File(System.getProperty("user.dir"), logFilePath);
        }

        try {
            if (!logFile.exists()) {
                Path parentDir = logFile.toPath().getParent();
                Files.createDirectories(parentDir);
                Files.createFile(logFile.toPath());
            }
        } catch (IOException e) {
            addError("Failed to create log file: " + logFilePath, e);
            return;
        }

        setFile(logFile.getAbsolutePath()); // Set the File property with the log file path

        super.start();
    }
}