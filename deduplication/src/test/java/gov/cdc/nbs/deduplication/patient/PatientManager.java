package gov.cdc.nbs.deduplication.patient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class PatientManager {

  private final PatientCreator creator;
  private final PatientNameCreator nameCreator;

  public PatientManager(@Qualifier("nbsJdbcClient") final JdbcClient client) {
    this.creator = new PatientCreator(client);
    this.nameCreator = new PatientNameCreator(client);
  }

  public long createPatient() {
    return creator.create();
  }

  public void addName(long patientId, PatientName name) {
    nameCreator.create(patientId, name);
  }

}
