package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "ECR Ingestion", description = "ECR Ingestion API")
public class EcrReportsController {
    private IEcrMsgQueryService ecrMsgQueryService;
    private ICdaMapper cdaMapper;

    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    @Autowired
    public EcrReportsController(IEcrMsgQueryService ecrMsgQueryService, ICdaMapper cdaMapper, NbsRepositoryServiceProvider nbsRepositoryServiceProvider) {
        this.ecrMsgQueryService = ecrMsgQueryService;
        this.cdaMapper = cdaMapper;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
    }

    @Operation(
            summary = "Submit a PHDC XML to Data Ingestion Service",
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
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, path = "/api/ecrs/{document-type-code}")
    public ResponseEntity<String> saveEcr(@RequestBody final String payload, @PathVariable("document-type-code") String parameter) throws EcrCdaXmlException {
        if(parameter.equalsIgnoreCase("PHC236")) {
            var result = ecrMsgQueryService.getSelectedEcrRecord();
            var xmlResult = this.cdaMapper.tranformSelectedEcrToCDAXml(result);

            nbsRepositoryServiceProvider.saveEcrCdaXmlMessage(result.getMsgContainer().getNbsInterfaceUid().toString()
                    , result.getMsgContainer().getDataMigrationStatus(), xmlResult);
            return ResponseEntity.ok(xmlResult);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide valid document type code.");
        }
    }
}
