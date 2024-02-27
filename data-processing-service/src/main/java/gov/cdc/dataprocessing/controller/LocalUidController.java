package gov.cdc.dataprocessing.controller;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.LocalUidGenerator;
import gov.cdc.dataprocessing.service.implementation.core.OdseIdGeneratorService;
import gov.cdc.dataprocessing.service.interfaces.core.IOdseIdGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/odse/")
@RequiredArgsConstructor
public class LocalUidController {
    private final IOdseIdGeneratorService odseIdGeneratorService;
    @Autowired
    public LocalUidController(OdseIdGeneratorService odseIdGeneratorService) {
        this.odseIdGeneratorService = odseIdGeneratorService;
    }

    @GetMapping(path = "/{className}")
    @Transactional
    public ResponseEntity<LocalUidGenerator> test(@PathVariable String className) {
        var result = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.valueOf(className));
        return ResponseEntity.ok(result);
    }
}
