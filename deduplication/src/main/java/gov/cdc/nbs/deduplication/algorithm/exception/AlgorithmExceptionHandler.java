package gov.cdc.nbs.deduplication.algorithm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;

@ControllerAdvice
public class AlgorithmExceptionHandler {

    @ExceptionHandler({ PassModificationException.class })
    public ResponseEntity<ExceptionMessage> handleBadRequestExceptions(Exception e) {
        return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    record ExceptionMessage(String message) {
    }
}
