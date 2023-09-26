package gov.cdc.dataingestion.reportstatus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import gov.cdc.dataingestion.security.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public String nbsInterfaceUid(@PathVariable String id) throws JsonProcessingException {
        logger.debug("Status requested for record with id: '{}'", id);

        String status = reportStatusService.getStatusForReport(id);

        Map<String, String> returnJson = new HashMap<>();
        returnJson.put("id", id);
        returnJson.put("status", status);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(returnJson);
    }
}
