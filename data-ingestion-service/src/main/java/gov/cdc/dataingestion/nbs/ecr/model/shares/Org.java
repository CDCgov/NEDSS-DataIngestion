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

    String orgName;
    String orgLocalId;
    String authState;
    String authStreetAddress1;
    String authStreetAddress2;
    String authCity;
    String authCounty;
    String authCountry;
    String authZip;
    String authPhone;
    String authExtn;
    String authOrgName;
    String authOrgLocalId;

    POCDMT000040Participant2 out;
}
