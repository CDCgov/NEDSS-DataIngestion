package gov.cdc.nbs.deduplication.merge.model;

import java.util.List;

public record RelatedTableAudit(
    String tableName,
    List<AuditUpdateAction> updates,
    List<AuditInsertAction> inserts
) {
}


