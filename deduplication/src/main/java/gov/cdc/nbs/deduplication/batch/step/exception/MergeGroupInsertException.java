package gov.cdc.nbs.deduplication.batch.step.exception;

public class MergeGroupInsertException extends RuntimeException {

  public MergeGroupInsertException() {
    super("Failed to insert a new merge group");
  }

}
