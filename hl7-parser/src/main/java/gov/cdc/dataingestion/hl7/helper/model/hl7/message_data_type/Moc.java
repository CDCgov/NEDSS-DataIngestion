package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import ca.uhn.hl7v2.model.v251.datatype.MOC;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Moc {
    Mo monetaryAmount = new Mo();
    Ce chargeCode = new Ce();
    public Moc(MOC moc) {
        this.monetaryAmount = new Mo(moc.getMonetaryAmount());
        this.chargeCode = new Ce(moc.getChargeCode());
    }

    public Moc() {

    }
}
