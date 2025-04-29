package gov.cdc.nbs.deduplication.algorithm.dataelements.model;

public record DataElements(
    // Personal Information
    DataElement firstName,
    DataElement lastName,
    DataElement dateOfBirth,
    DataElement sex,
    DataElement race,
    DataElement suffix,
    // Address Details
    DataElement address,
    DataElement city,
    DataElement state,
    DataElement zip,
    DataElement county,
    DataElement telephone,
    DataElement email,
    // Identification Details
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

  public DataElements() {
    this(
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement(),
        new DataElement());
  }

  public record DataElement(
      boolean active,
      Double oddsRatio,
      Double logOdds) {

    public DataElement() {
      this(
          false,
          null,
          null);
    }
  }
}
