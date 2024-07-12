package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timing_qty;

import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Cq;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Ei;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.getEiList;

@Getter
public class TimingQuantityRelationship {
    String setIdTq2;
    String sequenceResultFlag;
    List<Ei> relatedPlacerNumber = new ArrayList<>();
    List<Ei> relatedFillerNumber = new ArrayList<>();
    List<Ei> relatedPlacerGroupNumber = new ArrayList<>();
    String sequenceConditionCode;
    String cyclicEntryExitIndicator;
    Cq sequenceConditionTimeInterval = new Cq();
    String cyclicGroupMaximumNumberOfRepeats;
    String specialServiceRequestRelationship;

    public TimingQuantityRelationship(ca.uhn.hl7v2.model.v251.segment.TQ2 tq2) {
        this.setIdTq2 = tq2.getSetIDTQ2().getValue();
        this.sequenceResultFlag = tq2.getSequenceResultsFlag().getValue();
        this.relatedPlacerNumber = getEiList(tq2.getRelatedPlacerNumber());
        this.relatedFillerNumber = getEiList(tq2.getRelatedFillerNumber());
        this.relatedPlacerGroupNumber = getEiList(tq2.getRelatedPlacerGroupNumber());
        this.sequenceConditionCode = tq2.getSequenceConditionCode().getValue();
        this.cyclicEntryExitIndicator = tq2.getCyclicEntryExitIndicator().getValue();
        this.sequenceConditionTimeInterval = new Cq(tq2.getSequenceConditionTimeInterval());
        this.cyclicGroupMaximumNumberOfRepeats = tq2.getCyclicGroupMaximumNumberOfRepeats().getValue();
        this.specialServiceRequestRelationship = tq2.getSpecialServiceRequestRelationship().getValue();
    }

    public TimingQuantityRelationship() {

    }
}
