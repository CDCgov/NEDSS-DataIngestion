package gov.cdc.nbs.deduplication.algorithm.pass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;

class DibbsServiceTest {

  private RestClient client = Mockito.mock(RestClient.class);

  private NamedParameterJdbcTemplate template = Mockito.mock(NamedParameterJdbcTemplate.class);

  private ObjectMapper mapper = new ObjectMapper()
      .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

  private DibbsService service = new DibbsService(client, template, true, mapper);

  private final DibbsAlgorithm algorithm = new DibbsAlgorithm(
      "testLabel",
      "testDescription",
      false,
      null,
      new ArrayList<>());

  @Test
  void should_save_new_algorithm() throws JsonProcessingException {
    // mock
    when(template.queryForObject(
        eq(DibbsService.QUERY_LABEL_COUNT),
        Mockito.any(MapSqlParameterSource.class),
        eq(Integer.class)))
        .thenReturn(0);

    String expectedBody = mapper.writeValueAsString(algorithm);
    mockCreateCall(expectedBody, false);

    ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
    when(template.update(eq(DibbsService.SET_DEFAULT), captor.capture())).thenReturn(1);

    // act
    service.save(algorithm);

    // verify api was called
    verify(client, times(1)).post();
    verify(client, times(0)).put();
    // verify setDefault was called
    assertThat(captor.getValue().getValue("label")).isEqualTo(algorithm.label());
  }

  @Test
  void should_throw_exception_save_fails() throws JsonProcessingException {
    // mock
    when(template.queryForObject(
        eq(DibbsService.QUERY_LABEL_COUNT),
        Mockito.any(MapSqlParameterSource.class),
        eq(Integer.class)))
        .thenReturn(null);

    String expectedBody = mapper.writeValueAsString(algorithm);
    mockCreateCall(expectedBody, true);

    // act
    PassModificationException ex = assertThrows(PassModificationException.class, () -> service.save(algorithm));

    // verify api was called
    assertThat(ex.getMessage()).isEqualTo("Failed to save Dibbs algorithm");
  }

  @Test
  void should_update_algorithm() throws JsonProcessingException {
    // mock
    when(template.queryForObject(
        eq(DibbsService.QUERY_LABEL_COUNT),
        Mockito.any(MapSqlParameterSource.class),
        eq(Integer.class)))
        .thenReturn(1);

    String expectedBody = mapper.writeValueAsString(algorithm);
    mockUpdateCall(algorithm.label(), expectedBody, false);

    ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
    when(template.update(eq(DibbsService.SET_DEFAULT), captor.capture())).thenReturn(1);

    // act
    service.save(algorithm);

    // verify api was called
    verify(client, times(1)).put();
    verify(client, times(0)).post();

    // verify setDefault was called
    assertThat(captor.getValue().getValue("label")).isEqualTo(algorithm.label());
  }

  @Test
  void should_throw_exception_update_fails() throws JsonProcessingException {
    // mock
    when(template.queryForObject(
        eq(DibbsService.QUERY_LABEL_COUNT),
        Mockito.any(MapSqlParameterSource.class),
        eq(Integer.class)))
        .thenReturn(1);

    String expectedBody = mapper.writeValueAsString(algorithm);
    mockUpdateCall(algorithm.label(), expectedBody, true);

    // act
    PassModificationException ex = assertThrows(PassModificationException.class, () -> service.save(algorithm));

    // verify api was called
    assertThat(ex.getMessage()).isEqualTo("Failed to update Dibbs algorithm");
  }

  private void mockUpdateCall(String label, String expectedBody, boolean throwException) {
    RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
    RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
    when(client.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("/algorithm/" + label)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(expectedBody)).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);

    if (throwException) {
      when(responseSpec.toBodilessEntity()).thenThrow(RestClientException.class);
    }
  }

  private void mockCreateCall(String expectedBody, boolean throwException) {
    RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
    RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
    when(client.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("/algorithm")).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(expectedBody)).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);

    if (throwException) {
      when(responseSpec.toBodilessEntity()).thenThrow(RestClientException.class);
    }
  }
}
