package gov.cdc.nbs.deduplication.sync.kafka;

public class SyncProcessException extends RuntimeException {
  public SyncProcessException(String message) {
    super(message);
  }
}
