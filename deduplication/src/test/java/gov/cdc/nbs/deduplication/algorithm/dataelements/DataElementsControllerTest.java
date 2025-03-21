package gov.cdc.nbs.deduplication.algorithm.dataelements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;

@ExtendWith(MockitoExtension.class)
class DataElementsControllerTest {

    @Mock
    private DataElementsService service;

    @InjectMocks
    private DataElementsController controller;

    @Test
    void returns_current_config() {
        when(service.getCurrentDataElements()).thenReturn(TestData.DATA_ELEMENTS);
        DataElements actual = controller.get();

        assertThat(actual).isEqualTo(TestData.DATA_ELEMENTS);
        verify(service, times(1)).getCurrentDataElements();
    }

    @Test
    void saves_config() {
        when(service.save(null)).thenReturn(TestData.DATA_ELEMENTS);
        DataElements actual = controller.save(null);

        assertThat(actual).isEqualTo(TestData.DATA_ELEMENTS);
        verify(service, times(1)).save(null);
    }
}
