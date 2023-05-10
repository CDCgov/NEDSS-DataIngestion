package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.MOC;
import lombok.Getter;

@Getter
public class Moc {
    Mo monetaryAmount;
    Ce chargeCode;
    public Moc(MOC moc) {
        this.monetaryAmount = new Mo(moc.getMonetaryAmount());
        this.chargeCode = new Ce(moc.getChargeCode());
    }
}
