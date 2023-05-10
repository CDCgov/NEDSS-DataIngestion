package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.PRL;
import lombok.Getter;

@Getter
public class Prl {
    Ce parentObservationIdentifier;
    String parentObservationSubIdentifier;
    String parentObservationValueDescriptor;
    public Prl(PRL prl) {
        this.parentObservationIdentifier = new Ce(prl.getParentObservationIdentifier());
        this.parentObservationSubIdentifier = prl.getParentObservationSubIdentifier().getValue();
        this.parentObservationValueDescriptor = prl.getParentObservationValueDescriptor().getValue();
    }
}
