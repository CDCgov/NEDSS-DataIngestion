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
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements.DataElement;

@ExtendWith(MockitoExtension.class)
class DataElementsControllerTest {

    @Mock
    private DataElementsService service;

    @InjectMocks
    private DataElementsController controller;

    private static final DataElements dataElements = new DataElements(
            new DataElement(true, 240.0, 9.7884, 0.84),
            new DataElement(true, 20.0, 3.44, 0.85),
            new DataElement(true, 1.1, 19.625, 0.86),
            new DataElement(true, 2.2, 29.47, 0.94),
            new DataElement(true, 3.3, 39.74, 0.64),
            new DataElement(true, 4.4, 49.72, 0.50),
            new DataElement(true, 5.5, 59.975, 0.51),
            new DataElement(true, 6.6, 69.23231, 0.52),
            new DataElement(true, 7.0, 79.552, 0.53),
            new DataElement(true, 8.8, 89.24, 0.54),
            new DataElement(true, 9.9, 99.87, 0.55),
            new DataElement(true, 10.1, 9.01, 0.56),
            new DataElement(true, 11.2, 1.23, 0.57),
            new DataElement(true, 12.3, 2.55, 0.58),
            new DataElement(true, 13.4, 3.44, 0.59),
            new DataElement(true, 14.5, 3.0, 0.60),
            new DataElement(true, 15.6, 4.84, 0.61),
            new DataElement(true, 16.7, 5.224, 0.62),
            new DataElement(true, 17.8, 6.94, 0.63),
            new DataElement(true, 18.9, 7.74, 0.64),
            new DataElement(true, 20.11, 8.584, 0.65),
            new DataElement(true, 21.12, .324, 0.66),
            new DataElement(true, 22.13, 1.14, 0.67),
            new DataElement(true, 23.14, 2.4, 0.68),
            new DataElement(true, 24.15, 3.784, 0.69),
            new DataElement(true, 25.16, 4.84, 0.70));

    @Test
    void returns_current_config() {
        when(service.getCurrentDataElements()).thenReturn(dataElements);
        DataElements actual = controller.get();

        assertThat(actual).isEqualTo(dataElements);
        verify(service, times(1)).getCurrentDataElements();
    }

    @Test
    void saves_config() {
        when(service.save(null)).thenReturn(dataElements);
        DataElements actual = controller.save(null);

        assertThat(actual).isEqualTo(dataElements);
        verify(service, times(1)).save(null);
    }
}
