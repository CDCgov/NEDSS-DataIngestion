package gov.cdc.nbs.deduplication.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.algorithm.dataelements.DataElementsService;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmExport;
import gov.cdc.nbs.deduplication.algorithm.pass.PassService;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.AlgorithmException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.BlockingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingAttributeEntry;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingMethod;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.Pass;

@ExtendWith(MockitoExtension.class)
class AlgorithmControllerTest {

    @Mock
    private PassService passService;

    @Mock
    private DataElementsService dataElementsService;

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private AlgorithmController controller;

    private final Pass pass = new Pass(
            null,
            "pass 1",
            "description 1",
            true,
            List.of(BlockingAttribute.ADDRESS),
            List.of(
                    new MatchingAttributeEntry(MatchingAttribute.FIRST_NAME, MatchingMethod.EXACT, 0.7),
                    new MatchingAttributeEntry(MatchingAttribute.LAST_NAME, MatchingMethod.JAROWINKLER, 0.66)),
            0.52,
            0.92);

    private final Algorithm algorithm = new Algorithm(List.of(pass));

    @Test
    void should_get_algorithm() {
        when(passService.getCurrentAlgorithm()).thenReturn(algorithm);

        Algorithm actual = controller.get();

        assertThat(actual).isEqualTo(algorithm);
        verify(passService, times(1)).getCurrentAlgorithm();
    }

    @Test
    void should_export() throws IOException {
        when(passService.getCurrentAlgorithm()).thenReturn(algorithm);
        DataElements dataElements = new DataElements();
        when(dataElementsService.getCurrentDataElements()).thenReturn(dataElements);

        ResponseEntity<byte[]> actual = controller.export();

        AlgorithmExport exportedAlgorithm = mapper.readValue(actual.getBody(), AlgorithmExport.class);
        assertThat(exportedAlgorithm.algorithm()).isEqualTo(algorithm);
        assertThat(exportedAlgorithm.dataElements()).isEqualTo(dataElements);
    }

    @Test
    void should_throw_exception_export() throws IOException {
        when(passService.getCurrentAlgorithm()).thenReturn(algorithm);
        DataElements dataElements = new DataElements();
        when(dataElementsService.getCurrentDataElements()).thenReturn(dataElements);

        when(mapper.writeValueAsString(any(AlgorithmExport.class))).thenThrow(JsonProcessingException.class);

        AlgorithmException ex = assertThrows(AlgorithmException.class, () -> controller.export());
        assertThat(ex.getMessage()).isEqualTo("Failed to export algorithm");
    }

    @Test
    void should_import() throws IOException {
        DataElements dataElements = new DataElements();
        AlgorithmExport exportedAlgorithm = new AlgorithmExport(dataElements, algorithm);
        MultipartFile multipartFile = new MockMultipartFile("importedFile",
                mapper.writeValueAsBytes(exportedAlgorithm));

        ArgumentCaptor<DataElements> elementsCaptor = ArgumentCaptor.forClass(DataElements.class);
        when(dataElementsService.save(elementsCaptor.capture())).thenReturn(dataElements);
        ArgumentCaptor<Algorithm> algorithmCaptor = ArgumentCaptor.forClass(Algorithm.class);
        doNothing().when(passService).saveAlgorithm(algorithmCaptor.capture());

        Algorithm actual = controller.importAlgorithm(multipartFile);

        assertThat(actual).isEqualTo(algorithm);
        assertThat(elementsCaptor.getValue()).isEqualTo(dataElements);
        assertThat(algorithmCaptor.getValue()).isEqualTo(algorithm);
    }

    @Test
    void should_throw_exception_import() throws IOException {
        DataElements dataElements = new DataElements();
        AlgorithmExport exportedAlgorithm = new AlgorithmExport(dataElements, algorithm);
        MultipartFile multipartFile = new MockMultipartFile(
                "importedFile",
                mapper.writeValueAsBytes(exportedAlgorithm));

        when(mapper.readValue(
                multipartFile.getBytes(),
                AlgorithmExport.class))
                .thenThrow(JsonProcessingException.class);

        AlgorithmException ex = assertThrows(
                AlgorithmException.class,
                () -> controller.importAlgorithm(multipartFile));
        assertThat(ex.getMessage()).isEqualTo("Failed to import algorithm");

    }
}
