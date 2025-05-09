package gov.cdc.nbs.deduplication.merge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MergeExceptionHandler {

  @ExceptionHandler({
      MergeListException.class })
  public ResponseEntity<ExceptionMessage> handleBadRequestExceptions(Exception e) {
    return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  record ExceptionMessage(String message) {
  }
}
