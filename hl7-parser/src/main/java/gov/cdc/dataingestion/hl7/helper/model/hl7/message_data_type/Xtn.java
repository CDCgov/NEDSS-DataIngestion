package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import ca.uhn.hl7v2.model.v251.datatype.XTN;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Xtn {
    String telephoneNumber;
    String teleComCode;
    String teleComEquipmentType;
    String emailAddress;
    String countryCode;
    String cityCode;
    String localNumber;
    String extension;
    String anyText;
    String extPrefix;
    String speedDialCode;
    String unformattedTeleNumber;
    public Xtn(XTN xtn) {
        this.telephoneNumber = xtn.getTelephoneNumber().getValue();
        this.teleComCode = xtn.getTelecommunicationUseCode().getValue();
        this.teleComEquipmentType = xtn.getTelecommunicationEquipmentType().getValue();
        this.emailAddress = xtn.getEmailAddress().getValue();
        this.countryCode = xtn.getCountryCode().getValue();
        this.cityCode = xtn.getAreaCityCode().getValue();
        this.localNumber = xtn.getLocalNumber().getValue();
        this.extension = xtn.getExtension().getValue();
        this.anyText = xtn.getAnyText().getValue();
        this.extPrefix = xtn.getExtensionPrefix().getValue();
        this.speedDialCode = xtn.getSpeedDialCode().getValue();
        this.unformattedTeleNumber = xtn.getUnformattedTelephoneNumber().getValue();
    }

    public Xtn() {

    }
}
