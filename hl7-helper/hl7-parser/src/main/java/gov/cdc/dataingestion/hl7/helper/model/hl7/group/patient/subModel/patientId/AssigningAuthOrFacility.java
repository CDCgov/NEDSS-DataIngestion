package gov.cdc.dataingestion.hl7.helper.model.hl7.group.patient.subModel.patientId;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.Hd;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssigningAuthOrFacility extends Hd {
    public AssigningAuthOrFacility(ca.uhn.hl7v2.model.v251.datatype.HD data) {
        this.setNameSpaceId(data.getNamespaceID().getValue());
        this.setUniversalId(data.getUniversalID().getValue());
        this.setUniversalIdType(data.getUniversalIDType().getValue());
    }
}
