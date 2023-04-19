package gov.cdc.dataingestion.deadletter.controller;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<ElrDeadLetterDto> getAllNewErrorMessage() {
        return ResponseEntity.ok(elrDeadLetterService.getDltRecordById(""));
    }
}
