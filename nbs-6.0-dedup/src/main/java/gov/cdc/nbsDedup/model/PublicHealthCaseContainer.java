package gov.cdc.nbsDedup.model;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.model.dto.act.ActIdDto;
import gov.cdc.nbsDedup.model.dto.act.ActRelationshipDto;
import gov.cdc.nbsDedup.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.nbsDedup.model.dto.edx.EDXEventProcessDto;
import gov.cdc.nbsDedup.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.nbsDedup.model.dto.nbs.NbsActEntityDto;
import gov.cdc.nbsDedup.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.nbsDedup.model.dto.participation.ParticipationDto;
import gov.cdc.nbsDedup.model.dto.phc.CaseManagementDto;
import gov.cdc.nbsDedup.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.nbsDedup.model.dto.phc.PublicHealthCaseDto;
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
    public Collection<ParticipationDto> theParticipationDTCollection;
    public Collection<ActRelationshipDto> theActRelationshipDTCollection;
    public Collection<NbsActEntityDto> nbsCaseEntityCollection;
    public Collection<NbsCaseAnswerDto> nbsAnswerCollection;
    public Collection<EDXActivityDetailLogDto> edxPHCRLogDetailDTCollection;
    public Collection<EDXEventProcessDto> edxEventProcessDtoCollection;
    private String errorText;
    private boolean isCoinfectionCondition;

}
