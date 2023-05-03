package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.XAD;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Xad {
    Sad streetAddress;
    String otherDesignation;
    String city;
    String state;
    String  zip;
    String country;
    String addressType;
    String otherGeographic;
    String countyCode;
    String censusTract;
    String addressRepresentationCode;
    Dr addressValidityRange;
    Ts effectiveDate;
    Ts expirationDate;
    public Xad(XAD xad) {
        this.streetAddress = new Sad(xad.getStreetAddress());
        this.otherDesignation = xad.getOtherDesignation().getValue();
        this.city = xad.getCity().getValue();
        this.state = xad.getStateOrProvince().getValue();
        this.zip = xad.getZipOrPostalCode().getValue();
        this.country = xad.getCountry().getValue();
        this.addressType = xad.getAddressType().getValue();
        this.otherGeographic = xad.getOtherDesignation().getValue();
        this.countyCode = xad.getCountyParishCode().getValue();
        this.censusTract = xad.getCensusTract().getValue();
        this.addressRepresentationCode = xad.getAddressRepresentationCode().getValue();
        this.addressValidityRange = new Dr(xad.getAddressValidityRange());
        this.effectiveDate = new Ts(xad.getEffectiveDate());
        this.expirationDate = new Ts(xad.getExpirationDate());
    }
}
