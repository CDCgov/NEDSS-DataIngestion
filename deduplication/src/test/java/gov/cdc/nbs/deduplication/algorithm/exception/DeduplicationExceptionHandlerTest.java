package gov.cdc.nbs.deduplication.algorithm.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import gov.cdc.nbs.deduplication.algorithm.exception.DeduplicationExceptionHandler.ExceptionMessage;

class DeduplicationExceptionHandlerTest {

  private final DeduplicationExceptionHandler handler = new DeduplicationExceptionHandler();

  @Test
  void should_handle_configuration_parsing_exception() {
    ConfigurationParsingException exception = new ConfigurationParsingException();
    ResponseEntity<ExceptionMessage> response = handler.handleBadRequestExceptions(exception);
    ExceptionMessage message = response.getBody();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(500));
    assertNotNull(message);
    assertThat(message.message()).isEqualTo(exception.getMessage());
  }
}
