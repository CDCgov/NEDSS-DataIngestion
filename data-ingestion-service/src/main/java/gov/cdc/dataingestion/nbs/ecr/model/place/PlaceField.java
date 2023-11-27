package gov.cdc.dataingestion.nbs.ecr.model.place;

import gov.cdc.nedss.phdc.cda.POCDMT000040Participant2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PlaceField {
    String state;
    String streetAddress1;
    String streetAddress2;
    String city ;
    String county ;
    String country;
    String zip;
    String workPhone;
    String workExtn;
    String workURL;
    String workEmail;
    String workCountryCode;
    String placeComments;
    String placeAddressComments;
    String teleAsOfDate;
    String postalAsOfDate;
    String censusTract;
    POCDMT000040Participant2 out;
}
