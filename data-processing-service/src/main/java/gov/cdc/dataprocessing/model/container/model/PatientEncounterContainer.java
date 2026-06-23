package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.PatientEncounterDto;
import java.io.Serializable;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientEncounterContainer extends BaseContainer implements Serializable {
  private static final long serialVersionUID = 1L;
  public PatientEncounterDto thePatientEncounterDT = new PatientEncounterDto();
  public Collection<Object> theActivityLocatorParticipationDTCollection;
  public Collection<Object> theActIdDTCollection;
  public Collection<Object> theParticipationDTCollection;
  public Collection<Object> theActRelationshipDTCollection;
}
