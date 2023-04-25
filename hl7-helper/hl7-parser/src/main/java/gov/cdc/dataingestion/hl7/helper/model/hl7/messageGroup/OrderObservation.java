package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;

import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.CommonOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderObservation {
    CommonOrder commonOrder;
    public OrderObservation(ca.uhn.hl7v2.model.v251.group.ORU_R01_ORDER_OBSERVATION oruR01OrderObservation) {
        this.commonOrder = new CommonOrder(oruR01OrderObservation.getORC());
    }
}
