package gov.cdc.dataingestion.custommetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class CustomMetricsBuilder {
    Counter customMessagesProcessed;
    Counter customMessagesValidated;
    Counter customMessagesValidatedSuccess;
    Counter customMessagesValidatedFailure;
    Counter customDuplicateHl7Found;
    Counter customXmlConversionRequested;
    Counter customXmlConvertedSuccess;
    Counter customXmlConvertedFailure;
    Counter customTokensRequested;



    public CustomMetricsBuilder(MeterRegistry meterRegistry) {
        this.customMessagesProcessed = Counter
                .builder("custom_messages_processed")
                .register(meterRegistry);
        this.customMessagesValidated = Counter
                .builder("custom_messages_validated")
                .register(meterRegistry);
        this.customMessagesValidatedSuccess = Counter
                .builder("custom_messages_validated_success")
                .register(meterRegistry);
        this.customMessagesValidatedFailure = Counter
                .builder("custom_messages_validated_failure")
                .register(meterRegistry);
        this.customDuplicateHl7Found = Counter
                .builder("custom_duplicate_hl7_found")
                .register(meterRegistry);
        this.customXmlConversionRequested = Counter
                .builder("custom_xml_conversion_requested")
                .register(meterRegistry);
        this.customXmlConvertedSuccess = Counter
                .builder("custom_xml_converted_success")
                .register(meterRegistry);
        this.customXmlConvertedFailure = Counter
                .builder("custom_xml_converted_failure")
                .register(meterRegistry);
        this.customTokensRequested = Counter
                .builder("custom_tokens_requested")
                .register(meterRegistry);

    }

    public void incrementMessagesProcessed() {
        customMessagesProcessed.increment();
    }
    public void incrementMessagesValidated() {
        customMessagesValidated.increment();
    }
    public void incrementMessagesValidatedSuccess() {
        customMessagesValidatedSuccess.increment();
    }
    public void incrementMessagesValidatedFailure() {
        customMessagesValidatedFailure.increment();
    }
    public void incrementDuplicateHL7Messages() {
        customDuplicateHl7Found.increment();
    }
    public void incrementXmlConversionRequested() {
        customXmlConversionRequested.increment();
    }
    public void incrementXmlConversionRequestedSuccess() {
        customXmlConvertedSuccess.increment();
    }
    public void incrementXmlConversionRequestedFailure() {
        customXmlConvertedFailure.increment();
    }
    public void incrementTokensRequested() {
        customTokensRequested.increment();
    }


}