package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import ca.uhn.hl7v2.model.v251.datatype.PRL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Prl {
    Ce parentObservationIdentifier = new Ce();
    String parentObservationSubIdentifier;
    String parentObservationValueDescriptor;
    public Prl(PRL prl) {
        this.parentObservationIdentifier = new Ce(prl.getParentObservationIdentifier());
        this.parentObservationSubIdentifier = prl.getParentObservationSubIdentifier().getValue();
        this.parentObservationValueDescriptor = prl.getParentObservationValueDescriptor().getValue();
    }

    public Prl() {

    }
}
