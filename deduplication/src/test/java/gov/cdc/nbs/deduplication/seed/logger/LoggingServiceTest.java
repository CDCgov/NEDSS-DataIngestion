package gov.cdc.nbs.deduplication.seed.logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingServiceTest {

  @Mock
  private NamedParameterJdbcTemplate template;

  @InjectMocks
  private LoggingService loggingService;

  @Test
  void testLogError() {
    String stepName = "Test Step";
    String message = "Test error message";
    String nbsFailedPersonIds = "100,101,102";
    Throwable throwable = new RuntimeException("Test exception");

    SqlParameterSource expectedParameterSource = new MapSqlParameterSource()
        .addValue("step_name", stepName)
        .addValue("message", message)
        .addValue("exception_type", throwable.getClass().getName())
        .addValue("exception_message", throwable.getMessage())
        .addValue("failed_ids", nbsFailedPersonIds);

    loggingService.logError(stepName, message, nbsFailedPersonIds, throwable);

    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<SqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(SqlParameterSource.class);

    verify(template).update(queryCaptor.capture(), paramsCaptor.capture());

    SqlParameterSource actualParameterSource = paramsCaptor.getValue();

    assertThat(actualParameterSource.getValue("step_name")).isEqualTo(expectedParameterSource.getValue("step_name"));
    assertThat(actualParameterSource.getValue("message")).isEqualTo(expectedParameterSource.getValue("message"));
    assertThat(actualParameterSource.getValue("exception_type")).isEqualTo(
        expectedParameterSource.getValue("exception_type"));
    assertThat(actualParameterSource.getValue("exception_message")).isEqualTo(
        expectedParameterSource.getValue("exception_message"));
    assertThat(actualParameterSource.getValue("failed_ids")).isEqualTo(expectedParameterSource.getValue("failed_ids"));

  }


}
