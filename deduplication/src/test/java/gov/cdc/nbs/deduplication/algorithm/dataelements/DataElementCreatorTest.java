package gov.cdc.nbs.deduplication.algorithm.dataelements;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfiguration;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfiguration.DataElement;
import gov.cdc.nbs.deduplication.algorithm.exception.ConfigurationParsingException;

@ExtendWith(MockitoExtension.class)
class DataElementCreatorTest {

  @Mock
  private JdbcTemplate template;

  @Mock
  private ObjectMapper mapper;

  @InjectMocks
  private DataElementCreator creator;

  @Test
  void should_create() throws JsonProcessingException {
    DataElementConfiguration config = new DataElementConfiguration(
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
    when(mapper.writeValueAsString(config)).thenReturn("stringValue");

    creator.create(config);

    verify(template, times(1)).update(Mockito.anyString(), Mockito.eq("stringValue"));
  }

  @Test
  void should_not_create() throws JsonProcessingException {
    when(mapper.writeValueAsString(null)).thenThrow(JsonProcessingException.class);
    assertThrows(ConfigurationParsingException.class, () -> creator.create(null));

  }

}
