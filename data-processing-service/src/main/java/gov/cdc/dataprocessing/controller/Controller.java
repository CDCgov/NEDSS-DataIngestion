package gov.cdc.dataprocessing.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class Controller {
    @Autowired
    public Controller( ) {

    }

    @GetMapping("/status")
    public ResponseEntity<String> getDataPipelineStatusHealth() {
        log.info("Data Processing Service Status OK");
        return ResponseEntity.status(HttpStatus.OK).body("Data Processing Service Status OK");
    }
}