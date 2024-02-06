package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dto.PersonDT;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
public class PersonVO extends LdfBaseVO{
    private static final long serialVersionUID = 1L;
    // private boolean itDirty = false;
    // private boolean itNew = true;
    // private boolean itDelete = false;
    public PersonDT thePersonDT = new PersonDT();
    public Collection<Object> thePersonNameDTCollection=new ArrayList<Object>();
    public Collection<Object> thePersonRaceDTCollection;
    public Collection<Object> thePersonEthnicGroupDTCollection;
    public Collection<Object> theEntityLocatorParticipationDTCollection;
    public Collection<Object> theEntityIdDTCollection;


    //	private String custom;//custom queues
    //collections for role and participation object association added by John Park
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theRoleDTCollection;

    private String defaultJurisdictionCd;
    private Boolean isExistingPatient;
    private boolean isExt = false;
    private boolean isMPRUpdateValid = true;
    private String localIdentifier;
    private String role;
    private String addReasonCode;


}
