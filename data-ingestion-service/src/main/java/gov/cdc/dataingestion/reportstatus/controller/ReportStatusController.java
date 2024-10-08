package gov.cdc.dataingestion.reportstatus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "ELR Status", description = "ELR Status API")
public class ReportStatusController {
    private static Logger logger = LoggerFactory.getLogger(ReportStatusController.class);

    private final ReportStatusService reportStatusService;

    public ReportStatusController(ReportStatusService reportStatusService) {
        this.reportStatusService = reportStatusService;
    }

    @Operation(
            summary = "Get status by raw id",
            description = "Return all info from Elr raw and nbs interface tables.",
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
    @GetMapping("/api/elrs/status/{elr-id}")
    public ResponseEntity<String> getReportStatus(@PathVariable("elr-id") String elrId) throws JsonProcessingException {
        logger.debug("Status requested for record with id: '{}'", elrId);

        if(elrId == null || elrId.isEmpty() || !isValidUUID(elrId)) {
            logger.error("Invalid 'UUID' parameter provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid 'UUID' parameter provided.");
        }

        String status = reportStatusService.getStatusForReport(elrId);

        Map<String, String> returnJson = new HashMap<>();
        returnJson.put("id", elrId);
        if(status.equals("Provided UUID is not present in the database. Either provided an invalid UUID or the injected message failed validation.") || status.equals("Couldn't find status for the requested ID.")) {
            returnJson.put("error_message", status);
        }
        else {
            returnJson.put("status", status);
        }
        ObjectMapper mapper = new ObjectMapper();
        return ResponseEntity.ok(mapper.writeValueAsString(returnJson));
    }

    @Operation(
            summary = "Get all status related to raw id",
            description = "Return all info such as the pipeline which the message is currently at, dlt info,nbs ingestion etc..",
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
    @GetMapping(path = "/api/elrs/status-details/{elr-id}")
    public ResponseEntity<List<MessageStatus>> getMessageStatus(@PathVariable("elr-id") String rawMessageId)  {
        List<String> messageIdList = Arrays.asList(rawMessageId.split(","));
        List<MessageStatus> statusList = messageIdList.stream()
                .map(reportStatusService::getMessageStatus)
                .toList();

        List<MessageStatus> unmodifiableStatusList = List.copyOf(statusList);

        return ResponseEntity.ok(unmodifiableStatusList);
    }

    private boolean isValidUUID(String id) {
        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        return id.matches(regex);
    }
}