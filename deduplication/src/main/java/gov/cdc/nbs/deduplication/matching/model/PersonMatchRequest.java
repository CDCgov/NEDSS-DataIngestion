package gov.cdc.nbs.deduplication.matching.model;

import java.sql.Timestamp;
import java.util.List;

public record PersonMatchRequest(
    PersonDto personDto,
    List<PersonNameDto> names,
    List<PersonRaceDto> races,
    List<PostalLocatorDto> postalLocators,
    List<TeleLocatorDto> teleLocators,
    List<EntityIdDto> identifications) {

  public record PersonDto(
      Timestamp birthTime,
      String currSexCd,
      String additionalGenderCd) {
  }

  public record PersonNameDto(
      String firstNm,
      String middleNm,
      String lastNm,
      String nmSuffix) {
  }

  public record PersonRaceDto(String raceCategoryCd) {
  }

  public record PostalLocatorDto(
      String streetAddr1,
      String streetAddr2,
      String cityDescTxt,
      String stateCd,
      String zipCd,
      String cntyCd) {
  }

  public record TeleLocatorDto(String phoneNbrTxt) {
  }

  public record EntityIdDto(
      String typeCd,
      String assigningAuthorityCd,
      String rootExtensionTxt) {
  }
}
