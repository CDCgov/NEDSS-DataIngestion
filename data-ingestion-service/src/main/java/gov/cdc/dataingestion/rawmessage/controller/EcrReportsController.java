package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
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
@Tag(name = "ECR Ingestion", description = "ECR Ingestion API")
public class EcrReportsController {

    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    @Autowired
    public EcrReportsController(NbsRepositoryServiceProvider nbsRepositoryServiceProvider) {
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
    }

    @Operation(
            summary = "Submit an ECR document to Data Ingestion Service",
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
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, path = "/api/ecrs")
    public ResponseEntity<Integer> saveIncomingEcr(@RequestBody final String payload,
                                                   @RequestHeader("systemNm") String systemNm,
                                                   @RequestHeader("origDocTypeEicr") String origDocTypeEicr,
                                                   @RequestHeader(value = "origDocTypeRR", required = false) String origDocTypeRR) {
        try {
            String eicrXml = extractXmlContent(payload, "<eICRXML>", "</eICRXML>");
            String rrXml = extractXmlContent(payload, "<RRXML>", "</RRXML>");

            NbsInterfaceModel nbsInterfaceModel;
            if(rrXml.equalsIgnoreCase("null") || rrXml.isEmpty() || rrXml.isBlank()) {
                nbsInterfaceModel = nbsRepositoryServiceProvider.saveIncomingEcrMessageWithoutRR(eicrXml, systemNm, origDocTypeEicr);
            }
            else {
                nbsInterfaceModel = nbsRepositoryServiceProvider.saveIncomingEcrMessageWithRR(eicrXml, systemNm, origDocTypeEicr, rrXml, origDocTypeRR);
            }
            return ResponseEntity.ok(nbsInterfaceModel.getNbsInterfaceUid());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong while parsing the payload", e);
        }
    }

    private String extractXmlContent(String textInput, String startTag, String endTag) {
        int startIndex = textInput.indexOf(startTag) + startTag.length();
        int endIndex = textInput.indexOf(endTag, startIndex);
        if (startIndex < 0 || endIndex < 0 || startIndex >= endIndex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed XML content: missing or incorrect tags");
        }
        return textInput.substring(startIndex, endIndex).trim();
    }
}
