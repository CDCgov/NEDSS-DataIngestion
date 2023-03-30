package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@Slf4j
@RequiredArgsConstructor
public class ElrReportsController {

    private final RawELRService rawELRService;

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> save(@RequestBody final String payload, @RequestHeader("msgType") String type) {

        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setType(type);
        rawERLDto.setPayload(payload);
        return ResponseEntity.ok(rawELRService.submission(rawERLDto));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RawERLDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(rawELRService.getById(id));
    }

}
