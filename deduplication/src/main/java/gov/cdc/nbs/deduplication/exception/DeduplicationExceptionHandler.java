package gov.cdc.nbs.deduplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import gov.cdc.nbs.deduplication.algorithm.dataelements.exception.DataElementModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.AlgorithmException;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.batch.mapper.PersonMapException;

@ControllerAdvice
public class DeduplicationExceptionHandler {

  @ExceptionHandler({
      PassModificationException.class,
      DataElementModificationException.class,
      AlgorithmException.class })
  public ResponseEntity<ExceptionMessage> handleBadRequestExceptions(Exception e) {
    return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({
      PersonMapException.class
  })
  public ResponseEntity<ExceptionMessage> handleInternalServerExceptions(Exception e) {
    return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  record ExceptionMessage(String message) {
  }
}
