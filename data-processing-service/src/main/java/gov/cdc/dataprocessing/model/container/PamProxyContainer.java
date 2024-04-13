package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ExportReceivingFacilityDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.NotificationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PamProxyContainer  extends BaseContainer{
    private static final long serialVersionUID = 1L;

    private PublicHealthCaseVO publicHealthCaseVO;

    private Collection<PersonContainer>  thePersonVOCollection;

    private BasePamContainer pamVO;

    private Collection<Object>  theVaccinationSummaryVOCollection;

    private Collection<Object>  theNotificationSummaryVOCollection;

    private Collection<Object>  theTreatmentSummaryVOCollection;

    private Collection<Object>  theLabReportSummaryVOCollection;

    private Collection<Object>  theMorbReportSummaryVOCollection;

    private Collection<ParticipationDto>  theParticipationDTCollection;

    private Collection<Object>  theInvestigationAuditLogSummaryVOCollection;

    private Collection<Object>  theOrganizationVOCollection;

    public Collection<Object>  theNotificationVOCollection;
    private boolean associatedNotificationsInd;

    private NotificationVO theNotificationVO;

    public Collection<Object>  theDocumentSummaryVOCollection;
    private boolean isOOSystemInd;
    private boolean isOOSystemPendInd;
    private Collection<Object> theCTContactSummaryDTCollection;

    private Collection<Object> nbsAttachmentDTColl;
    private Collection<Object> nbsNoteDTColl;

    private boolean isUnsavedNote;
    private ExportReceivingFacilityDT exportReceivingFacilityDT;
}
