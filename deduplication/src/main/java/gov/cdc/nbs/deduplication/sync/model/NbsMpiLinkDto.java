package gov.cdc.nbs.deduplication.sync.model;

import gov.cdc.nbs.deduplication.seed.model.MpiResponse;


public record NbsMpiLinkDto(
    String personReferenceId,
    String externalPersonId,
    String patientReferenceId,
    String externalPatientId
) {
  public NbsMpiLinkDto(MpiResponse mpiResponse) {
    this(
        mpiResponse.persons().getFirst().person_reference_id(),
        mpiResponse.persons().getFirst().external_person_id(),
        mpiResponse.persons().getFirst().patients().getFirst().patient_reference_id(),
        mpiResponse.persons().getFirst().patients().getFirst().external_patient_id()
    );
  }

  public NbsMpiLinkDto(String personReferenceId, String externalPersonId, MpiPatientResponse patientResponse) {
    this(
        personReferenceId,
        externalPersonId,
        patientResponse.patient_reference_id(),
        patientResponse.external_patient_id()
    );
  }
}
