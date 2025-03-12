package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.FhirService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
//@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "FHIR Ingestion", description = "FHIR JSON Ingestion API")

public class FhirController {

    private final FhirService fhirService;
    private final CustomMetricsBuilder customMetricsBuilder;
    @Autowired
    public FhirController(FhirService fhirService, CustomMetricsBuilder customMetricsBuilder) {
        this.fhirService = fhirService;
        this.customMetricsBuilder = customMetricsBuilder;
    }

    @SuppressWarnings("java:S1871")
    @Operation(
            summary = "Submit a ECR in FHIR JSON format.",
            description = "Ingest an ECR in FHIR JSON format that should be in a .json file",
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/api/fhir")
    public ResponseEntity<String> save(@RequestParam("fhirfile") MultipartFile fhirfile, @RequestHeader(name = "version", defaultValue = "1") String version) {
        customMetricsBuilder.incrementMessagesProcessed();
        String filename=fhirfile.getOriginalFilename();
        System.out.println("filename:"+filename);
        try {
            String fhirBundle = new String(fhirfile.getBytes());
            fhirService.convertFhirBundleToPhdcXML(fhirBundle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("FHIR json received.");
    }
}