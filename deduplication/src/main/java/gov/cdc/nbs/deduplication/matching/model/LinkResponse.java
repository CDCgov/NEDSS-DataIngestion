package gov.cdc.nbs.deduplication.matching.model;

import java.util.List;

public record LinkResponse(
    String patient_reference_id,
    String person_reference_id,
    String match_grade, // certain, possible, certainly-not
    List<Results> results) {
  public record Results(
      String person_reference_id,
      Double accumulated_points,
      String pass_label,
      Double rms, // relative match strength 0 - 1
      Double mmt, // minimum match threshold 0 - 1
      Double cmt, // certain match threshold 0 - 1
      String match_grade) {
  }
}
