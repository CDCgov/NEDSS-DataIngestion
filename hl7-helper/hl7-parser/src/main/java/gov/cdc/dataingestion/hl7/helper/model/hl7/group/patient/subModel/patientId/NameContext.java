package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId;

import ca.uhn.hl7v2.model.Message;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ce;

public class NameContext extends Ce {
    public NameContext(ca.uhn.hl7v2.model.v251.datatype.CE message) {
        setIdentifier(message.getIdentifier().getValue());
        setText(message.getText().getValue());
        setNameOfCodingSystem(message.getNameOfCodingSystem().getValue());
        setAlternateIdentifier(message.getAlternateIdentifier().getValue());
        setAlternateText(message.getAlternateText().getValue());
        setNameOfAlternateCodingSystem(message.getNameOfAlternateCodingSystem().getValue());
    }
}
