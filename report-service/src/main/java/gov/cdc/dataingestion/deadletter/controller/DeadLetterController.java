package gov.cdc.dataingestion.deadletter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports-dlt")
@Slf4j
@RequiredArgsConstructor
public class DeadLetterController {
}
