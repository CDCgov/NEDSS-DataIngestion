package gov.cdc.dataingestion.nbs.ecr.model.shares;

import gov.cdc.nedss.phdc.cda.POCDMT000040Participant2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Psn {
    String firstName;
    String prefix;
    String lastName;
    String suffix;
    String degree;
    String address1;
    String address2;
    String city;
    String county;
    String country;
    String state;
    String zip;
    String telephone;
    String extn;
    String email;
    POCDMT000040Participant2 out;
}
