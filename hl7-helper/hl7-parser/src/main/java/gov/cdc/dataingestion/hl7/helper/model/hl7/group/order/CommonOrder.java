package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Ei;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonOrder {
    String orderControl;
    Ei placerOrderNumber;
    Ei fillerOrderNumber;
    Ei placerGroupNumber;
    String orderStatus;
    String responseFlag;

}
