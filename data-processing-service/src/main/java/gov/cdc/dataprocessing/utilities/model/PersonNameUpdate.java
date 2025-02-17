package gov.cdc.dataprocessing.utilities.model;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PersonNameUpdate {
    private List<PersonName> domainList = new ArrayList<>();
    private List<PersonName> domainListMpr = new ArrayList<>();
}
