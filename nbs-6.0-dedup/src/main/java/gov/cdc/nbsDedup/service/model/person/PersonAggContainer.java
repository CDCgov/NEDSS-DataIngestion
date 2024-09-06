package gov.cdc.nbsDedup.service.model.person;

import gov.cdc.nbsDedup.model.container.model.PersonContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonAggContainer {
    PersonContainer personContainer;
    PersonContainer providerContainer;
}
