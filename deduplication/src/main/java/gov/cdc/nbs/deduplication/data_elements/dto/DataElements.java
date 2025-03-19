package gov.cdc.nbs.deduplication.data_elements.dto;

import java.util.List;

public record DataElements(
        DataElement firstName,
        DataElement lastName,
        DataElement dateOfBirth,
        DataElement currentSex,
        DataElement race,
        DataElement suffix,
        DataElement streetAddress1,
        DataElement city,
        DataElement state,
        DataElement zip,
        DataElement county,
        DataElement telephone,
        DataElement telecom,
        DataElement email,
        DataElement accountNumber,
        DataElement driversLicenseNumber,
        DataElement medicaidNumber,
        DataElement medicalRecordNumber,
        DataElement medicareNumber,
        DataElement nationalUniqueIdentifier,
        DataElement patientExternalIdentifier,
        DataElement patientInternalIdentifier,
        DataElement personNumber,
        DataElement socialSecurity,
        DataElement visaPassport,
        DataElement wicIdentifier
) {
    public List<DataElement> getAllDataElements() {
        return List.of(firstName, lastName, dateOfBirth, currentSex, race, suffix, streetAddress1,
                city, state, zip, county, telephone, telecom, email, accountNumber,
                driversLicenseNumber, medicaidNumber, medicalRecordNumber, medicareNumber,
                nationalUniqueIdentifier, patientExternalIdentifier, patientInternalIdentifier,
                personNumber, socialSecurity, visaPassport, wicIdentifier);
    }
}