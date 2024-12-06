package gov.cdc.dataprocessing.service.implementation.person.matching;

import java.util.List;

public record LinkResponse(
    String patient_reference_id,
    String person_reference_id,
    String prediction, // match, possible_match, no_match
    List<Results> results) {
  public record Results(
      String person_reference_id,
      Double belongingness_ratio) {
  }
}