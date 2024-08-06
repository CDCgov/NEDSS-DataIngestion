package gov.cdc.dataingestion.custommetrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class TimeMetricsBuilder {
    Timer elrRawEventTimer;
    Timer elrRawXmlEventTimer;
    Timer elrValidatedTimer;
    Timer xmlPrepTimer;
    public TimeMetricsBuilder(MeterRegistry meterRegistry){

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
