package gov.cdc.dataingestion.hl7.helper.model;

public class PatientAddress {

    // PID-11-1-1
    String streetAddress;

    // PID-11-1-2
    String streetName;

    // PID-11-1-3
    String dwellingNumber;

    // PID-11-2
    String otherDesignation;

    // PID-11-3
    String city;

    // PID-11-4
    String state;

    // PID-11-5
    String zipCode;

    // PID-11-6
    String country;

    // PID-11-7
    String addressType;

    // ..more


    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getDwellingNumber() {
        return dwellingNumber;
    }

    public void setDwellingNumber(String dwellingNumber) {
        this.dwellingNumber = dwellingNumber;
    }

    public String getOtherDesignation() {
        return otherDesignation;
    }

    public void setOtherDesignation(String otherDesignation) {
        this.otherDesignation = otherDesignation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
