package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import gov.cdc.dataingestion.validation.services.interfaces.IHL7Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "ELR Ingestion", description = "ELR Ingestion API")
public class ElrReportsController {

    private final RawELRService rawELRService;

    private IEcrMsgQueryService ecrMsgQueryService;
    private ICdaMapper mapper;

    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;
    private final CustomMetricsBuilder customMetricsBuilder;

    private IHL7Service hl7Service;

    @Autowired
    public ElrReportsController(IEcrMsgQueryService ecrMsgQueryService,
                                ICdaMapper mapper,
                                RawELRService rawELRService,
                                NbsRepositoryServiceProvider nbsRepositoryServiceProvider,
                                CustomMetricsBuilder customMetricsBuilder,
                                IHL7Service hl7Service) {
        this.ecrMsgQueryService = ecrMsgQueryService;
        this.mapper = mapper;
        this.rawELRService = rawELRService;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
        this.customMetricsBuilder = customMetricsBuilder;
        this.hl7Service = hl7Service;
    }



    @Operation(
            summary = "Submit a plain text HL7 message",
            description = "Submit a plain text HL7 message with msgType header",
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
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> save(@RequestBody final String payload, @RequestHeader("msgType") String type,
                                       @RequestHeader(name = "version",  defaultValue = "1") String version) {
            if (type.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required headers should not be null");
            }

            if (!type.equalsIgnoreCase("HL7")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide valid value for msgType header");
            }


            RawERLDto rawERLDto = new RawERLDto();
            customMetricsBuilder.incrementMessagesProcessed();
            rawERLDto.setType(type);
            rawERLDto.setPayload(payload);
            rawERLDto.setValidationActive(true);

            return ResponseEntity.ok(rawELRService.submission(rawERLDto, version));
    }

    @Operation(
            summary = "Get a report information by id",
            description = "Get a HL7 report by the given id",
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
    @GetMapping(path = "/{id}")
    public ResponseEntity<RawERLDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(rawELRService.getById(id));
    }

    @Operation(
            summary = "Transform parsed ecr data in MSG table into CDA xml",
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

    @Operation(
            summary = "Verifying whether the payload is a valid hl7 message or not",
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
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, path = "/validate-hl7")
    public ResponseEntity<String> hl7Validator(@RequestBody final String payload) throws DiHL7Exception {
        return ResponseEntity.ok(hl7Service.hl7Validator(payload));
    }
}
