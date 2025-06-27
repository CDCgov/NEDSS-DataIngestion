package gov.cdc.dataprocessing.controller;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Real Time Ingestion", description = "Real Time Ingestion API")
public class RtiController {
    private final UidPoolManager poolManager;
    private static Logger logger = LoggerFactory.getLogger(RtiController.class); // NOSONAR

    public RtiController(UidPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    @Operation(
            summary = "Reinitialize all UID pools",
            description = "Clears and reloads all UID pools for all LocalIdClass types, including GA variants."
    )
    @GetMapping("/rti/uid/reinitialize-all")
    public String reinitializeAllUidPools() throws DataProcessingException {
        poolManager.reInitializePools();
        return "All UID pools have been reinitialized.";
    }
}
