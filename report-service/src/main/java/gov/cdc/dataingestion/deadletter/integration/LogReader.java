package gov.cdc.dataingestion.deadletter.integration;

import gov.cdc.dataingestion.deadletter.integration.interfaces.ILogReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogReader implements ILogReader {
    private final String logFilePath = "logs/kafka_dlt_error.log";
    public void readDltErrorFromLog() {
        List<String> logRecords = new ArrayList<>();
        try {
            var bufferReader = new BufferedReader(new FileReader(this.logFilePath));
            String line;
            while((line = bufferReader.readLine()) != null ) {
                logRecords.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String logRecord : logRecords) {
            System.out.println(logRecord);
            // Perform additional operations on each log record
        }
    }
}
