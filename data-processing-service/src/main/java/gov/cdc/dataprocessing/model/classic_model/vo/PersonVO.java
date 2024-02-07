package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dto.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
public class PersonVO extends LdfBaseVO{
    private static final long serialVersionUID = 1L;
    public PersonDT thePersonDT = new PersonDT();
    public Collection<PersonNameDT> thePersonNameDTCollection=new ArrayList<>();
    public Collection<Object> thePersonRaceDTCollection;
    public Collection<Object> thePersonEthnicGroupDTCollection;
    public Collection<EntityLocatorParticipationDT> theEntityLocatorParticipationDTCollection = new ArrayList<>();
    public Collection<EntityIdDT> theEntityIdDTCollection = new ArrayList<>();


    //	private String custom;//custom queues
    //collections for role and participation object association added by John Park
    public Collection<Object> theParticipationDTCollection;
    public Collection<RoleDT> theRoleDTCollection = new ArrayList<>();

    private String defaultJurisdictionCd;
    private Boolean isExistingPatient;
    private boolean isExt = false;
    private boolean isMPRUpdateValid = true;
    private String localIdentifier;
    private String role;
    private String addReasonCode;


}
