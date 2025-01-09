package gov.cdc.nbs.deduplication.algorithm.dataelements.model;

public record DataElementConfiguration(
    Double belongingnessRatio,
    DataElement firstName,
    DataElement lastName,
    DataElement suffix,
    DataElement birthDate,
    DataElement mrn,
    DataElement ssn,
    DataElement sex,
    DataElement gender,
    DataElement race,
    DataElement address,
    DataElement city,
    DataElement state,
    DataElement zip,
    DataElement county,
    DataElement telephone) {

  public record DataElement(
      Boolean active,
      Double m,
      Double u,
      Double logOdds,
      Double threshold) {
  }
}
