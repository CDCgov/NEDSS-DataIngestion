package gov.cdc.nbs.deduplication.batch.model;

import java.util.List;

public record PersonMergeData(
    String personUid,
    AdminComments adminComments,
    Ethnicity ethnicity,
    SexAndBirth sexAndBirth,
    Mortality mortality,
    GeneralPatientInformation generalPatientInformation,
    List<Investigation> investigations,
    List<Address> addresses,
    List<PhoneEmail> phoneEmails,
    List<Name> names,
    List<Identification> identifications,
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

  public record Address(
      String id,
      String asOf,
      String type,
      String use,
      String address,
      String address2,
      String city,
      String state,
      String zipcode,
      String county,
      String censusTract,
      String country,
      String comments) {
  }

  public record PhoneEmail(
      String id,
      String asOf,
      String type,
      String use,
      String countryCode,
      String phoneNumber,
      String extension,
      String email,
      String url,
      String comments) {
  }

  public record Name(
      String personUid,
      String sequence,
      String asOf,
      String type,
      String prefix,
      String first,
      String middle,
      String secondMiddle,
      String last,
      String secondLast,
      String suffix,
      String degree) {
  }

  public record Identification(
      String personUid,
      String sequence,
      String asOf,
      String type,
      String assigningAuthority,
      String value) {
  }

  // RACE Object
  public record Race(
      String personUid,
      String id,
      String asOfDate,
      String category) {
  }
}
