package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.ExportReceivingFacilityDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PamProxyContainer  extends BaseContainer {
    private static final long serialVersionUID = 1L;

    private PublicHealthCaseContainer publicHealthCaseContainer;

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

    private NotificationContainer theNotificationContainer;

    public Collection<Object>  theDocumentSummaryVOCollection;
    private boolean isOOSystemInd;
    private boolean isOOSystemPendInd;
    private Collection<Object> theCTContactSummaryDTCollection;

    private Collection<Object> nbsAttachmentDTColl;
    private Collection<NbsNoteDto> nbsNoteDTColl;

    private boolean isUnsavedNote;
    private ExportReceivingFacilityDto exportReceivingFacilityDto;
}
