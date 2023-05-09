package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;


import ca.uhn.hl7v2.model.v251.datatype.MSG;
import lombok.Getter;

@Getter
public class Msg {
    String messageCode;
    String triggerEvent;
    String messageStructure;

    public Msg(MSG msg) {
        this.messageCode = msg.getMessageCode().getValue();
        this.triggerEvent = msg.getTriggerEvent().getValue();
        this.messageStructure = msg.getMessageStructure().getValue();
    }
}
