//package gov.cdc.dataingestion.config;
//
//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.Logger;
//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.util.ContextInitializer;
//import ch.qos.logback.core.joran.spi.JoranException;
//import org.junit.jupiter.api.*;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.TimeZone;
//
//public class LogDynamicFileAppenderConfigTest {
//
//    private static LogDynamicFileAppenderConfig<String> logDynamicFileAppenderConfig;
//
//    @BeforeAll
//    public static void setUp() throws IOException, JoranException {
//        // Load logback configuration from dlt-logback.xml
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        ContextInitializer initializer = new ContextInitializer(loggerContext);
//        initializer.configureByResource(LogDynamicFileAppenderConfigTest.class.getClassLoader().getResource("dlt-logback.xml"));
//
//        logDynamicFileAppenderConfig = new LogDynamicFileAppenderConfig<>();
//        logDynamicFileAppenderConfig.setContext(loggerContext); // Set the logger context
//    }
//
//    @AfterAll
//    public static void tearDown() throws IOException {
//        // Clean up
//        if (logDynamicFileAppenderConfig.getFile() != null) {
//            deleteLogFile(logDynamicFileAppenderConfig.getFile());
//        }
//    }
//
//    @Test
//    public void start_LogFilePathNotConfigured_ErrorLogged() {
//        // Act
//        logDynamicFileAppenderConfig.start();
//
//        // Assert
//        Assertions.assertTrue(logDynamicFileAppenderConfig.getStatusManager().getCount() > 0);
//    }
//
//    @Test
//    public void start_LogFilePathContainsDateFormat_LogFileCreatedWithFormattedDate() throws IOException {
//        // Arrange
//        String logFilePath = "logs/app-%d{yyyy-MM-dd}.log";
//        logDynamicFileAppenderConfig.setLogFilePath(logFilePath);
//
//        // Act
//        logDynamicFileAppenderConfig.start();
//
//        // Assert
//        Assertions.assertNotNull(logDynamicFileAppenderConfig.getFile());
//
//        // Clean up
//        deleteLogFile(logDynamicFileAppenderConfig.getFile());
//    }
//
//    private static void deleteLogFile(String logFilePath) throws IOException {
//        Path logFile = Paths.get(logFilePath);
//        if (Files.exists(logFile)) {
//            Files.delete(logFile);
//        }
//    }
//
//
//}
