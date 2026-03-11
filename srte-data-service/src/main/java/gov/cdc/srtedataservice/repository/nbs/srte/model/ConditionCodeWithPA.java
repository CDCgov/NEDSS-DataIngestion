package gov.cdc.srtedataservice.repository.nbs.srte.model;

import gov.cdc.srtedataservice.model.container.model.ProgramAreaContainer;
import jakarta.persistence.Entity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@SuppressWarnings("java:S3740")
public class ConditionCodeWithPA extends BaseConditionCode implements Serializable, Comparable {

  // Constructors, getters, and setters
  private String stateProgAreaCode;
  private String stateProgAreaCdDesc;

  @Override
  public int compareTo(Object o) {
    return getConditionShortNm().compareTo(((ProgramAreaContainer) o).getConditionShortNm());
  }
}
