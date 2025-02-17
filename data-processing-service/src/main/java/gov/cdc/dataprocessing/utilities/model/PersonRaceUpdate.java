package gov.cdc.dataprocessing.utilities.model;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PersonRaceUpdate {
    private List<PersonRace> personRaceList = new ArrayList<>();
    private List<PersonRace> personRaceMprList = new ArrayList<>();
    private List<PersonRace> personRaceDeleteList = new ArrayList<>();


    /**
     * deleteInactivePersonRace(retainingRaceCodeList, patientUid, parentUid);
     * */
    private List<String> retainingRaceCodeListForDeletion;
    private Long patientUidForDeletion;
    private Long parentUidForDeletion;

}
