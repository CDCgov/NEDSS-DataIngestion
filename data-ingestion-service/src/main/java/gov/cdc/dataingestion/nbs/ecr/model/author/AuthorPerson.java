package gov.cdc.dataingestion.nbs.ecr.model.author;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorPerson {
    private String firstName;
    private String lastName;
    private String streetAddress1;
    private String streetAddress2;
    private String city;
    private String county;
    private String zip;
    private String state;
    private String country;
    private String phone;
    private String extn;
    private String localId;
}
