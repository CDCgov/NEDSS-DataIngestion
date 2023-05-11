package gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantity;
import gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.timingQty.TimingQuantityRelationship;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TimingQty {
    TimingQuantity timeQuantity = new TimingQuantity();
    List<TimingQuantityRelationship> timeQuantityRelationship = new ArrayList<>();

    public TimingQty() {

    }

    public TimingQty(ca.uhn.hl7v2.model.v251.group.ORU_R01_TIMING_QTY oruR01TimingQty) throws HL7Exception {
        this.timeQuantity = new TimingQuantity(oruR01TimingQty.getTQ1());
        this.timeQuantityRelationship = new ArrayList<>();
        for(var item : oruR01TimingQty.getTQ2All()) {
            this.timeQuantityRelationship.add(new TimingQuantityRelationship(item));
        }
    }
}
