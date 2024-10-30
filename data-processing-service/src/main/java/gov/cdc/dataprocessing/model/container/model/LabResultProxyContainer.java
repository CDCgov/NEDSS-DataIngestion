package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
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
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public class LabResultProxyContainer extends PageActProxyContainer {
    @Serial
    private static final long serialVersionUID = 1L;
    public boolean associatedNotificationInd;
    private Long sendingFacilityUid;
    public boolean associatedInvInd=false;
    private Collection<ObservationContainer> theObservationContainerCollection = new ArrayList<>();
    private Collection<MaterialContainer> theMaterialContainerCollection = new ArrayList<>();
    private Collection<RoleDto> theRoleDtoCollection = new ArrayList<>();
    private Collection<Object> theActIdDTCollection;
    public Collection<Object> theInterventionVOCollection;
    public Collection<EDXDocumentDto> eDXDocumentCollection;
    private ArrayList<String> theConditionsList;
    private Collection<MessageLogDto> messageLogDCollection =null;
    private String labClia = null;
    private boolean manualLab = false;

    public LabResultProxyContainer() {
        theParticipationDtoCollection = new ArrayList<>();
        theOrganizationContainerCollection = new ArrayList<>();
    }

}
