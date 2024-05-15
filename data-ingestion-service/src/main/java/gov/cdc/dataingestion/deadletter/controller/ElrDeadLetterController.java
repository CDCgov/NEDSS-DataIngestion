package gov.cdc.dataingestion.deadletter.controller;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elr/dlt")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "ELR Dead Letter", description = "Elr Dead Letter Messages")
public class ElrDeadLetterController {

    private final ElrDeadLetterService elrDeadLetterService;

    @Operation(
            summary = "Get all dead letter messages",
            description = "Get all dead letter messages",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client Id",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret",
                            required = true,
                            schema = @Schema(type = "string"))}
    )
    @GetMapping(path = "/get-dlt-messages")
    public ResponseEntity<List<ElrDeadLetterDto>> getAllNewErrorMessage() {
        return ResponseEntity.ok(elrDeadLetterService.getAllErrorDltRecord());
    }

    @Operation(
            summary = "Get dead letter message by dlt id",
            description = "Get dead letter message by dlt id",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client Id",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret",
                            required = true,
                            schema = @Schema(type = "string"))}
    )
    @GetMapping(path = "/get-messge-by-dlt-id")
    public ResponseEntity<ElrDeadLetterDto> getErrorMessage(@RequestParam("id") String id) throws DeadLetterTopicException {
        return ResponseEntity.ok(elrDeadLetterService.getDltRecordById(id));
    }

    @Operation(
            summary = "ReInject the message",
            description = "ReInject the payload with dlt id",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client Id",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret",
                            required = true,
                            schema = @Schema(type = "string"))}
    )
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, path = "/reprocess-dlt-message")
    public ResponseEntity<ElrDeadLetterDto> messageReInject(@RequestParam("id") String id, @RequestBody final String payload) throws DeadLetterTopicException {
        return ResponseEntity.ok(elrDeadLetterService.updateAndReprocessingMessage(id, payload));
    }
}