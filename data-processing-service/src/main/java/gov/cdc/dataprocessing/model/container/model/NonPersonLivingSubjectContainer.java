package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.NonPersonLivingSubjectDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class NonPersonLivingSubjectContainer  extends BaseContainer {
    private static final long serialVersionUID = 1L;
    //   private Boolean itDirty = false; // defined in AbstractVO
//   private Boolean itNew = true; // defined in AbstractVO
    public NonPersonLivingSubjectDto theNonPersonLivingSubjectDT = new NonPersonLivingSubjectDto();
    public Collection<Object> theEntityLocatorParticipationDTCollection;
    public Collection<Object> theEntityIdDTCollection;
    //collections for role and participation object association added by John Park
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theRoleDTCollection;
}
