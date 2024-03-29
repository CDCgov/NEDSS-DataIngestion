package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

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

    public Fn(ca.uhn.hl7v2.model.v251.datatype.FN fn) {
        this.surname = fn.getSurname().getValue();
        this.ownSurnamePrefix = fn.getOwnSurnamePrefix().getValue();
        this.ownSurname = fn.getOwnSurname().getValue();
        this.surnamePrefixFromPartner = fn.getSurnamePrefixFromPartnerSpouse().getValue();
        this.surnameFromPartner = fn.getSurnameFromPartnerSpouse().getValue();
    }

    public Fn() {

    }
}
