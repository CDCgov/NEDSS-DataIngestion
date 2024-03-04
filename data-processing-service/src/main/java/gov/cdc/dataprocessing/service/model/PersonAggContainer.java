package gov.cdc.dataprocessing.service.model;

import gov.cdc.dataprocessing.model.container.PersonContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonAggContainer {
    PersonContainer personContainer;
    PersonContainer providerContainer;
}
