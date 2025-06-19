package gov.cdc.nbs.deduplication.patient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
// Handles creating and managing patient data within NBS 6 tests
public class PatientManager {

  private final PersonManager personManager;
  private final PatientNameCreator nameCreator;

  public PatientManager(@Qualifier("nbsJdbcClient") final JdbcClient client) {
    this.personManager = new PersonManager(client);
    this.nameCreator = new PatientNameCreator(client);
  }

  public long createPatient() {
    return personManager.create();
  }

  public void addName(long patientId, PatientName name) {
    nameCreator.create(patientId, name);
  }

  public void markInactive(long patientId) {
    personManager.setInactive(patientId);
  }

}
