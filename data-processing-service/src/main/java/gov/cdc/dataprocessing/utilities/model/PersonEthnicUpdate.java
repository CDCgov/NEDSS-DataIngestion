package gov.cdc.dataprocessing.utilities.model;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PersonEthnicUpdate {
    private List<PersonEthnicGroup> personEthnicGroupList = new ArrayList<>();
    private List<PersonEthnicGroup> personEthnicGroupMprList = new ArrayList<>();
}
