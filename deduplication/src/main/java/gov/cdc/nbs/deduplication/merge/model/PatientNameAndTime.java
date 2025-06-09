package gov.cdc.nbs.deduplication.merge.model;

import java.time.LocalDateTime;

public record PatientNameAndTime(
    String personLocalId,
    String name,
    LocalDateTime addTime) {

}
