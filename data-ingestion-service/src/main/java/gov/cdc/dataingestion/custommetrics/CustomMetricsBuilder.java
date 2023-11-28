package gov.cdc.dataingestion.custommetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetricsBuilder {
    Counter custom_messages_processed;
    Counter custom_messages_validated;
    Counter custom_messages_validated_success;
    Counter custom_messages_validated_failure;
    Counter custom_duplicate_hl7_found;
    Counter custom_xml_conversion_requested;
    Counter custom_xml_converted_success;
    Counter custom_xml_converted_failure;
    Counter custom_tokens_requested;


    public CustomMetricsBuilder(MeterRegistry meterRegistry) {
        this.custom_messages_processed = Counter
                .builder("custom_messages_processed")
                .register(meterRegistry);
        this.custom_messages_validated = Counter
                .builder("custom_messages_validated")
                .register(meterRegistry);
        this.custom_messages_validated_success = Counter
                .builder("custom_messages_validated_success")
                .register(meterRegistry);
        this.custom_messages_validated_failure = Counter
                .builder("custom_messages_validated_failure")
                .register(meterRegistry);
        this.custom_duplicate_hl7_found = Counter
                .builder("custom_duplicate_hl7_found")
                .register(meterRegistry);
        this.custom_xml_conversion_requested = Counter
                .builder("custom_xml_conversion_requested")
                .register(meterRegistry);
        this.custom_xml_converted_success = Counter
                .builder("custom_xml_converted_success")
                .register(meterRegistry);
        this.custom_xml_converted_failure = Counter
                .builder("custom_xml_converted_failure")
                .register(meterRegistry);
        this.custom_tokens_requested = Counter
                .builder("custom_tokens_requested")
                .register(meterRegistry);
    }

    public void incrementMessagesProcessed() {
        custom_messages_processed.increment();
    }
    public void incrementMessagesValidated() {
        custom_messages_validated.increment();
    }
    public void incrementMessagesValidatedSuccess() {
        custom_messages_validated_success.increment();
    }
    public void incrementMessagesValidatedFailure() {
        custom_messages_validated_failure.increment();
    }
    public void incrementDuplicateHL7Messages() {
        custom_duplicate_hl7_found.increment();
    }
    public void incrementXmlConversionRequested() {
        custom_xml_conversion_requested.increment();
    }
    public void incrementXmlConversionRequestedSuccess() {
        custom_xml_converted_success.increment();
    }
    public void incrementXmlConversionRequestedFailure() {
        custom_xml_converted_failure.increment();
    }
    public void incrementTokensRequested() {
        custom_tokens_requested.increment();
    }
}