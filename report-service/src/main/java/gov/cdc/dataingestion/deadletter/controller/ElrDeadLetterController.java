package gov.cdc.dataingestion.deadletter.controller;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterELRDto;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports-dlt")
@Slf4j
@RequiredArgsConstructor
public class ElrDeadLetterController {

    private final ElrDeadLetterService elrDeadLetterService;

    @GetMapping(path = "/get-new-error-messages")
    public ResponseEntity<List<ElrDeadLetterELRDto>> getAllNewErrorMessage() {
        return ResponseEntity.ok(elrDeadLetterService.getAllNewDltRecord());
    }
}
