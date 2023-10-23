package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public ElrReportsController(IEcrMsgQueryService ecrMsgQueryService,
                                ICdaMapper mapper,
                                RawELRService rawELRService,
                                NbsRepositoryServiceProvider nbsRepositoryServiceProvider) {
        this.ecrMsgQueryService = ecrMsgQueryService;
        this.mapper = mapper;
        this.rawELRService = rawELRService;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
    }



    @Operation(
            summary = "Submit a plain text HL7 message",
            description = "Submit a plain text HL7 message with msgType header",
            tags = { "dataingestion", "elr" })
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> save(@RequestBody final String payload, @RequestHeader("msgType") String type,  @RequestHeader("validationActive") String validationActive) {
            RawERLDto rawERLDto = new RawERLDto();
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
