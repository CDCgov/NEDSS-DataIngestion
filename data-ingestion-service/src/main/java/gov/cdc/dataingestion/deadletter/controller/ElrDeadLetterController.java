package gov.cdc.dataingestion.deadletter.controller;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.exception.DateValidationException;
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
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "ELR Dead Letter", description = "Elr Dead Letter Messages")
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
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
    @GetMapping(path = "/api/elrs/error-messages")
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
    @GetMapping(path = "/api/elrs/error-messages/{dlt-id}")
    public ResponseEntity<ElrDeadLetterDto> getErrorMessage(@PathVariable("dlt-id") String id) throws DeadLetterTopicException {
        return ResponseEntity.ok(elrDeadLetterService.getDltRecordById(id));
    }

    @Operation(
            summary = "ReInject the message with dlt id",
            description = "ReInject the message with dlt id",
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
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, path = "/api/elrs/{dlt-id}")
    public ResponseEntity<ElrDeadLetterDto> messageReInject(@PathVariable("dlt-id") String dltId, @RequestBody final String payload) throws DeadLetterTopicException {
        return ResponseEntity.ok(elrDeadLetterService.updateAndReprocessingMessage(dltId, payload));
    }
    @Operation(
            summary = "Get ELR Ingestion error messages by Date range. The Start date must be earlier than or equal to the End date.",
            description = "Get dead letter error messages by date",
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
                        schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                        name = "startDate",
                        description = "The start date must be in MM/DD/YYYY format.",
                        required = true,
                        schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                        name = "endDate",
                        description = "The end date must be in MM/DD/YYYY format",
                        required = true,
                        schema = @Schema(type = "string"))}
    )
    @GetMapping(path = "/api/elrs/errors")
    public ResponseEntity<List<ElrDeadLetterDto>> getErrorMessagesByDate(@RequestHeader("startDate") String startDate,
                                                                         @RequestHeader(name = "endDate") String endDate) throws DateValidationException {
        return ResponseEntity.ok(elrDeadLetterService.getDltErrorsByDate(startDate, endDate));
    }
}