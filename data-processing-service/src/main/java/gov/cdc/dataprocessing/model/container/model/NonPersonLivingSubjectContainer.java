package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.NonPersonLivingSubjectDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
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
