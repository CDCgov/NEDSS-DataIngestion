package gov.cdc.nbs.deduplication.batch.model;

import java.time.LocalDateTime;

public record PatientNameAndTimeDTO(LocalDateTime addTime, String fullName) {
}
