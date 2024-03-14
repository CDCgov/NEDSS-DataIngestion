package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.CaseManagementDT;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXEventProcessDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PublicHealthCaseVO extends BaseContainer {
    private static final long serialVersionUID = 1L;
    //  private boolean itNew = false;
    // private boolean itDirty = true;
    private boolean isPamCase;
    private CaseManagementDT theCaseManagementDT = new CaseManagementDT();
    private PublicHealthCaseDT thePublicHealthCaseDT = new PublicHealthCaseDT();
    private Collection<Object> theConfirmationMethodDTCollection;
    private Collection<Object> theActIdDTCollection;
    public Collection<Object> theActivityLocatorParticipationDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theActRelationshipDTCollection;
    public Collection<Object> nbsCaseEntityCollection;
    public Collection<Object> nbsAnswerCollection;
    public Collection<EDXActivityDetailLogDto> edxPHCRLogDetailDTCollection;
    public Collection<EDXEventProcessDT> edxEventProcessDTCollection;


    private String errorText;
    private boolean isCoinfectionCondition;

}
