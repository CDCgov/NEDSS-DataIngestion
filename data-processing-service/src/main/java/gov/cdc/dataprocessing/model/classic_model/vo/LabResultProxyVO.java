package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dto.MessageLogDT;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class LabResultProxyVO extends PageActProxyVO {
    private static final long serialVersionUID = 1L;
    public boolean associatedNotificationInd;
    private Long sendingFacilityUid;
    public boolean associatedInvInd=false;
    //private Collection<Object> thePersonVOCollection;
    private Collection<ObservationVO> theObservationVOCollection;
    private Collection<Object> theOrganizationVOCollection = new ArrayList<>();
    private Collection<Object> theMaterialVOCollection;
    //private Collection<Object> theParticipationDTCollection;
    //  private Collection<Object> theActRelationshipDTCollection;
    private Collection<Object> theRoleDTCollection;
    private Collection<Object> theActIdDTCollection;
    public Collection<Object> theInterventionVOCollection;
    public Collection<Object> eDXDocumentCollection;
    private ArrayList<String> theConditionsList;
    private Collection<MessageLogDT> messageLogDCollection =null;


}
