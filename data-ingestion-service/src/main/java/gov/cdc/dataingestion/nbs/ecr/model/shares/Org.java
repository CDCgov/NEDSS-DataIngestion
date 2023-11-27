package gov.cdc.dataingestion.nbs.ecr.model.shares;

import gov.cdc.nedss.phdc.cda.POCDMT000040Participant2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Org {
    String state;
    String streetAddress1;
    String streetAddress2;
    String city;
    String county;
    String country;
    String zip;
    String phone;
    String extn ;
    POCDMT000040Participant2 out;
}
