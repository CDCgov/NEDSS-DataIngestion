package gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.CaseManagementDT;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.dto.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXEventProcessDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
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
    private Collection<ConfirmationMethodDto> theConfirmationMethodDTCollection;
    private Collection<ActIdDto> theActIdDTCollection;
    public Collection<ActivityLocatorParticipationDto> theActivityLocatorParticipationDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<ParticipationDto> theParticipationDTCollection;
    public Collection<ActRelationshipDto> theActRelationshipDTCollection;
    public Collection<NbsActEntityDto> nbsCaseEntityCollection;
    public Collection<NbsCaseAnswerDto> nbsAnswerCollection;
    public Collection<EDXActivityDetailLogDto> edxPHCRLogDetailDTCollection;
    public Collection<EDXEventProcessDT> edxEventProcessDTCollection;


    private String errorText;
    private boolean isCoinfectionCondition;

}
