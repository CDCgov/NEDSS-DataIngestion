package gov.cdc.dataingestion.reportstatus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ReportStatusController {
    private static Logger logger = LoggerFactory.getLogger(ReportStatusController.class);

    private final ReportStatusService reportStatusService;

    public ReportStatusController(ReportStatusService reportStatusService) {
        this.reportStatusService = reportStatusService;
    }

    @GetMapping("/report-status/{id}")
    public ResponseEntity<String> getReportStatus(@PathVariable String id) throws JsonProcessingException {
        logger.debug("Status requested for record with id: '{}'", id);

        if(id == null || id.isEmpty() || !isValidUUID(id)) {
            logger.error("Invalid 'UUID' parameter provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid 'UUID' parameter provided.");
        }

        String status = reportStatusService.getStatusForReport(id);

        Map<String, String> returnJson = new HashMap<>();
        returnJson.put("id", id);
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
            description = "return all info such as the pipeline which the message is currently at, dlt info, etc.."
            )
    @GetMapping(path = "/get-message-info")
    public ResponseEntity<MessageStatus> getMessageStatus(@RequestParam("raw-id") String rawMessageId)  {
        var info = reportStatusService.getMessageStatus(rawMessageId);
        return ResponseEntity.ok(info);
    }

    private boolean isValidUUID(String id) {
        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        return id.matches(regex);
    }
}
