package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;

@Getter
public class Pln {
    String idNumber;
    String typeOfIdNumber;
    String stateOtherQualifyingInformation;
    String expirationDate;
    public Pln(ca.uhn.hl7v2.model.v251.datatype.PLN pln) {
        this.idNumber = pln.getIDNumber().getValue();
        this.typeOfIdNumber = pln.getTypeOfIDNumber().getValue();
        this.stateOtherQualifyingInformation = pln.getStateOtherQualifyingInformation().getValue();
        this.expirationDate = pln.getExpirationDate().getValue();
    }
}
