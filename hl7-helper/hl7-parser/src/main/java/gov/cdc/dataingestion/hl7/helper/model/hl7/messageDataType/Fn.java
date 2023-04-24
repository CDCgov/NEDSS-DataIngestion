package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fn {
    String surname;
    String ownSurnamePrefix;
    String ownSurname;
    String surnamePrefixFromPartner;
    String surnameFromPartner;
}
