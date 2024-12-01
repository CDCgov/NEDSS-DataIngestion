package gov.cdc.nbs.mpidatasyncer.model;

import java.time.LocalDateTime;

public record LogRequest(LocalDateTime start, LocalDateTime end) { }
