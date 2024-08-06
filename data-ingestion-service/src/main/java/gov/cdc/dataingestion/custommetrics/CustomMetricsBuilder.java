package gov.cdc.dataingestion.custommetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
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

    Timer elrRawEventTimer;
    Timer elrRawXmlEventTimer;
    Timer elrValidatedTimer;
    Timer xmlPrepTimer;

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

        this.elrRawEventTimer = Timer
                .builder("elr_raw_event_timer")
                .register(meterRegistry);
        this.elrRawXmlEventTimer = Timer
                .builder("elr_raw_xml_event_timer")
                .register(meterRegistry);
        this.elrValidatedTimer = Timer
                .builder("elr_validated_timer")
                .register(meterRegistry);
        this.xmlPrepTimer = Timer
                .builder("xml_prep_timer")
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

    public void recordElrRawEventTime(Runnable task) {
        elrRawEventTimer.record(task);
    }

    public void recordElrRawXmlEventTime(Runnable task) {
        elrRawXmlEventTimer.record(task);
    }

    public void recordElrValidatedTime(Runnable task) {
        elrValidatedTimer.record(task);
    }

    public void recordXmlPrepTime(Runnable task) {
        xmlPrepTimer.record(task);
    }
}