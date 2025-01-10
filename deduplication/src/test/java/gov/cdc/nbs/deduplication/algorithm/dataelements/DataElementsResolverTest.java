package gov.cdc.nbs.deduplication.algorithm.dataelements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfiguration;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfiguration.DataElement;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfigurationResponse;
import gov.cdc.nbs.deduplication.algorithm.exception.ConfigurationParsingException;

@ExtendWith(MockitoExtension.class)
class DataElementsResolverTest {

  @Mock
  private JdbcTemplate template;

  @Spy
  private ObjectMapper mapper = new ObjectMapper();

  @InjectMocks
  private DataElementsResolver resolver;

  private static final DataElementConfiguration CONFIG = new DataElementConfiguration(
      new DataElement(true, 0.16, 0.01, 0.16, 0.01),
      new DataElement(true, 0.15, 0.02, 0.15, 0.02),
      new DataElement(true, 0.14, 0.03, 0.14, 0.03),
      new DataElement(true, 0.13, 0.04, 0.13, 0.04),
      new DataElement(true, 0.12, 0.05, 0.12, 0.05),
      new DataElement(true, 0.11, 0.06, 0.11, 0.06),
      new DataElement(true, 0.10, 0.07, 0.10, 0.07),
      new DataElement(true, 0.09, 0.08, 0.09, 0.08),
      new DataElement(true, 0.08, 0.09, 0.08, 0.09),
      new DataElement(true, 0.07, 0.10, 0.07, 0.10),
      new DataElement(true, 0.06, 0.11, 0.06, 0.11),
      new DataElement(true, 0.05, 0.12, 0.05, 0.12),
      new DataElement(true, 0.04, 0.13, 0.04, 0.13),
      new DataElement(true, 0.03, 0.14, 0.03, 0.14),
      new DataElement(true, 0.02, 0.15, 0.02, 0.15),
      new DataElement(true, 0.01, 0.16, 0.01, 0.16));

  @Test
  void should_resolve() throws JsonProcessingException {
    String configString = mapper.writeValueAsString(CONFIG);
    List<String> configList = List.of(configString, "config2");

    when(template.queryForList(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(configList);

    DataElementConfigurationResponse configurationResponse = resolver.resolveCurrent();

    assertThat(configurationResponse.configuration()).isEqualTo(CONFIG);
  }

  @Test
  void should_not_resolve_empty() {
    List<String> configList = new ArrayList<>();

    when(template.queryForList(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(configList);

    DataElementConfigurationResponse configurationResponse = resolver.resolveCurrent();

    assertThat(configurationResponse.configuration()).isNull();
  }

  @Test
  void should_not_resolve_parsing_exception() throws JsonProcessingException {
    List<String> configList = List.of("config1");
    when(template.queryForList(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(configList);
    doThrow(JsonProcessingException.class).when(mapper).readValue("config1", DataElementConfiguration.class);

    assertThrows(ConfigurationParsingException.class, () -> resolver.resolveCurrent());

  }
}
