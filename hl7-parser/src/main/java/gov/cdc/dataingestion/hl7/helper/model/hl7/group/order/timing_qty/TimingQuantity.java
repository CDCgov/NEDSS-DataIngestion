package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timing_qty;

import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Cq;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Cwe;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Rpt;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Ts;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.*;
import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.getCweList;

@Getter
public class TimingQuantity {
    String setIdTq1;
    Cq quantity = new Cq();
    List<Rpt> repeatPattern = new ArrayList<>();
    List<String> explicitTime = new ArrayList<>();
    List<Cq> relativeTimeAndUnits = new ArrayList<>();
    Cq serviceDuration = new Cq();
    Ts startDateTime = new Ts();
    Ts endDateTime = new Ts();
    List<Cwe> priority = new ArrayList<>();
    String conditionText;
    String textInstruction;
    String conjunction;
    Cq occurrenceDuration = new Cq();
    String totalOccurrences;

    public TimingQuantity(ca.uhn.hl7v2.model.v251.segment.TQ1 tq1) {
        this.setIdTq1 = tq1.getSetIDTQ1().getValue();
        this.quantity = new Cq(tq1.getQuantity());
        this.repeatPattern = getRptList(tq1.getRepeatPattern());
        this.explicitTime = getTmStringList(tq1.getExplicitTime());
        this.relativeTimeAndUnits = getCqList(tq1.getRelativeTimeAndUnits());
        this.serviceDuration = new Cq(tq1.getServiceDuration());
        this.startDateTime = new Ts(tq1.getStartDateTime());
        this.endDateTime = new Ts(tq1.getEndDateTime());
        this.priority = getCweList(tq1.getPriority());
        this.conditionText = tq1.getConditionText().getValue();
        this.textInstruction = tq1.getTextInstruction().getValue();
        this.conjunction = tq1.getConjunction().getValue();
        this.occurrenceDuration = new Cq(tq1.getOccurrenceDuration());
        this.totalOccurrences = tq1.getTotalOccurrenceS().getValue();
    }

    public TimingQuantity() {

    }
}
