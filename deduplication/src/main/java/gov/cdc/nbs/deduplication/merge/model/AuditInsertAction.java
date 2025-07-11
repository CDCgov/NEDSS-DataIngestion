package gov.cdc.nbs.deduplication.merge.model;

import java.util.Map;

public record AuditInsertAction(
    Map<String, Object> primaryKey
) {
}
