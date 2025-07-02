package gov.cdc.dataprocessing.controller;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.dead_letter.RtiDltDto;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.dead_letter.IDpDeadLetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Real Time Ingestion", description = "Real Time Ingestion API")
public class RtiController {
    private final UidPoolManager poolManager;
    private final IDpDeadLetterService deadLetterService;
    private static Logger logger = LoggerFactory.getLogger(RtiController.class); // NOSONAR

    public RtiController(UidPoolManager poolManager,
                         IDpDeadLetterService deadLetterService) {
        this.poolManager = poolManager;
        this.deadLetterService = deadLetterService;
    }

    @Operation(
            summary = "Reinitialize all UID pools",
            description = "Clears and reloads all UID pools for all LocalIdClass types, including GA variants.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client ID for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            }
    )
    @GetMapping("/uid/reinitialize")
    public String reinitializeAllUidPools() throws DataProcessingException {
        poolManager.reInitializePools();
        return "All UID pools have been reinitialized.";
    }

    @Operation(
            summary = "Retrieve Dead Letter (DLT) records",
            description = "Fetches DLT records either by specific NBS Interface UID or all records with non-success status if no UID is provided.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client ID for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "interfaceUid",
                            description = "Optional NBS Interface UID to filter DLT records",
                            required = false,
                            schema = @Schema(type = "long")
                    )
            }
    )
    @GetMapping("/dlt")
    public List<RtiDltDto> getDeadLetterRecords(
            @RequestParam(name = "interfaceUid", required = false) Long interfaceUid
    ) {
        return deadLetterService.findDltRecords(interfaceUid);
    }
}
