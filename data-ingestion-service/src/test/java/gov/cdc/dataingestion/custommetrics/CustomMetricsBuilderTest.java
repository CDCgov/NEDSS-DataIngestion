package gov.cdc.dataingestion.custommetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomMetricsBuilderTest {

    private MeterRegistry meterRegistryMock;
    private Counter counterMock;
    private CustomMetricsBuilder customMetricsBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistryMock = mock(MeterRegistry.class);
        counterMock = mock(Counter.class);

        customMetricsBuilder = new CustomMetricsBuilder(meterRegistryMock);
    }

    @Test
    void testIncrementMessagesProcessed() {
        when(meterRegistryMock.counter("custom_messages_processed")).thenReturn(counterMock);
        customMetricsBuilder.custom_messages_processed = counterMock;

        customMetricsBuilder.incrementMessagesProcessed();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementMessagesValidated() {
        when(meterRegistryMock.counter("custom_messages_validated")).thenReturn(counterMock);
        customMetricsBuilder.custom_messages_validated = counterMock;

        customMetricsBuilder.incrementMessagesValidated();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementMessagesValidatedSuccess() {
        when(meterRegistryMock.counter("custom_messages_validated_success")).thenReturn(counterMock);
        customMetricsBuilder.custom_messages_validated_success = counterMock;

        customMetricsBuilder.incrementMessagesValidatedSuccess();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementMessagesValidatedFailure() {
        when(meterRegistryMock.counter("custom_messages_validated_failure")).thenReturn(counterMock);
        customMetricsBuilder.custom_messages_validated_failure = counterMock;

        customMetricsBuilder.incrementMessagesValidatedFailure();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementDuplicateHL7Messages() {
        when(meterRegistryMock.counter("custom_duplicate_hl7_found")).thenReturn(counterMock);
        customMetricsBuilder.custom_duplicate_hl7_found = counterMock;

        customMetricsBuilder.incrementDuplicateHL7Messages();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementXmlConversionRequested() {
        when(meterRegistryMock.counter("custom_xml_conversion_requested")).thenReturn(counterMock);
        customMetricsBuilder.custom_xml_conversion_requested = counterMock;

        customMetricsBuilder.incrementXmlConversionRequested();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementXmlConversionRequestedSuccess() {
        when(meterRegistryMock.counter("custom_xml_converted_success")).thenReturn(counterMock);
        customMetricsBuilder.custom_xml_converted_success = counterMock;

        customMetricsBuilder.incrementXmlConversionRequestedSuccess();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementXmlConversionRequestedFailure() {
        when(meterRegistryMock.counter("custom_xml_converted_failure")).thenReturn(counterMock);
        customMetricsBuilder.custom_xml_converted_failure = counterMock;

        customMetricsBuilder.incrementXmlConversionRequestedFailure();

        verify(counterMock).increment();
    }

    @Test
    void testIncrementTokensRequested() {
        when(meterRegistryMock.counter("custom_tokens_requested")).thenReturn(counterMock);
        customMetricsBuilder.custom_tokens_requested = counterMock;

        customMetricsBuilder.incrementTokensRequested();

        verify(counterMock).increment();
    }
}