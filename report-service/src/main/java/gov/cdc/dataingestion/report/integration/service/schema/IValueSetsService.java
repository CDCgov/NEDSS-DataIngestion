package gov.cdc.dataingestion.report.integration.service.schema;

import gov.cdc.dataingestion.report.integration.service.IService;
import gov.cdc.dataingestion.report.model.ValueSet;

import java.util.Map;

/**
 * Loads value sets.
 */
public interface IValueSetsService
        extends IService<String, Map<String, ValueSet>> {
        //nop
        }
