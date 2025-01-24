package gov.cdc.nbs.deduplication.seed.model;
import java.time.LocalDateTime;

public record DeduplicationEntry(
    Long nbsPersonId,
    Long nbsPersonParentId,
    String mpiPatientId,
    String mpiPersonId,
    String status,
    LocalDateTime processed_at){

}
