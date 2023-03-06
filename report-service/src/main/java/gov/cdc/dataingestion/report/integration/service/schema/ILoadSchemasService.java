package gov.cdc.dataingestion.report.integration.service.schema;

import gov.cdc.dataingestion.report.integration.service.IService;
import gov.cdc.dataingestion.report.model.Schema;
import java.util.Map;

/**
 * Loads schemas.
 */
public interface ILoadSchemasService
        extends IService<String, Map<String, Schema>> {
         //nop
 }
