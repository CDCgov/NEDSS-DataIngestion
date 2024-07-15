package gov.cdc.dataprocessing.service.model.person;

import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class PersonAggContainer {
    PersonContainer personContainer;
    PersonContainer providerContainer;
}
