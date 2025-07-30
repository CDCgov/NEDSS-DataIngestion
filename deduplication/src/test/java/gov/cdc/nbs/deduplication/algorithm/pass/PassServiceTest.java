package gov.cdc.nbs.deduplication.algorithm.pass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import gov.cdc.nbs.deduplication.algorithm.dataelements.DataElementsService;
import gov.cdc.nbs.deduplication.algorithm.dataelements.TestData;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.AlgorithmException;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.BlockingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingAttributeEntry;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingMethod;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.Pass;

@ExtendWith(MockitoExtension.class)
class PassServiceTest {

    @Mock
    private NamedParameterJdbcTemplate template;

    @Mock
    private DataElementsService dataElementsService;

    @Mock
    private DibbsService dibbsService;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private PassService service;

    private AlgorithmMapper algorithmMapper = new AlgorithmMapper("nbs",false);

    private final Pass pass = new Pass(
        1l,
        "pass 1",
        "description 1",
        true,
        List.of(BlockingAttribute.ADDRESS),
        List.of(
            new MatchingAttributeEntry(MatchingAttribute.FIRST_NAME, MatchingMethod.EXACT, 0.7),
            new MatchingAttributeEntry(MatchingAttribute.LAST_NAME, MatchingMethod.JAROWINKLER, 0.8)),
        0.52,
        0.92);

    private final Algorithm algorithm = new Algorithm(Stream.of(pass).collect(Collectors.toCollection(ArrayList::new)));

    private void mockCurrentConfig(Algorithm returnedAlgorithm) throws JsonProcessingException {
        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        when(template.getJdbcTemplate()).thenReturn(mockTemplate);

        when(mockTemplate.queryForList(
            PassService.SELECT_CURRENT_CONFIG,
            String.class))
            .thenReturn(List.of("response"));

        when(mapper.readValue("response", Algorithm.class)).thenReturn(returnedAlgorithm);
    }

    @Test
    void should_get_current_algorithm() throws JsonProcessingException {
        mockCurrentConfig(algorithm);

        Algorithm actual = service.getCurrentAlgorithm();

        assertThat(actual).isEqualTo(algorithm);
    }

    @Test
    void should_get_empty_algorithm() {
        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        when(template.getJdbcTemplate()).thenReturn(mockTemplate);

        when(mockTemplate.queryForList(
            PassService.SELECT_CURRENT_CONFIG,
            String.class))
            .thenReturn(List.of());

        Algorithm actual = service.getCurrentAlgorithm();

        assertThat(actual.passes()).isEmpty();
    }

    @Test
    void should_throw_algorithm_exception() throws JsonProcessingException {
        JdbcTemplate mockTemplate = Mockito.mock(JdbcTemplate.class);
        when(template.getJdbcTemplate()).thenReturn(mockTemplate);

        when(mockTemplate.queryForList(
            PassService.SELECT_CURRENT_CONFIG,
            String.class))
            .thenReturn(List.of("response"));

        when(mapper.readValue("response", Algorithm.class)).thenThrow(JsonProcessingException.class);

        AlgorithmException ex = assertThrows(AlgorithmException.class, () -> service.getCurrentAlgorithm());

        assertThat(ex.getMessage()).isEqualTo("Failed to parse algorithm");
    }

    @Test
    void should_save_pass() throws JsonProcessingException {
        mockCurrentConfig(algorithm);

        ArgumentCaptor<Algorithm> algorithmCaptor = ArgumentCaptor.forClass(Algorithm.class);
        when(mapper.writeValueAsString(algorithmCaptor.capture())).thenReturn("string value");
        when(dataElementsService.getCurrentDataElements()).thenReturn(TestData.DATA_ELEMENTS);
        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        when(template.update(eq(PassService.INSERT_CONFIG), captor.capture())).thenReturn(1);

        Algorithm actual = service.save(new Pass(
            null,
            "pass 2",
            "description 2",
            true,
            List.of(BlockingAttribute.SEX),
            List.of(
                new MatchingAttributeEntry(MatchingAttribute.ADDRESS, MatchingMethod.EXACT, 0.77),
                new MatchingAttributeEntry(MatchingAttribute.BIRTHDATE, MatchingMethod.EXACT, 0.9)),
            0.52,
            0.92));

        assertThat(actual).isEqualTo(algorithm);
        verify(dibbsService, times(1)).save(algorithmMapper.map(actual, TestData.DATA_ELEMENTS));
        assertThat(captor.getValue().getValue("configuration")).isEqualTo("string value");
        assertThat(algorithmCaptor.getValue().passes()).hasSize(2);
        assertThat(algorithmCaptor.getValue().passes().get(1).id()).isEqualTo(2l);
    }

    @Test
    void should_save_pass_when_empty() throws JsonProcessingException {
        Algorithm emptyAlgorithm = new Algorithm(new ArrayList<>());
        mockCurrentConfig(emptyAlgorithm);

        ArgumentCaptor<Algorithm> algorithmCaptor = ArgumentCaptor.forClass(Algorithm.class);
        when(mapper.writeValueAsString(algorithmCaptor.capture())).thenReturn("string value");
        when(dataElementsService.getCurrentDataElements()).thenReturn(TestData.DATA_ELEMENTS);

        service.save(pass);

        assertThat(algorithmCaptor.getValue().passes()).hasSize(1);
        assertThat(algorithmCaptor.getValue().passes().get(0).id()).isEqualTo(1l);
    }

    @Test
    void should_throw_exception_when_mapper_fails() throws JsonProcessingException {
        Algorithm emptyAlgorithm = new Algorithm(new ArrayList<>());
        mockCurrentConfig(emptyAlgorithm);
        when(dataElementsService.getCurrentDataElements()).thenReturn(TestData.DATA_ELEMENTS);
        when(mapper.writeValueAsString(Mockito.any(Algorithm.class))).thenThrow(JsonProcessingException.class);

        PassModificationException ex = assertThrows(PassModificationException.class, () -> service.save(pass));

        assertThat(ex.getMessage()).isEqualTo("Failed to save pass");
    }

    @Test
    void should_throw_exception_when_missing_data_elements() throws JsonProcessingException {
        Algorithm emptyAlgorithm = new Algorithm(new ArrayList<>());
        mockCurrentConfig(emptyAlgorithm);
        when(dataElementsService.getCurrentDataElements()).thenReturn(null);

        PassModificationException ex = assertThrows(PassModificationException.class, () -> service.save(pass));

        assertThat(ex.getMessage()).isEqualTo("Data elements must first be configured");
    }

    @Test
    void should_update_pass() throws JsonProcessingException {
        mockCurrentConfig(algorithm);

        ArgumentCaptor<Algorithm> algorithmCaptor = ArgumentCaptor.forClass(Algorithm.class);
        when(mapper.writeValueAsString(algorithmCaptor.capture())).thenReturn("string value");
        when(dataElementsService.getCurrentDataElements()).thenReturn(TestData.DATA_ELEMENTS);
        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        when(template.update(eq(PassService.INSERT_CONFIG), captor.capture())).thenReturn(1);

        Pass updatedPass = new Pass(
            4l,
            "updated name",
            "updated description",
            false,
            List.of(BlockingAttribute.BIRTHDATE),
            List.of(
                new MatchingAttributeEntry(MatchingAttribute.ADDRESS, MatchingMethod.EXACT, 0.8),
                new MatchingAttributeEntry(MatchingAttribute.PHONE, MatchingMethod.EXACT, 1.0)),
            0.52,
            0.92);
        Algorithm actual = service.update(1l, updatedPass);

        verify(dibbsService, times(1)).save(algorithmMapper.map(actual, TestData.DATA_ELEMENTS));
        assertThat(captor.getValue().getValue("configuration")).isEqualTo("string value");
        assertThat(algorithmCaptor.getValue().passes()).hasSize(1);
        assertThat(algorithmCaptor.getValue().passes().get(0)).isEqualTo(new Pass(1l, updatedPass));
    }

    @Test
    void update_pass_should_throw_exception_no_pass_found() throws JsonProcessingException {
        mockCurrentConfig(algorithm);

        PassModificationException ex = assertThrows(
            PassModificationException.class,
            () -> service.update(2l, null));
        assertThat(ex.getMessage()).isEqualTo("Failed to find pass with Id: 2");
    }

    @Test
    void delete_pass_single_pass() throws JsonProcessingException {
        mockCurrentConfig(algorithm);

        ArgumentCaptor<Algorithm> algorithmCaptor = ArgumentCaptor.forClass(Algorithm.class);
        when(mapper.writeValueAsString(algorithmCaptor.capture())).thenReturn("string value");
        when(dataElementsService.getCurrentDataElements()).thenReturn(TestData.DATA_ELEMENTS);
        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        when(template.update(eq(PassService.INSERT_CONFIG), captor.capture())).thenReturn(1);

        Algorithm actual = service.delete(1l);

        verify(dibbsService, times(1)).save(algorithmMapper.map(actual, TestData.DATA_ELEMENTS));
        assertThat(captor.getValue().getValue("configuration")).isEqualTo("string value");
        assertThat(algorithmCaptor.getValue().passes()).isEmpty();
    }

    @Test
    void delete_pass_two_passes() throws JsonProcessingException {
        List<Pass> passes = new ArrayList<>();
        passes.add(
            new Pass(
                1l,
                "pass 1",
                "description 1",
                true,
                List.of(BlockingAttribute.ADDRESS),
                List.of(
                    new MatchingAttributeEntry(MatchingAttribute.FIRST_NAME, MatchingMethod.EXACT, 0.45),
                    new MatchingAttributeEntry(MatchingAttribute.LAST_NAME, MatchingMethod.JAROWINKLER,
                        0.32)),
                0.52,
                0.92));
        passes.add(
            new Pass(
                2l,
                "pass 2",
                "description 2",
                true,
                List.of(BlockingAttribute.BIRTHDATE),
                List.of(
                    new MatchingAttributeEntry(MatchingAttribute.ADDRESS, MatchingMethod.EXACT, 0.88)),
                0.52,
                0.92));
        Algorithm algorithmWithTwoPasses = new Algorithm(passes);
        mockCurrentConfig(algorithmWithTwoPasses);

        ArgumentCaptor<Algorithm> algorithmCaptor = ArgumentCaptor.forClass(Algorithm.class);
        when(mapper.writeValueAsString(algorithmCaptor.capture())).thenReturn("string value");
        when(dataElementsService.getCurrentDataElements()).thenReturn(TestData.DATA_ELEMENTS);
        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        when(template.update(eq(PassService.INSERT_CONFIG), captor.capture())).thenReturn(1);

        Algorithm actual = service.delete(1l);

        verify(dibbsService, times(1)).save(algorithmMapper.map(actual, TestData.DATA_ELEMENTS));
        assertThat(captor.getValue().getValue("configuration")).isEqualTo("string value");

        assertThat(algorithmCaptor.getValue().passes()).hasSize(1);
        assertThat(algorithmCaptor.getValue().passes().get(0).name()).isEqualTo("pass 2");
    }

    @Test
    void delete_pass_should_throw_exception_no_pass_found() throws JsonProcessingException {
        mockCurrentConfig(algorithm);

        PassModificationException ex = assertThrows(
            PassModificationException.class,
            () -> service.delete(2l));
        assertThat(ex.getMessage()).isEqualTo("Failed to find pass with Id: 2");
    }

    @Test
    void should_save_dibbs() throws JsonProcessingException {
        mockCurrentConfig(algorithm);
        when(dataElementsService.getCurrentDataElements()).thenReturn(TestData.DATA_ELEMENTS);

        service.saveDibbsAlgorithm();

        verify(
            dibbsService,
            times(1))
            .save(algorithmMapper.map(algorithm, TestData.DATA_ELEMENTS));
    }

    @Test
    void should_not_save_dibbs_null() throws JsonProcessingException {
        mockCurrentConfig(null);

        service.saveDibbsAlgorithm();

        verifyNoInteractions(dibbsService);
    }

    @Test
    void should_not_save_dibbs_empty() throws JsonProcessingException {
        mockCurrentConfig(new Algorithm(List.of()));

        service.saveDibbsAlgorithm();

        verifyNoInteractions(dibbsService);
    }

}
