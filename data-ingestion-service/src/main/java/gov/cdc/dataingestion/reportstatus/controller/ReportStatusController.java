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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "ELR Status", description = "ELR Status API")
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
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
        // Sanitize elrId (ensure it's alphanumeric or whatever format you expect)
        String sanitizedElrId = StringUtils.replaceChars(elrId, "[]{}()", "");

        if(sanitizedElrId == null || sanitizedElrId.isEmpty() || !isValidUUID(sanitizedElrId)) {
            logger.error("Invalid 'UUID' parameter provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid 'UUID' parameter provided.");
        }

        List<String> statusList = reportStatusService.getStatusForReport(sanitizedElrId);

        statusList.addFirst("id:"+sanitizedElrId);

        ObjectMapper mapper = new ObjectMapper();
        return ResponseEntity.ok(mapper.writeValueAsString(statusList));
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
                .flatMap(id ->reportStatusService.getMessageStatus(id).stream())
                .toList();

        List<MessageStatus> unmodifiableStatusList = List.copyOf(statusList);

        return ResponseEntity.ok(unmodifiableStatusList);
    }

    private boolean isValidUUID(String id) {
        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        return id.matches(regex);
    }
}