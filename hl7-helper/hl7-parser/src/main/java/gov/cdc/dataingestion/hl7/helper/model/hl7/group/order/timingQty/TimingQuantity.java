package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cq;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Cwe;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Rpt;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ts;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.GetCweList;

@Getter
@Setter
public class TimingQuantity {
    String setIdTq1;
    Cq quantity;
    List<Rpt> repeatPattern;
    List<String> explicitTime;
    List<Cq> relativeTimeAndUnits;
    Cq serviceDuration;
    Ts startDateTime;
    Ts endDateTime;
    List<Cwe> priority;
    String conditionText;
    String textInstruction;
    String conjunction;
    Cq occurrenceDuration;
    String totalOccurrences;

    public TimingQuantity(ca.uhn.hl7v2.model.v251.segment.TQ1 tq1) {
        this.setIdTq1 = tq1.getSetIDTQ1().getValue();
        this.quantity = new Cq(tq1.getQuantity());
        this.repeatPattern = GetRptList(tq1.getRepeatPattern());
        this.explicitTime = GetTmStringList(tq1.getExplicitTime());
        this.relativeTimeAndUnits = GetCqList(tq1.getRelativeTimeAndUnits());
        this.serviceDuration = new Cq(tq1.getServiceDuration());
        this.startDateTime = new Ts(tq1.getStartDateTime());
        this.endDateTime = new Ts(tq1.getEndDateTime());
        this.priority = GetCweList(tq1.getPriority());
        this.conditionText = tq1.getConditionText().getValue();
        this.textInstruction = tq1.getTextInstruction().getValue();
        this.conjunction = tq1.getConjunction().getValue();
        this.occurrenceDuration = new Cq(tq1.getOccurrenceDuration());
        this.totalOccurrences = tq1.getTotalOccurrenceS().getValue();
    }
}
