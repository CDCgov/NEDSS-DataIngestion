package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;

import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ContactData;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.ObservationRequest;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.shared.NoteAndComment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderObservation {
    CommonOrder commonOrder;
    ObservationRequest observationRequest;
    NoteAndComment noteAndComment;
    ContactData contactData;
    public OrderObservation(ca.uhn.hl7v2.model.v251.group.ORU_R01_ORDER_OBSERVATION oruR01OrderObservation) {
        this.commonOrder = new CommonOrder(oruR01OrderObservation.getORC());
        this.observationRequest = new ObservationRequest(oruR01OrderObservation.getOBR());
        this.noteAndComment = new NoteAndComment(oruR01OrderObservation.getNTE());
        this.contactData = new ContactData(oruR01OrderObservation.getCTD());
    }
}
