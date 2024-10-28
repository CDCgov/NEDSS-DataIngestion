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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
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
