package gov.cdc.nbs.deduplication.merge.model;

import java.util.List;

public record PatientMergeRequest(
    String survivingRecord,
    String adminCommentsSource,
    List<NameId> names,
    List<AddressId> addresses,
    List<PhoneEmailId> phoneEmails,
    List<IdentificationId> identifications,
    List<RaceId> races
) {

  public record NameId(String personUid, String sequence) {}

  public record AddressId(String locatorId) {}

  public record PhoneEmailId(String locatorId) {}

  public record IdentificationId(String personUid, String sequence) {}

  public record RaceId(String personUid, String raceCode) {}

}
