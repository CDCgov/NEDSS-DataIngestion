package gov.cdc.dataingestion.reportstatus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @ResponseBody
    public String getReportStatus(@PathVariable String id) throws JsonProcessingException {
        logger.debug("Status requested for record with id: '{}'", id);

        if(id == null || id.isEmpty() || !isValidUUID(id)) {
            throw new IllegalArgumentException("Invalid 'UUID' parameter provided.");
        }

        String status = reportStatusService.getStatusForReport(id);

        Map<String, String> returnJson = new HashMap<>();
        returnJson.put("id", id);
        if(status.equals("Provided UUID is not present in the database.") || status.equals("Couldn't find status for the requested ID.")) {
            returnJson.put("error_message", status);
        }
        else {
            returnJson.put("status", status);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(returnJson);
    }

    private boolean isValidUUID(String id) {
        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        return id.matches(regex);
    }
}
