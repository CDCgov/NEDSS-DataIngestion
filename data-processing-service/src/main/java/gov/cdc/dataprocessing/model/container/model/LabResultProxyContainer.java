package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class LabResultProxyContainer extends PageActProxyContainer {
    private static final long serialVersionUID = 1L;
    public boolean associatedNotificationInd;
    private Long sendingFacilityUid;
    public boolean associatedInvInd=false;
    //private Collection<Object> thePersonVOCollection;
    private Collection<ObservationContainer> theObservationContainerCollection = new ArrayList<>();
    // private Collection<OrganizationContainer> theOrganizationContainerCollection = new ArrayList<>();
    private Collection<MaterialContainer> theMaterialContainerCollection = new ArrayList<>();
    //private Collection<ParticipationDto> theParticipationDtoCollection = new ArrayList<>();
    //  private Collection<Object> theActRelationshipDtoCollection;
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
