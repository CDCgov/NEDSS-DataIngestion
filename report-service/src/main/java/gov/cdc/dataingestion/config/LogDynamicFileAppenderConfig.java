package gov.cdc.dataingestion.config;
import ch.qos.logback.core.FileAppender;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LogDynamicFileAppenderConfig<E> extends FileAppender<E> {

    private String logFilePath;

    /**
     * Helper method used by Logback
     * Reading logFilePatch tag from dlt-logback.xml and return value
     * */
    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    /**
     * Purpose: Dynamically create log file if not exist
     * */
    @Override
    public void start() {
        if (logFilePath == null) {
            addError("Log file path is not configured");
            return;
        }

        if (logFilePath.contains("%d{")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedDate = dateFormat.format(new Date());
            logFilePath = logFilePath.replace("%d{yyyy-MM-dd_HH-mm-ss}", formattedDate);
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
