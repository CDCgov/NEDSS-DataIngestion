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

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfiguration;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfiguration.DataElement;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElementConfigurationResponse;

@ExtendWith(MockitoExtension.class)
class DataElementsControllerTest {

  @Mock
  private DataElementsResolver resolver;

  @Mock
  private DataElementCreator creator;

  @InjectMocks
  private DataElementsController controller;

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
  void should_resolve() {
    DataElementConfigurationResponse expected = new DataElementConfigurationResponse(null);
    when(resolver.resolveCurrent()).thenReturn(expected);
    DataElementConfigurationResponse actual = controller.getDataElements();

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void should_save_and_resolve() {
    DataElementConfigurationResponse expected = new DataElementConfigurationResponse(CONFIG);
    when(resolver.resolveCurrent()).thenReturn(expected);

    DataElementConfigurationResponse actual = controller.save(CONFIG);
    assertThat(actual).isEqualTo(expected);
    verify(creator, times(1)).create(CONFIG);

  }
}
