package gov.cdc.nbs.deduplication.merge.model;

import java.util.Map;

public record AuditUpdateAction(
    Map<String, Object> primaryKey,
    Map<String, Object> previousValues
) {
}
