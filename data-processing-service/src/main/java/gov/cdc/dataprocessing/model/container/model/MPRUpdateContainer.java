package gov.cdc.dataprocessing.model.container.model;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MPRUpdateContainer {
  private PersonContainer mpr = null;
  private Collection<PersonContainer> personVOs = null;

  /** This is the constructor for the class. */
  public MPRUpdateContainer(PersonContainer mpr, Collection<PersonContainer> personVOs) {
    this.mpr = mpr;
    this.personVOs = personVOs;
  }
}
