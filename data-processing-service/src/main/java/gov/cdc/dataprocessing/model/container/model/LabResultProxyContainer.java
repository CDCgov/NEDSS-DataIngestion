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
@SuppressWarnings("all")
public class LabResultProxyContainer extends PageActProxyContainer {
    @Serial
    private static final long serialVersionUID = 1L;
    public boolean associatedNotificationInd;
    public boolean associatedInvInd = false;
    public Collection<Object> theInterventionVOCollection;
    public Collection<EDXDocumentDto> eDXDocumentCollection;
    private Long sendingFacilityUid;
    private Collection<ObservationContainer> theObservationContainerCollection = new ArrayList<>();
    private Collection<MaterialContainer> theMaterialContainerCollection = new ArrayList<>();
    private Collection<RoleDto> theRoleDtoCollection = new ArrayList<>();
    private Collection<Object> theActIdDTCollection;
    private ArrayList<String> theConditionsList;
    private Collection<MessageLogDto> messageLogDCollection = null;
    private String labClia = null;
    private boolean manualLab = false;

    public LabResultProxyContainer() {
        theParticipationDtoCollection = new ArrayList<>();
        theOrganizationContainerCollection = new ArrayList<>();
    }

}
