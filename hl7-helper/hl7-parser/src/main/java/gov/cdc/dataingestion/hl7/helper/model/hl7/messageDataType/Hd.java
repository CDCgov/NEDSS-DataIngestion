package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hd {
    String nameSpaceId;
    String universalId;
    String universalIdType;
}
