package gov.cdc.dataingestion.rawmessage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataIngestionStatusService {

    public DataIngestionStatusService() {}

    public ResponseEntity<String> getHealthStatus() {
        log.info("Data Ingestion Service Status OK");
        return ResponseEntity.status(HttpStatus.OK).body("Data Ingestion Service Status OK");
    }
}
