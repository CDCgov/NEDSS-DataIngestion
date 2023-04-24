package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.CE;

public class NameContext extends CE {
    public NameContext(Message message) {
        super(message);
    }
}
