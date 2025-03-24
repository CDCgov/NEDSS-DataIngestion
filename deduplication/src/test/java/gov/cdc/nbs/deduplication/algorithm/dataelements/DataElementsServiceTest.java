package gov.cdc.nbs.deduplication.algorithm.dataelements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.algorithm.dataelements.exception.DataElementModificationException;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;

@ExtendWith(MockitoExtension.class)
class DataElementsServiceTest {

    @Mock
    private NamedParameterJdbcTemplate template;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private DataElementsService service;

    @Test
    void should_get_current_data_elements() throws JsonProcessingException {
        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        when(template.getJdbcTemplate()).thenReturn(mockTemplate);

        when(mockTemplate.queryForList(
                DataElementsService.SELECT_CURRENT_DATA_ELEMENTS,
                String.class))
                .thenReturn(List.of("response"));

        when(mapper.readValue("response", DataElements.class)).thenReturn(TestData.DATA_ELEMENTS);

        DataElements actual = service.getCurrentDataElements();

        assertThat(actual).isEqualTo(TestData.DATA_ELEMENTS);
    }

    @Test
    void should_return_null_if_no_elements() {
        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        when(template.getJdbcTemplate()).thenReturn(mockTemplate);

        when(mockTemplate.queryForList(
                DataElementsService.SELECT_CURRENT_DATA_ELEMENTS,
                String.class))
                .thenReturn(List.of());

        DataElements actual = service.getCurrentDataElements();

        assertThat(actual).isNull();
    }

    @Test
    void should_throw_when_parsing_fails() throws JsonProcessingException {
        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        when(template.getJdbcTemplate()).thenReturn(mockTemplate);

        when(mockTemplate.queryForList(
                DataElementsService.SELECT_CURRENT_DATA_ELEMENTS,
                String.class))
                .thenReturn(List.of("response"));

        when(mapper.readValue("response", DataElements.class)).thenThrow(JsonProcessingException.class);

        DataElementModificationException ex = assertThrows(
                DataElementModificationException.class,
                () -> service.getCurrentDataElements());
        assertThat(ex.getMessage()).isEqualTo("Failed to parse data elements");
    }

    @Test
    void should_save_data_elements() throws JsonProcessingException {
        // mock
        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        when(template.getJdbcTemplate()).thenReturn(mockTemplate);

        when(mockTemplate.queryForList(
                DataElementsService.SELECT_CURRENT_DATA_ELEMENTS,
                String.class))
                .thenReturn(List.of("response"));

        when(mapper.readValue("response", DataElements.class)).thenReturn(TestData.DATA_ELEMENTS);
        when(mapper.writeValueAsString(TestData.DATA_ELEMENTS)).thenReturn("stringValue");

        // act
        DataElements actual = service.save(TestData.DATA_ELEMENTS);

        // verify
        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(template).update(eq(DataElementsService.INSERT_DATA_ELEMENTS), captor.capture());

        assertThat(actual).isEqualTo(TestData.DATA_ELEMENTS);
        assertThat(captor.getValue().getValue("configuration")).isEqualTo("stringValue");
    }

    @Test
    void should_throw_when_serializing_fails() throws JsonProcessingException {
        // mock
        when(mapper.writeValueAsString(TestData.DATA_ELEMENTS)).thenThrow(JsonProcessingException.class);

        // act
        DataElementModificationException ex = assertThrows(
                DataElementModificationException.class,
                () -> service.save(TestData.DATA_ELEMENTS));

        // verify
        assertThat(ex.getMessage()).isEqualTo("Failed to save data elements");
    }
}
