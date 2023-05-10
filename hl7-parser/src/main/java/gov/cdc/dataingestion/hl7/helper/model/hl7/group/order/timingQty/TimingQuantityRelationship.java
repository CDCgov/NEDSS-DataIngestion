package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;

import java.util.List;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

@Getter
public class TimingQuantityRelationship {
    String setIdTq2;
    String sequenceResultFlag;
    List<Ei> relatedPlacerNumber;
    List<Ei> relatedFillerNumber;
    List<Ei> relatedPlacerGroupNumber;
    String sequenceConditionCode;
    String cyclicEntryExitIndicator;
    Cq sequenceConditionTimeInterval;
    String cyclicGroupMaximumNumberOfRepeats;
    String specialServiceRequestRelationship;

    public TimingQuantityRelationship(ca.uhn.hl7v2.model.v251.segment.TQ2 tq2) {
        this.setIdTq2 = tq2.getSetIDTQ2().getValue();
        this.sequenceResultFlag = tq2.getSequenceResultsFlag().getValue();
        this.relatedPlacerNumber = GetEiList(tq2.getRelatedPlacerNumber());
        this.relatedFillerNumber = GetEiList(tq2.getRelatedFillerNumber());
        this.relatedPlacerGroupNumber = GetEiList(tq2.getRelatedPlacerGroupNumber());
        this.sequenceConditionCode = tq2.getSequenceConditionCode().getValue();
        this.cyclicEntryExitIndicator = tq2.getCyclicEntryExitIndicator().getValue();
        this.sequenceConditionTimeInterval = new Cq(tq2.getTq28_SequenceConditionTimeInterval());
        this.cyclicGroupMaximumNumberOfRepeats = tq2.getCyclicGroupMaximumNumberOfRepeats().getValue();
        this.specialServiceRequestRelationship = tq2.getSpecialServiceRequestRelationship().getValue();
    }
}
