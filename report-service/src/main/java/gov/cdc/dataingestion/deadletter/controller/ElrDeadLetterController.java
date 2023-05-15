package gov.cdc.dataingestion.deadletter.controller;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports-dlt")
@Slf4j
@RequiredArgsConstructor
public class ElrDeadLetterController {

    private final ElrDeadLetterService elrDeadLetterService;

    @GetMapping(path = "/get-error-messages")
    public ResponseEntity<List<ElrDeadLetterDto>> getAllNewErrorMessage() throws DeadLetterTopicException {
        return ResponseEntity.ok(elrDeadLetterService.getAllErrorDltRecord());
    }

    @GetMapping(path = "/get-message")
    public ResponseEntity<ElrDeadLetterDto> getErrorMessage(@RequestParam("id") String id) throws DeadLetterTopicException {
        return ResponseEntity.ok(elrDeadLetterService.getDltRecordById(id));
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, path = "/update-inject")
    public ResponseEntity<?> messageReInject(@RequestParam("id") String id, @RequestBody final String payload) throws Exception {
        return ResponseEntity.ok(elrDeadLetterService.updateAndReprocessingMessage(id, payload));
    }
}