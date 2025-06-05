package gov.cdc.nbs.deduplication.batch.model;

import java.util.List;

public record PersonMergeData(
    String personUid,
    AdminComments adminComments,
    Ethnicity ethnicity,
    SexAndBirth sexAndBirth,
    Mortality mortality,
    GeneralPatientInformation general,
    List<Investigation> investigations,
    List<Address> addresses,
    List<PhoneEmail> phoneEmails,
    List<Name> names,
    List<Identification> identifications,
    List<Race> races) {

  public record AdminComments(
      String date,
      String comment) {
  }

  public record Ethnicity(
      String asOf,
      String ethnicity,
      String reasonUnknown,
      String spanishOrigin) {
    public Ethnicity() {
      this(
          null,
          null,
          null,
          null);
    }
  }

  public record SexAndBirth(
      String asOf,
      String dateOfBirth,
      String currentSex,
      String sexUnknown,
      String transgender,
      String additionalGender,
      String birthGender,
      String multipleBirth,
      String birthOrder,
      String birthCity,
      String birthState,
      String birthCounty,
      String birthCountry) {
    public SexAndBirth() {
      this(
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null);
    }
  }

  public record Mortality(
      String asOf,
      String deceased,
      String dateOfDeath,
      String deathCity,
      String deathState,
      String deathCounty,
      String deathCountry) {
    public Mortality() {
      this(
          null,
          null,
          null,
          null,
          null,
          null,
          null);
    }
  }

  public record GeneralPatientInformation(
      String asOf,
      String maritalStatus,
      String mothersMaidenName,
      String numberOfAdultsInResidence,
      String numberOfChildrenInResidence,
      String primaryOccupation,
      String educationLevel,
      String primaryLanguage,
      String speaksEnglish,
      String stateHivCaseId) {
    public GeneralPatientInformation() {
      this(
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          null);
    }
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

  public record Race(
      String personUid,
      String raceCode,
      String asOf,
      String race,
      String detailedRaces) {
  }
}
