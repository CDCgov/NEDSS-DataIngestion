package gov.cdc.nbs.deduplication.duplicates.model;

import java.time.LocalDateTime;

public record PatientNameAndTimeDTO(LocalDateTime addTime, String fullName) {}
