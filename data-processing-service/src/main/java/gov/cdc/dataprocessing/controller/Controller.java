package gov.cdc.dataprocessing.controller;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {
    private final IManagerService managerService;
    @Autowired
    public Controller(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<String> test(@PathVariable String id) throws Exception {
        try {
            managerService.processDistribution("ELR",id);

        } catch (Exception e) {
            throw  new DataProcessingException(e.getMessage());
        }
        return ResponseEntity.ok("OK");
    }
}
