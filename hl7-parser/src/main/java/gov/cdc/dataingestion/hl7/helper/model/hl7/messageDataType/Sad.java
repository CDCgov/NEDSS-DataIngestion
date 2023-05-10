package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.SAD;
import lombok.Getter;

@Getter
public class Sad {
    String streetMailingAddress;
    String streetName;
    String dwellingNumber;

    public Sad(SAD sad) {
        this.streetMailingAddress = sad.getStreetOrMailingAddress().getValue();
        this.streetName = sad.getStreetName().getValue();
        this.dwellingNumber = sad.getDwellingNumber().getValue();
    }
}
