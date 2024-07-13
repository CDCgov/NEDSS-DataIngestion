package gov.cdc.dataprocessing.controller;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data-processing-svc/rti")
@RequiredArgsConstructor
@Slf4j
public class Controller {
    private final IManagerService managerService;
    @Autowired
    public Controller(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/status")
    public ResponseEntity<String> getDataPipelineStatusHealth() {
        log.info("Data Processing Service Status OK");
        return ResponseEntity.status(HttpStatus.OK).body("Data Processing Service Status OK");
    }
}