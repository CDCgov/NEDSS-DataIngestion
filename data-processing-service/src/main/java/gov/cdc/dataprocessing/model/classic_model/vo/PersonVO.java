package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dto.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
public class PersonVO extends LdfBaseVO{
    public PersonDT thePersonDT = new PersonDT();
    public Collection<PersonNameDT> thePersonNameDTCollection=new ArrayList<>();
    public Collection<PersonRaceDT> thePersonRaceDTCollection=new ArrayList<>();
    public Collection<PersonEthnicGroupDT> thePersonEthnicGroupDTCollection=new ArrayList<>();
    public Collection<EntityLocatorParticipationDT> theEntityLocatorParticipationDTCollection = new ArrayList<>();
    public Collection<EntityIdDT> theEntityIdDTCollection = new ArrayList<>();


    public Collection<ParticipationDT> theParticipationDTCollection=new ArrayList<>();
    public Collection<RoleDT> theRoleDTCollection = new ArrayList<>();

    private String defaultJurisdictionCd;
//    private Boolean isExistingPatient;
    private boolean isExt = false;
    private boolean isMPRUpdateValid = true;
    private String localIdentifier;
    private String role;
    private String addReasonCode;

    /**
     * NEW VARIABLE
     * */

    private Boolean patientMatchedFound;

}
