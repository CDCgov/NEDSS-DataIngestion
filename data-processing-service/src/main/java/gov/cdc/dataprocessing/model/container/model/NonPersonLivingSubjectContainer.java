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
    public NonPersonLivingSubjectDto theNonPersonLivingSubjectDT = new NonPersonLivingSubjectDto();
    public Collection<Object> theEntityLocatorParticipationDTCollection;
    public Collection<Object> theEntityIdDTCollection;
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theRoleDTCollection;
}
