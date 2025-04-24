package gov.cdc.nbs.deduplication.matching.model;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PersonMatchRequest(
    PersonDto personDto,
    List<PersonNameDto> names,
    List<PersonRaceDto> races,
    List<PostalLocatorDto> postalLocators,
    List<TeleLocatorDto> teleLocators,
    List<EntityIdDto> identifications) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record PersonDto(
      Timestamp birthTime,
      String currSexCd,
      String additionalGenderCd) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record PersonNameDto(
      String firstNm,
      String middleNm,
      String lastNm,
      String nmSuffix) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record PersonRaceDto(String raceCategoryCd) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record PostalLocatorDto(
      String streetAddr1,
      String streetAddr2,
      String cityDescTxt,
      String stateCd,
      String zipCd,
      String cntyCd) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record TeleLocatorDto(String phoneNbrTxt) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record EntityIdDto(
      String typeCd,
      String assigningAuthorityCd,
      String rootExtensionTxt) {
  }
}
