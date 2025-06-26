package gov.cdc.nbs.deduplication.merge.model;

import java.util.List;

public record PatientMergeRequest(
    String survivingRecord,
    String adminCommentsSource,
    List<NameId> names,
    List<AddressId> addresses,
    List<PhoneEmailId> phoneEmails,
    List<IdentificationId> identifications,
    List<RaceId> races,
    String ethnicitySource,
    SexAndBirthFieldSource sexAndBirthFieldSource,
    MortalityFieldSource mortalityFieldSource,
    GeneralInfoFieldSource generalInfoFieldSource
) {

  public record NameId(String personUid, String sequence) {
  }


  public record AddressId(String locatorId) {
  }


  public record PhoneEmailId(String locatorId) {
  }


  public record IdentificationId(String personUid, String sequence) {
  }


  public record RaceId(String personUid, String raceCode) {
  }


  public record SexAndBirthFieldSource(
      String asOfSource,
      String dateOfBirthSource,
      String currentSexSource,
      String sexUnknownSource,
      String transgenderSource,
      String additionalGenderSource,
      String birthGenderSource,
      String multipleBirthSource,
      String birthOrderSource,
      String birthAddressSource
  ) {
  }


  public record MortalityFieldSource(
      String asOfSource,
      String deceasedSource,
      String dateOfDeathSource,
      String deathAddressSource
  ) {
  }


  public record GeneralInfoFieldSource(
      String asOfSource,
      String maritalStatusSource,
      String mothersMaidenNameSource,
      String numberOfAdultsInResidenceSource,
      String numberOfChildrenInResidenceSource,
      String primaryOccupationSource,
      String educationLevelSource,
      String primaryLanguageSource,
      String speaksEnglishSource,
      String stateHivCaseIdSource
  ) {
  }

}
