package gov.cdc.dataingestion.custommetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class CustomMetricsBuilderTest {

    private MeterRegistry meterRegistryMock;
    private Counter counterMock;
    private Timer timerMock;
    private CustomMetricsBuilder customMetricsBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistryMock = mock(MeterRegistry.class);
        counterMock = mock(Counter.class);
        timerMock = mock(Timer.class);

        customMetricsBuilder = new CustomMetricsBuilder(meterRegistryMock);
    }


    @Test
    void testIncrementMessagesProcessed() {
        when(meterRegistryMock.counter("custom_messages_processed")).thenReturn(counterMock);
        customMetricsBuilder.customMessagesProcessed = counterMock;

        customMetricsBuilder.incrementMessagesProcessed();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementMessagesValidated() {
        when(meterRegistryMock.counter("custom_messages_validated")).thenReturn(counterMock);
        customMetricsBuilder.customMessagesValidated = counterMock;

        customMetricsBuilder.incrementMessagesValidated();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementMessagesValidatedSuccess() {
        when(meterRegistryMock.counter("custom_messages_validated_success")).thenReturn(counterMock);
        customMetricsBuilder.customMessagesValidatedSuccess = counterMock;

        customMetricsBuilder.incrementMessagesValidatedSuccess();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementMessagesValidatedFailure() {
        when(meterRegistryMock.counter("custom_messages_validated_failure")).thenReturn(counterMock);
        customMetricsBuilder.customMessagesValidatedFailure = counterMock;

        customMetricsBuilder.incrementMessagesValidatedFailure();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementDuplicateHL7Messages() {
        when(meterRegistryMock.counter("custom_duplicate_hl7_found")).thenReturn(counterMock);
        customMetricsBuilder.customDuplicateHl7Found = counterMock;

        customMetricsBuilder.incrementDuplicateHL7Messages();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementXmlConversionRequested() {
        when(meterRegistryMock.counter("custom_xml_conversion_requested")).thenReturn(counterMock);
        customMetricsBuilder.customXmlConversionRequested = counterMock;

        customMetricsBuilder.incrementXmlConversionRequested();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementXmlConversionRequestedSuccess() {
        when(meterRegistryMock.counter("custom_xml_converted_success")).thenReturn(counterMock);
        customMetricsBuilder.customXmlConvertedSuccess = counterMock;

        customMetricsBuilder.incrementXmlConversionRequestedSuccess();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementXmlConversionRequestedFailure() {
        when(meterRegistryMock.counter("custom_xml_converted_failure")).thenReturn(counterMock);
        customMetricsBuilder.customXmlConvertedFailure = counterMock;

        customMetricsBuilder.incrementXmlConversionRequestedFailure();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementTokensRequested() {
        when(meterRegistryMock.counter("custom_tokens_requested")).thenReturn(counterMock);
        customMetricsBuilder.customTokensRequested = counterMock;

        customMetricsBuilder.incrementTokensRequested();

        verify(counterMock).increment();
    }
}