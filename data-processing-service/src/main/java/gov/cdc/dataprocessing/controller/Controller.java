package gov.cdc.dataprocessing.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
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