package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.OSD;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Osd {

    String sequenceResultFlag;
    String placerOrderNumberEntityIdentifier;
    String placerOrderNumberNamespaceId;
    String fillerOrderNumberEntityIdentifier;
    String fillerOrderNumberNamespaceId;
    String sequenceConditionValue;
    String maximumNumberOfRepeats;
    String placerOrderNumberUniversalId;
    String placerOrderNumberUniversalIdType;
    String fillerOrderNumberUniversalId;
    String fillerOrderNumberUniversalIdType;

    public Osd(OSD osd) {
        this.sequenceResultFlag = osd.getSequenceResultsFlag().getValue();
        this.placerOrderNumberEntityIdentifier = osd.getPlacerOrderNumberEntityIdentifier().getValue();
        this.placerOrderNumberNamespaceId = osd.getPlacerOrderNumberNamespaceID().getValue();
        this.fillerOrderNumberEntityIdentifier = osd.getFillerOrderNumberEntityIdentifier().getValue();
        this.fillerOrderNumberNamespaceId = osd.getFillerOrderNumberNamespaceID().getValue();
        this.sequenceConditionValue = osd.getSequenceConditionValue().getValue();
        this.maximumNumberOfRepeats = osd.getMaximumNumberOfRepeats().getValue();
        this.placerOrderNumberUniversalId = osd.getPlacerOrderNumberUniversalID().getValue();
        this.placerOrderNumberUniversalIdType = osd.getPlacerOrderNumberUniversalIDType().getValue();
        this.fillerOrderNumberUniversalId = osd.getFillerOrderNumberUniversalID().getValue();
        this.fillerOrderNumberUniversalIdType = osd.getFillerOrderNumberUniversalIDType().getValue();
    }

    public Osd() {

    }
}
