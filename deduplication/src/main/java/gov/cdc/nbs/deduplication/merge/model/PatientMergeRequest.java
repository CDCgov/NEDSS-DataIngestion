package gov.cdc.nbs.deduplication.merge.model;

import java.util.List;

public record PatientMergeRequest(
    String survivingRecord,
    String adminComments,
    List<NameId> names,
    List<AddressId> addresses,
    List<PhoneEmailId> phoneEmails,
    List<IdentificationId> identifications,
    List<RaceId> races,
    String ethnicity,
    SexAndBirthFieldSource sexAndBirth,
    MortalityFieldSource mortality,
    GeneralInfoFieldSource generalInfo) {

  public record NameId(
      String personUid,
      String sequence) {
  }

  public record AddressId(String locatorId) {
  }

  public record PhoneEmailId(String locatorId) {
  }

  public record IdentificationId(
      String personUid,
      String sequence) {
  }

  public record RaceId(
      String personUid,
      String raceCode) {
  }

  public record SexAndBirthFieldSource(
      String asOf,
      String dateOfBirth,
      String currentSex, // unknown reason depends on currentSex
      String transgenderInfo,
      String additionalGender,
      String birthGender,
      String multipleBirth, // birth order depends on multiple birth
      String birthCity,
      String birthState, // birth county depends on birth state
      String birthCountry) {
  }

  public record MortalityFieldSource(
      String asOf,
      String deceased,
      String dateOfDeath,
      String deathCity,
      String deathState, // death county depends on death state
      String deathCountry) {
  }

  public record GeneralInfoFieldSource(
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
  }

}
