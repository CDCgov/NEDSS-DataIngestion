package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXDocumentDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.MessageLogDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.MaterialVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class LabResultProxyContainer extends PageActProxyVO {
    private static final long serialVersionUID = 1L;
    public boolean associatedNotificationInd;
    private Long sendingFacilityUid;
    public boolean associatedInvInd=false;
    //private Collection<Object> thePersonVOCollection;
    private Collection<ObservationVO> theObservationVOCollection = new ArrayList<>();
    private Collection<OrganizationVO> theOrganizationVOCollection = new ArrayList<>();
    private Collection<MaterialVO> theMaterialVOCollection = new ArrayList<>();
    private Collection<ParticipationDT> theParticipationDTCollection = new ArrayList<>();
    //  private Collection<Object> theActRelationshipDTCollection;
    private Collection<RoleDto> theRoleDtoCollection = new ArrayList<>();
    private Collection<Object> theActIdDTCollection;
    public Collection<Object> theInterventionVOCollection;
    public Collection<EDXDocumentDT> eDXDocumentCollection;
    private ArrayList<String> theConditionsList;
    private Collection<MessageLogDT> messageLogDCollection =null;


}
