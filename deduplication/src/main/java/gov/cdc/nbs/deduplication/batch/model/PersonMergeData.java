package gov.cdc.nbs.deduplication.batch.model;

import java.util.Arrays;
import java.util.List;

public record PersonMergeData(
    String personUid,
    AdminComments adminComments,
    Ethnicity ethnicity,
    SexAndBirth sexAndBirth,
    Mortality mortality,
    GeneralPatientInformation generalPatientInformation,
    List<Investigation> investigations,
    List<Address> address,
    List<Telecom> telecom,
    List<Name> name,
    List<Identifier> identifiers,
    List<Race> race) {

  public record AdminComments(
      String date,
      String comment) {
  }

  // ETHNICITY Object
  public record Ethnicity(
      String asOfDate,
      String ethnicGroupDescription,
      String spanishOrigin,
      String ethnicUnknownReason) {
  }

  // SEX & BIRTH Object
  public record SexAndBirth(
      String asOfDate,
      String birthTime,
      String currentSexCode,
      String sexUnknownReason,
      String additionalGenderCode,
      String birthGenderCode,
      Boolean multipleBirthIndicator,
      Integer birthOrderNumber,
      String birthCityCode,
      String birthStateCode,
      String birthCountryCode,
      String preferredGender) {
  }

  // MORTALITY Object
  public record Mortality(
      String asOfDate,
      String deceasedIndicatorCode,
      String deceasedTime,
      String deathCity,
      String deathState,
      String deathCounty,
      String deathCountry) {
  }

  // GENERAL PATIENT INFORMATION Object
  public record GeneralPatientInformation(
      String asOfDate,
      String maritalStatusDescription,
      String mothersMaidenName,
      Integer adultsInHouseholdNumber,
      Integer childrenInHouseholdNumber,
      String occupationCode,
      String educationLevelDescription,
      String primaryLanguageDescription,
      String speaksEnglishCode,
      String stateHivCaseId) {
  }

  // INVESTIGATION Object
  public record Investigation(
      String investigationId,
      String startedOn,
      String condition) {
  }

  // ADDRESS Object
  public record Address(
      String id,
      String asOfDate,
      String useCode,
      List<String> line,
      String city,
      String state,
      String postalCode,
      String county,
      String censusTract,
      String country,
      String type,
      String comments) {
  }

  // TELECOM Object
  public record Telecom(
      String id,
      String asOfDate,
      String useCode,
      String countryCode,
      String phoneNumber,
      String extension,
      String email,
      String url,
      String type,
      String comments) {
  }

  // NAME Object
  public record Name(
      String personUid,
      String id,
      String asOfDate,
      List<String> given,
      String family,
      String secondFamily,
      String prefix,
      String suffix,
      String degree,
      String type) {
  }

  // IDENTIFIER
  public record Identifier(
      String id,
      String asOfDate,
      String value,
      String authority,
      String type) {
    public static final List<String> SUPPORTED_IDENTIFIERS = Arrays.asList(
        "AC", "ACSN", "AIN", "AM", "AMA", "AN", "ANC", "AND", "ANON", "ANT", "APRN", "ASID", "BA", "BC",
        "BCFN", "BCT", "BR", "BRN", "BSNR", "CAII", "CC", "CONM", "CY", "CZ", "DC", "DCFN", "DDS", "DEA",
        "DFN", "DI", "DL", "DN", "DO", "DP", "DPM", "DR", "DS", "DSG", "EI", "EN", "ESN", "FDR", "FDRFN",
        "FGN", "FI", "FILL", "GI", "GIN", "GL", "GN", "HC", "IND", "IRISTEM", "JHN", "LACSN", "LANR", "LI",
        "LN", "LR", "MA", "MB", "MC", "MCD", "MCN", "MCR", "MCT", "MD", "MI", "MR", "MRT", "MS", "NBSNR",
        "NCT", "NE", "NH", "NI", "NII", "NIIP", "NP", "NPI", "OBI", "OD", "PA", "PC", "PCN", "PE", "PEN",
        "PGN", "PHC", "PHE", "PHO", "PI", "PIN", "PLAC", "PN", "PNT", "PPIN", "PPN", "PRC", "PRN", "PT",
        "QA", "RI", "RN", "RPH", "RR", "RRI", "RRP", "SAMN", "SB", "SID", "SL", "SN", "SNBSN", "SNO", "SP",
        "SR", "SRX", "SS", "STN", "TAX", "TN", "TPR", "TRL", "U", "UDI", "UPIN", "USID", "VN", "VP", "VS",
        "WC", "WCN", "WP", "XV", "XX");
  }

  // RACE Object
  public record Race(
      String personUid,
      String id,
      String asOfDate,
      String category) {
  }
}
