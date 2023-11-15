package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Tag(name = "ELR Reports", description = "ELR reports API")

@RestController
@RequestMapping("/api/reports")
@Slf4j
@RequiredArgsConstructor
public class ElrReportsController {

    private final RawELRService rawELRService;

    private IEcrMsgQueryService ecrMsgQueryService;
    private ICdaMapper mapper;

    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;
    private final CustomMetricsBuilder customMetricsBuilder;

    @Autowired
    public ElrReportsController(IEcrMsgQueryService ecrMsgQueryService,
                                ICdaMapper mapper,
                                RawELRService rawELRService,
                                NbsRepositoryServiceProvider nbsRepositoryServiceProvider,
                                CustomMetricsBuilder customMetricsBuilder) {
        this.ecrMsgQueryService = ecrMsgQueryService;
        this.mapper = mapper;
        this.rawELRService = rawELRService;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
        this.customMetricsBuilder = customMetricsBuilder;
    }



    @Operation(
            summary = "Submit a plain text HL7 message",
            description = "Submit a plain text HL7 message with msgType header",
            tags = { "dataingestion", "elr" })
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> save(@RequestBody final String payload, @RequestHeader("msgType") String type,  @RequestHeader("validationActive") String validationActive) {

            if (type.isEmpty() || validationActive.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required headers should not be null");
            }

            if (!type.equalsIgnoreCase("HL7")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide valid value for msgType header");
            }

            boolean validationCheck = "true".equalsIgnoreCase(validationActive) || "false".equalsIgnoreCase(validationActive);
            if (!validationCheck) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide valid value for validationActive header: value must be either true or false");
            }

            RawERLDto rawERLDto = new RawERLDto();
            customMetricsBuilder.incrementMessagesProcessed();
            rawERLDto.setType(type);
            rawERLDto.setPayload(payload);
            if (validationActive != null && !validationActive.isEmpty() && validationActive.equalsIgnoreCase("true")) {
                rawERLDto.setValidationActive(true);
            }
            return ResponseEntity.ok(rawELRService.submission(rawERLDto));
    }

    @Operation(
            summary = "Get a report information by id",
            description = "Get a HL7 report by the given id",
            tags = { "dataingestion", "elr" })
    @GetMapping(path = "/{id}")
    public ResponseEntity<RawERLDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(rawELRService.getById(id));
    }

    @Operation(
            summary = "Transform parsed ecr data in MSG table into CDA xml")
    @GetMapping(path = "/ecr/cda-transformation")
    public ResponseEntity<String> processingMsgEcrIntoCDA() throws EcrCdaXmlException {
        var result = ecrMsgQueryService.getSelectedEcrRecord();
        try {
            String xmlREsult = mapper.tranformSelectedEcrToCDAXml(result);
            nbsRepositoryServiceProvider.saveEcrCdaXmlMessage("21216969", -1, xmlREsult);
            return ResponseEntity.ok(xmlREsult);
        } catch ( Exception e) {
            e.getMessage();
        }

        return ResponseEntity.ok("AA");
    }
}
