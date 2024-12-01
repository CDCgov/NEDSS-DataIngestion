package gov.cdc.nbs.mpidatasyncer.model;

import gov.cdc.nbs.mpidatasyncer.entity.nbs.Person;
import java.io.Serializable;
import java.util.List;

public record PersonBatchDto(boolean isNew, List<Person> batch) implements Serializable {
  private static final long serialVersionUID = 1L;
}
