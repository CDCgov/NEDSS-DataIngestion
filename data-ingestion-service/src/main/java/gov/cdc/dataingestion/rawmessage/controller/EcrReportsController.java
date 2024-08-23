package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
            summary = "Submit a ECR document to Data Ingestion Service",
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
                                                   @RequestHeader("rr-message") String incomingRR,
                                                   @RequestHeader("system-nm") String systemNm,
                                                   @RequestHeader("orig-doc-type-eicr") String origDocTypeEicr,
                                                   @RequestHeader("orig-doc-type-rr") String origDocTypeRR) {
        NbsInterfaceModel nbsInterfaceModel;
        if(incomingRR.equalsIgnoreCase("null") || incomingRR.isEmpty() || incomingRR.isBlank()) {
            nbsInterfaceModel = nbsRepositoryServiceProvider.saveIncomingEcrMessageWithoutRR(payload, systemNm, origDocTypeEicr);
        }
        else {
            nbsInterfaceModel = nbsRepositoryServiceProvider.saveIncomingEcrMessageWithRR(payload, systemNm, origDocTypeEicr, incomingRR, origDocTypeRR);
        }
        return ResponseEntity.ok(nbsInterfaceModel.getNbsInterfaceUid());
    }
}
