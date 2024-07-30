package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "ELR Ingestion", description = "ELR Ingestion API")
public class ElrReportsController {

    private final RawELRService rawELRService;

    private IEcrMsgQueryService ecrMsgQueryService;
    private final ICdaMapper cdaMapper;
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;
    private final CustomMetricsBuilder customMetricsBuilder;

    private IHL7Service hl7Service;

    @Autowired
    public ElrReportsController(IEcrMsgQueryService ecrMsgQueryService,
                                RawELRService rawELRService,
                                ICdaMapper cdaMapper, NbsRepositoryServiceProvider nbsRepositoryServiceProvider,
                                CustomMetricsBuilder customMetricsBuilder,
                                IHL7Service hl7Service) {
        this.ecrMsgQueryService = ecrMsgQueryService;
        this.cdaMapper = cdaMapper;
        this.rawELRService = rawELRService;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
        this.customMetricsBuilder = customMetricsBuilder;
        this.hl7Service = hl7Service;
    }


    @Operation(
            summary = "Submit a plain text or XML converted HL7 message",
            description = "Submit a plain text or XML converted HL7 message with msgType header",
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
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, path = "/api/elrs")
    public ResponseEntity<String> save(@RequestBody final String payload, @RequestHeader("msgType") String type,
                                       @RequestHeader(name = "version", defaultValue = "1") String version) {
        if (type.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required headers should not be null");
        }

        RawERLDto rawERLDto = new RawERLDto();
        customMetricsBuilder.incrementMessagesProcessed();

        if (type.equalsIgnoreCase("HL7")) {
            rawERLDto.setType(type);
            rawERLDto.setPayload(payload);
            rawERLDto.setValidationActive(true);
            return ResponseEntity.ok(rawELRService.submission(rawERLDto, version));
        }
        else if (type.equalsIgnoreCase("HL7-XML")) {
            rawERLDto.setType(type);
            rawERLDto.setPayload(payload);
            rawERLDto.setValidationActive(true);
            return ResponseEntity.ok(rawELRService.submission(rawERLDto, version));
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide valid value for msgType header");
        }
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
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, path = "/api/elrs/validate")
    public ResponseEntity<String> hl7Validator(@RequestBody final String payload) throws DiHL7Exception {
        return ResponseEntity.ok(hl7Service.hl7Validator(payload));
    }
}
