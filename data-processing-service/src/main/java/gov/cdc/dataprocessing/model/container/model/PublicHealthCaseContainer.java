package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter

public class PublicHealthCaseContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private boolean isPamCase;
    private CaseManagementDto theCaseManagementDto = new CaseManagementDto();
    private PublicHealthCaseDto thePublicHealthCaseDto = new PublicHealthCaseDto();
    private Collection<ConfirmationMethodDto> theConfirmationMethodDTCollection;
    private Collection<ActIdDto> theActIdDTCollection;
    public Collection<ActivityLocatorParticipationDto> theActivityLocatorParticipationDTCollection;
    //Collections added for Participation and Activity Relationship object association
    public Collection<ParticipationDto> theParticipationDTCollection;
    public Collection<ActRelationshipDto> theActRelationshipDTCollection;
    public Collection<NbsActEntityDto> nbsCaseEntityCollection;
    public Collection<NbsCaseAnswerDto> nbsAnswerCollection;
    public Collection<EDXActivityDetailLogDto> edxPHCRLogDetailDTCollection;
    public Collection<EDXEventProcessDto> edxEventProcessDtoCollection;


    private String errorText;
    private boolean isCoinfectionCondition;

}
