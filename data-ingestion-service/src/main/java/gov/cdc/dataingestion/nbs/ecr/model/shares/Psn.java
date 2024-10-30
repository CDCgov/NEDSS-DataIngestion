package gov.cdc.dataingestion.nbs.ecr.model.shares;

import gov.cdc.nedss.phdc.cda.POCDMT000040Participant2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
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
