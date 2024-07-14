package gov.cdc.dataprocessing.controller;

import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data-processing-svc/rti")
@Slf4j
public class Controller {

    @Autowired
    public Controller() {

    }

    @GetMapping("/status")
    public ResponseEntity<String> getDataPipelineStatusHealth() {
        return ResponseEntity.status(HttpStatus.OK).body("Data Processing Service Status OK");
    }
}