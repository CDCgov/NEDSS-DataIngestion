package gov.cdc.nbs.deduplication.seed.model;

import java.util.List;

public record MpiResponse(List<Person> persons) {

  public record Person(String person_reference_id,
      String external_person_id,
      List<Patient> patients) {
  }

  public record Patient(
      String patient_reference_id,
      String external_patient_id) {
  }

}
