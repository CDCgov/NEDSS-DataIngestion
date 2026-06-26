package gov.cdc.dataingestion.reportstatus.exception;

public class ElrNotFoundException extends RuntimeException {

  public ElrNotFoundException() {
    super(
        "Provided UUID is not present in the database. The provided UUID is either invalid or the message failed validation.");
  }
}
