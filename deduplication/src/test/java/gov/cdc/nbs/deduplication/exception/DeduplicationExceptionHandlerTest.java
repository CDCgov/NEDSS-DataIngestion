package gov.cdc.nbs.deduplication.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import gov.cdc.nbs.deduplication.algorithm.dataelements.exception.DataElementModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.AlgorithmException;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.batch.mapper.PersonMapException;
import gov.cdc.nbs.deduplication.exception.DeduplicationExceptionHandler.ExceptionMessage;

class DeduplicationExceptionHandlerTest {

  private final DeduplicationExceptionHandler handler = new DeduplicationExceptionHandler();

  @Test
  void should_set_message_data_elements() {
    ResponseEntity<ExceptionMessage> response = handler
        .handleBadRequestExceptions(new DataElementModificationException("Data elements exception"));
    assertThat(response.getBody().message()).isEqualTo("Data elements exception");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
  }

  @Test
  void should_set_message_pass() {
    ResponseEntity<ExceptionMessage> response = handler
        .handleBadRequestExceptions(new PassModificationException("Pass exception"));
    assertThat(response.getBody().message()).isEqualTo("Pass exception");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
  }

  @Test
  void should_set_message_algorithm() {
    ResponseEntity<ExceptionMessage> response = handler
        .handleBadRequestExceptions(new AlgorithmException("Algorithm exception"));
    assertThat(response.getBody().message()).isEqualTo("Algorithm exception");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
  }

  @Test
  void should_set_message_internal() {
    ResponseEntity<ExceptionMessage> response = handler
        .handleInternalServerExceptions(new PersonMapException("Failed to map person data"));
    assertThat(response.getBody().message()).isEqualTo("Failed to map person data");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(500));
  }
}
